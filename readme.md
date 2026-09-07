# 🎬 Apex Track Day

**Apex Track Day** is an Android application for motorsport participants attending track days.

The app retrieves track day registrations from a message broker, stores them locally, and provides drivers with important updates such as payment reminders, session reminders, and real-time track day alerts.

It also includes extra features designed to improve the driver experience on track, including live telemetry, G-force monitoring, speed tracking, track temperature, location-based geofencing, auto-flashlight triggers, a performance shop with build list sharing, and RabbitMQ-based order publishing.

## Main Features

- Receive track day registrations from RabbitMQ
- Store registrations locally in Room
- Display upcoming and past track days in a race calendar
- Show QR codes for track day entry
- Schedule reminder notifications before sessions start
- Display detailed track day information
- Retrieve performance parts via Retrofit
- Store shop items locally for offline access
- Add parts to a build list / cart
- Share build list via Android intents
- Send shop orders to the message broker
- Display Mapbox map with track layout
- Show user location on the map
- Detect entering and exiting track grounds using geofencing
- Display live telemetry data
- Show G-force data from the accelerometer
- Track speed and average speed
- Retrieve track temperature via weather API
- Detect light intensity
- Detect movement
- Toggle flashlight manually
- Auto-enable flashlight when entering paddock area
- Auto-enable flashlight in low-light conditions
- Auto-enable flashlight on movement detection
- Use WorkManager for background notifications
- Use AlarmManager for exact session reminders
- Support deep-linking from notifications to relevant screens

## Tech Stack

- Kotlin
- Android
- Room
- Retrofit
- Moshi
- Hilt
- Coroutines
- Lifecycle
- Navigation Component
- WorkManager
- AlarmManager
- RabbitMQ / AMQP
- Mapbox
- Sensors
- CameraManager

## Architecture

The app uses a clean architecture approach with:

- UI layer
- ViewModel layer
- Repository layer
- Domain interfaces
- Data implementations
- Hilt dependency injection
- Separated sensor, location, weather, ticket, and shop repositories

Network communication is standardized using **Moshi**, and sensitive configuration values are stored in `BuildConfig`.

## Backend / Message Broker Setup

To start the API and message broker:

```bash
cd messagebroker/api
docker compose up
