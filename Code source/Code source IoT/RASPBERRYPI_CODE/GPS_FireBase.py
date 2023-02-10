import pyrebase
import serial
import pynmea2

'''firebaseConfig={

  "apiKey": "AIzaSyCylJnfn_jhkMrXa7y9KHxVzsgj46KU07o",
  "authDomain": "gps-tracker-9d072.firebaseapp.com",
  "databaseURL": "https://gps-tracker-9d072-default-rtdb.firebaseio.com",
  "projectId": "gps-tracker-9d072",
  "storageBucket": "gps-tracker-9d072.appspot.com",
  "messagingSenderId": "1032739292121",
  "appId": "1:1032739292121:web:ea39029e6a863865b74fc8",
  "measurementId": "G-6QQTR8C28Y"
    }
'''
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

while True:
        port="/dev/ttyAMA0"
        ser=serial.Serial(port, baudrate=9600, timeout=0.5)
        dataout = pynmea2.NMEAStreamReader()
        newdata=ser.readline()
        n_data = newdata.decode('latin-1')

        if n_data[0:6] == '$GPRMC':
                newmsg=pynmea2.parse(n_data)
                lat=newmsg.latitude
                lng=newmsg.longitude
                gps = "Latitude=" + str(lat) + " and Longitude=" + str(lng)
                print(gps)

                dataa = {"GPS":{"LAT": str(lat), "LNG": str(lng)}}
                db.update(dataa)
                print("Data sent")
