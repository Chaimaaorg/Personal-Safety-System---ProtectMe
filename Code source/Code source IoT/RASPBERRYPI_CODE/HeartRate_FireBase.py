import serial

import pyrebase
import serial
import pynmea2


firebaseConfig={


  "apiKey": "AIzaSyAkqXyhbmq2tv6gUROPJ8dTUWyE9QAPS8A",
  "authDomain": "personalsafetysystem-6d09f.firebaseapp.com",
  "databaseURL": "https://personalsafetysystem-6d09f-default-rtdb.firebaseio.com",
  "projectId": "personalsafetysystem-6d09f",
  "storageBucket": "personalsafetysystem-6d09f.appspot.com",
  "messagingSenderId": "940398198808",
  "appId": "1:940398198808:web:3f3131cc1e78fd204a436d"
    }
firebase=pyrebase.initialize_app(firebaseConfig)
db=firebase.database()


if __name__ == '__main__':
    ser = serial.Serial('/dev/ttyACM0', 9600, timeout=1)
    ser.reset_input_buffer()
    while True:
        if ser.in_waiting > 0:
            line = ser.readline().decode('utf-8').rstrip()
            dataa = {'HEART': { 'heartbeat': line }}
            db.update(dataa)
            print("Data sent")
            print(line)

