FROM python:3.12.11-slim-bookworm

WORKDIR /app

COPY main.py .

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY model/trained_model.pkl model/

COPY . .

EXPOSE 8086

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8086"]