import pika
import uuid
import json

# 💡 STAP 1: Voeg latitude en longitude toe aan de functie
def generate_ticket(event_name, location, latitude, longitude, date, start_time, heat, status, expected_g_forces, qr_code_data):
    ticket = {
        "id": str(uuid.uuid4()),
        "eventName": event_name,
        "location": location,
        "latitude": latitude,   # 💡 Toegevoegd voor Mapbox Android
        "longitude": longitude, # 💡 Toegevoegd voor Mapbox Android
        "date": date,
        "startTime": start_time,
        "heat": heat,
        "status": status,
        "expectedGForces": expected_g_forces,
        "qrCodeData": qr_code_data
    }
    return ticket

def publish_tickets():
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        host='messagebroker.device.dev',
        port=5672,
        credentials=pika.PlainCredentials('guest', 'guest')
    ))
    channel = connection.channel()
    
    # Zorg dat de queue bestaat
    channel.queue_declare(queue='tickets-queue', durable=True)
    
    # 💡 STAP 2: Geef de echte GPS-coördinaten mee per circuit
    tickets = [
        generate_ticket(
            "Track Day Nürburgring", 
            "Nürburgring Nordschleife",
            50.3341, 6.9427, # Lat, Lng voor Nürburgring
            "2026-05-15", "10:00", 1, "Aankomende Heats", "1.8G", "NURB-2026-H1-XYZ"
        ),
        generate_ticket(
            "Laguna Seca Open Pit", 
            "Laguna Seca",
            36.5841, -121.7529, # Lat, Lng voor Laguna Seca
            "2026-06-20", "14:00", 2, "Aankomende Heats", "1.5G", "LAG-2026-H2-ABC"
        ),
        generate_ticket(
            "Zandvoort Time Attack", 
            "Circuit Zandvoort",
            52.3888, 4.5409, # Lat, Lng voor Zandvoort
            "2026-03-01", "09:00", 1, "Resultaten", "1.4G", "ZAND-2026-H1-RES"
        ),
        generate_ticket(
            "Zolder Track Day", 
            "Circuit Zolder",
            50.9889, 5.2581, # Lat, Lng voor Zolder
            "2026-08-10", "13:00", 1, "Aankomende Heats", "1.3G", "ZOLD-2026-H1-ABC"
        ),
        generate_ticket(
            "Spa-Francorchamps Sprint", 
            "Circuit de Spa-Francorchamps",
            50.4372, 5.9714, # Lat, Lng voor Spa
            "2026-09-12", "11:30", 2, "Aankomende Heats", "1.6G", "SPA-2026-H2-DEF"
        ),
        generate_ticket(
            "Assen Performance Run", 
            "TT Circuit Assen",
            52.9585, 6.5224, # Lat, Lng voor Assen
            "2026-10-04", "15:00", 1, "Aankomende Heats", "1.4G", "ASSEN-2026-H1-GHI"
        )
    ]

    for ticket in tickets:
        channel.basic_publish(
            exchange='',
            routing_key='tickets-queue',
            body=json.dumps(ticket),
            properties=pika.BasicProperties(
                delivery_mode=2,  # Maak berichten persistent
                content_type='application/json'
            )
        )
        print(f"✔ Ticket published for: {ticket['eventName']} (Heat {ticket['heat']})")

    connection.close()

if __name__ == "__main__":
    publish_tickets()