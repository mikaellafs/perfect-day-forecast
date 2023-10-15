from sqlalchemy import Column, Integer, String, DateTime, create_engine, MetaData, Table, inspect
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError

import os

requests_table_name = "requests"

Base = declarative_base()
engine = create_engine(os.getenv("DB_URL"))
Session = sessionmaker(bind=engine)

if not inspect(engine).has_table(requests_table_name):
    # If the table does not exist, create it
    metadata = MetaData()
    basic_table = Table(requests_table_name, metadata,
        Column('id', Integer, primary_key=True),
        Column('user', String),
        Column('location', String),
        Column('start_date', DateTime),
        Column('end_date', DateTime)
    )

    metadata.create_all(engine)

class Request(Base):
    __tablename__ = requests_table_name
    id = Column(Integer, primary_key=True)
    user = Column(String)
    location = Column(String)
    start_date = Column(DateTime)
    end_date = Column(DateTime)

def save_request_to_db(request: Request):
    session = Session(expire_on_commit=False)
    try:
        session.add(request)
        session.commit()
    except SQLAlchemyError as e:
        print(f"An error occurred: {e}")
        session.rollback()
        raise Exception("An error occurred while saving the request")
    finally:
        session.close()