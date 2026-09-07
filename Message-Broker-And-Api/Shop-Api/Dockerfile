FROM python:3.11-slim

WORKDIR /app

# Installeer FastAPI en de Uvicorn webserver
RUN pip install --no-cache-dir fastapi uvicorn

COPY main.py .

# Start de server op poort 8080
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]