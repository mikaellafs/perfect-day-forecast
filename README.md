# Perfect day forecast
The system has been developed for learning purposes. It is designed to provide users recommendations for optimizing outdoor plans based on real-time weather data. The system's primary goal is to suggest the best day, if possible, within a given date range, considering preferred weather conditions and a specific location. It leverages RabbitMQ for communication between the data analysis and data collection services.

## Tech stack
-   Python with Flask (front-end)
-   Kotlin (back-end)
-   Docker
-   Docker Compose
-   Redis
-   Postgresql
-   Prometheus
-   Grafana

## Running the applications
Before anything, it's required to up the rabbitmq, database and redis servers. In the root directory:
```bash
docker-compose up -d
```
### web-app
Frontend of the system and runs on port 5000:
```bash
python3 src/app.py
```
### services
In this directory, you'll find the data analyzer and data collector services written in Kotlin. You can run these services using the following commands:
```bash
./gradlew a:data-collector:r
./gradlew a:data-analyzer:r
```
Or you can run them inside a container, in the project root:
```bash
docker-compose -f docker-compose-services up -d
```
### Prometheus and grafana
To observe the application, up prometheus and grafana containers.
```bash
docker-compose -f docker-compose-metrics up -d
```
