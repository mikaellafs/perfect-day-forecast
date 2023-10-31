import pika
import os
import json

def push_to_rabbitmq(user, location, start_date, end_date, weather_preference):
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        host= os.getenv("RABBIT_HOST"),
        port= os.getenv("RABBIT_PORT"),  
        # credentials=pika.PlainCredentials('username', 'password')
        ))
    channel = connection.channel()
    channel.queue_declare(queue=os.getenv("REQUESTS_QUEUE"))
    channel.basic_publish(exchange=os.getenv("REQUESTS_EXCHANGE"), routing_key='42', body=json.dumps({
        "request_id": user,
        "location": location,
        "start_date": start_date,
        "end_date": end_date,
        "weather_preference": weather_preference
    }))
    print(json.dumps({
        "request_id": user,
        "location": location,
        "start_date": start_date,
        "end_date": end_date,
        "weather_preference": weather_preference
    }))
    connection.close()