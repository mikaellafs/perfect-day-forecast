from dotenv import load_dotenv

load_dotenv()

from flask import Flask, request,render_template, jsonify
from models.request import Request, save_request_to_db
from rabbit.push import push_to_rabbitmq

app = Flask(__name__)

# List of locations available
locations = ["New York", "Los Angeles", "London", "Paris", "Tokyo", "Sydney", "Rio de Janeiro"]

# List of weather conditions
weather_conditions = {
    "sunny": "Sunny",
    "rainy": "Rainy",
    "cloudy": "Cloudy",
    "windy": "Windy"
}

@app.route("/")
def main():
    return render_template("index.html", locations=locations, weather_conditions=weather_conditions)

@app.route("/send_request", methods=["POST"])
def echo_input():
    user = request.form.get("username")
    location = request.form.get("location")
    start_date = request.form.get("start_date")
    end_date = request.form.get("end_date")
    weather_condition = request.form.get("weather_condition")

    req = Request(user=user, location=location, start_date=start_date, end_date=end_date)
    try:
        save_request_to_db(req)
        push_to_rabbitmq(req.id, location=location, start_date=start_date, end_date=end_date)
    except Exception as e:
        return jsonify(error=e.args[0]), 500

    return f"What is the most likely day to be {weather_condition} between {start_date} and {end_date} in {location}? Wait..."

app.run()