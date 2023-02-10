# ProtectMe - Personal Safety App

## Overview
**ProtectMe** is a personal safety and medical emergency application designed to ensure the safety and well-being of users. The app integrates IoT (Internet of Things) technology with a mobile application to provide real-time location tracking, emergency alerts, and heart rate monitoring. It is developed for both **Android** and **iOS** platforms using **Flutter** and **Dart**, with backend services powered by **Firebase**.

The app allows users to:
- Send emergency alerts to predefined contacts with their real-time location.
- Monitor heart rate using IoT sensors.
- Locate nearby emergency services such as hospitals, police stations, pharmacies, and medical clinics.
- Provide detailed directions and transportation options to reach these services.

---

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Architecture](#architecture)
5. [Installation](#installation)
6. [Usage](#usage)
7. [Demo](#demo)
8. [Contributors](#contributors)
9. [License](#license)

---

## Introduction
In today's world, personal safety is a major concern, especially for vulnerable individuals such as women, children, and the elderly. **ProtectMe** aims to provide a reliable and portable solution for personal safety and medical emergencies. The app combines IoT devices with a mobile application to offer real-time tracking, emergency alerts, and heart rate monitoring.

The app is designed to:
- Send instant alerts to trusted contacts in case of danger.
- Continuously monitor the user's heart rate and detect anomalies.
- Provide real-time location sharing with emergency contacts.
- Recommend nearby emergency services and provide directions.

---

## Features
### For the Primary User:
1. **Emergency Alert**: Send instant alerts to predefined contacts with the user's real-time location.
2. **Heart Rate Monitoring**: Continuously monitor the user's heart rate using IoT sensors.
3. **Nearby Services**: Locate nearby hospitals, police stations, pharmacies, and medical clinics.
4. **Real-Time Location Tracking**: Share the user's real-time location with emergency contacts.
5. **Contact Management**: Add, edit, or remove emergency contacts.
6. **Profile Management**: Update personal information and view health metrics (e.g., heart rate, steps).

### For Emergency Contacts:
1. **Real-Time Location Tracking**: View the primary user's real-time location on a map.
2. **Emergency Notifications**: Receive instant alerts when the primary user triggers an emergency.
3. **Contact Management**: View and manage the list of users who have added them as emergency contacts.

---

## Technologies Used
### Frontend:
- **Flutter**: For cross-platform mobile app development.
- **Dart**: The programming language used for Flutter.

### Backend:
- **Firebase**: For user authentication, real-time database, and cloud storage.
- **Firestore**: NoSQL database for real-time data synchronization.
- **Foursquare API**: For locating nearby emergency services (hospitals, police stations, etc.).
- **Google Maps API**: For real-time location tracking and directions.

### IoT Components:
- **Arduino UNO**: For heart rate monitoring and data collection.
- **Raspberry Pi 4**: For GPS tracking and emergency alert triggering.
- **GPS Module**: For real-time location tracking.
- **Heart Rate Sensor**: For continuous heart rate monitoring.
- **LCD Display**: For displaying heart rate data.

### Programming Languages:
- **Java**: For Android app development.
- **Python**: For IoT device programming (GPS and button press).
- **C++**: For Arduino programming (heart rate sensor).

---

## Architecture
The app follows the **MVC (Model-View-Controller)** architecture:
1. **Model**: Handles data and business logic (e.g., user data, heart rate, location).
2. **View**: Manages the user interface (UI) and displays data to the user.
3. **Controller**: Acts as an intermediary between the Model and View, processing user interactions and updating the UI.

### Physical Architecture:
- **IoT Devices**: Arduino and Raspberry Pi for heart rate monitoring and GPS tracking.
- **Mobile App**: Flutter-based app for user interaction and data visualization.
- **Backend**: Firebase for real-time data storage and synchronization.

---

## Installation
### Prerequisites:
- Flutter SDK installed on your machine.
- Android Studio or Xcode for running the app.
- Firebase account for backend services.
- Arduino IDE and Raspberry Pi setup for IoT components.

### Steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/protectme.git
   ```
2. Navigate to the project directory:
   ```bash
   cd protectme
   ```
3. Install dependencies:
   ```bash
   flutter pub get
   ```
4. Configure Firebase:
   - Create a Firebase project and add the `google-services.json` file to the `android/app` directory.
   - Enable Firebase Authentication, Firestore, and Realtime Database.
5. Run the app:
   ```bash
   flutter run
   ```

---

## Usage
1. **Sign Up/Login**: Create an account or log in using your email and password.
2. **Add Emergency Contacts**: Add trusted contacts who will receive emergency alerts.
3. **Trigger Emergency Alert**: Press the emergency button to send an alert with your real-time location.
4. **Monitor Heart Rate**: Use the IoT device to monitor your heart rate in real-time.
5. **Find Nearby Services**: Locate nearby hospitals, police stations, pharmacies, and medical clinics.
6. **Track Location**: Share your real-time location with emergency contacts.

---

## Demo
The folder Démo contains demo videos showcasing the app's features

---

## Contributors
- **AARAB YOUSRA**
- **ZARKTOUNI ISMAIL**
- **OURGANI CHAIMAA**
- **ElKAMOUNI HAJAR**

---

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

---

## References
- [Firebase Documentation](https://firebase.google.com/docs)
- [Foursquare API Documentation](https://developer.foursquare.com/)
- [Google Maps API Documentation](https://developers.google.com/maps/documentation)
- [Arduino Documentation](https://www.arduino.cc/en/Guide)
- [Raspberry Pi Documentation](https://www.raspberrypi.org/documentation/)

---

For any questions or issues, please contact the development team.