from sqlalchemy import Column, Integer, String, DateTime, create_engine, MetaData, Table, inspect
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError

import os

from enum import Enum

# Define an enumeration class
class RequestStatus(Enum):
    IN_PROGRESS = "in progress"
    DONE = "done"

class Preference(Enum):
    CLEAR_SKY = "clear sky"
    CLOUDY = "cloudy"
    FOGGY = "foggy"
    RAINY = "rainy"
    SNOWY = "snowy"

requests_table_name = "requests"

Base = declarative_base()
engine = create_engine(os.getenv("DB_URL"))
Session = sessionmaker(bind=engine)

if not inspect(engine).has_table(requests_table_name):
    # If the table does not exist, create it
    print("Creating requests table")
    metadata = MetaData()
    basic_table = Table(requests_table_name, metadata,
        Column('id', Integer, primary_key=True),
        Column('user', String),
        Column('location', String),
        Column('start_date', DateTime),
        Column('end_date', DateTime),
        Column('status', String),
        Column('weather_preference', String),
        Column('best_day_result', DateTime)
    )

    metadata.create_all(engine)

class Request(Base):
    __tablename__ = requests_table_name
    id = Column(Integer, primary_key=True)
    user = Column(String)
    location = Column(String)
    start_date = Column(DateTime)
    end_date = Column(DateTime)
    status = Column(String)
    weather_preference = Column(String)
    best_day_result = Column(DateTime)

def save_request_to_db(request: Request):
    session = Session(expire_on_commit=False)

    # Check if a request with the same username and overlapping date range is in progress
    overlapping_requests = session.query(Request).filter(
        Request.user == request.user,
        Request.status == RequestStatus.IN_PROGRESS.value,
        Request.start_date == request.start_date,
        Request.end_date == request.end_date,
        Request.weather_preference == request.weather_preference,
        Request.location == request.location
    ).all()

    if overlapping_requests:
        # If there are overlapping requests in progress, raise an exception
        raise Exception("Request already in progress")

    # If no overlapping requests, update the status of the new request
    request.status = "in progress"

    try:
        session.add(request)
        session.commit()
    except SQLAlchemyError as e:
        print(f"An error occurred: {e}")
        session.rollback()
        raise Exception("An error occurred while saving the request")
    finally:
        session.close()

def get_requests_from_user(user: String):
    session = Session(expire_on_commit=False)
    
    try:
        requests = session.query(Request).filter(Request.user == user).all()
        return requests
    except SQLAlchemyError as e:
        print(f"An error occurred: {e}")
        raise Exception("An error occurred while querying requests")
    finally:
        session.close()
