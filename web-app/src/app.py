from flask import Flask, request,render_template

app = Flask(__name__)

# List of locations available
locations = ["Sao Paulo", "Rio de Janeiro", "Vitória"]

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


@app.route("/echo_user_input", methods=["POST"])
def echo_input():
    location = request.form.get("location")
    start_date = request.form.get("start_date")
    end_date = request.form.get("end_date")
    weather_condition = request.form.get("weather_condition")

    return f"What is the most likely day to be {weather_condition} between {start_date} and {end_date} in {location}? Wait..."