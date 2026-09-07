import pika
import time

connection = pika.BlockingConnection(pika.ConnectionParameters(
    host='messagebroker.device.dev',
    port=5672,
    credentials=pika.PlainCredentials('guest', 'guest')
))
channel = connection.channel()

# Queues aanmaken voor Apex Track Day App
channel.queue_declare(queue='tickets-queue', durable=True)
channel.queue_declare(queue='orders-queue', durable=True)

print("Queues aangemaakt.")
connection.close()
