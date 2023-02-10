

#define USE_ARDUINO_INTERRUPTS true // Set-up low-level interrupts for most acurate BPM math.
#include <PulseSensorPlayground.h> // Includes the PulseSensorPlayground Library.
#include <Wire.h>
#include <LCD.h>
#include <LiquidCrystal_I2C.h> // F Malpartida's NewLiquidCrystal library
#define I2C_ADDR 0x27 // Define I2C Address for controller
#define En_pin 2
#define Rw_pin 1
#define Rs_pin 0
#define D4_pin 4
#define D5_pin 5
#define D6_pin 6
#define D7_pin 7
#define BACKLIGHT 3

LiquidCrystal_I2C lcd(I2C_ADDR,En_pin,Rw_pin,Rs_pin,D4_pin,D5_pin,D6_pin,D7_pin);
 
// Variables
const int PulseWire = 0; // PulseSensor PURPLE WIRE connected to ANALOG PIN 0
const int LED13 = 13; // The on-board Arduino LED, close to PIN 13.
int Threshold = 550; // Determine which Signal to "count as a beat" and which to ignore.
// Use the "Gettting Started Project" to fine-tune Threshold Value beyond default setting.
// Otherwise leave the default "550" value.
 
PulseSensorPlayground pulseSensor; // Creates an instance of the PulseSensorPlayground object called "pulseSensor"
void setup() {
 
Serial.begin(9600); // For Serial Monitor
lcd.begin(20,4);
lcd.setBacklightPin(BACKLIGHT,POSITIVE);
lcd.setBacklight(HIGH);
 
// Configure the PulseSensor object, by assigning our variables to it.
pulseSensor.analogInput(PulseWire);
pulseSensor.blinkOnPulse(LED13); //auto-magically blink Arduino's LED with heartbeat.
pulseSensor.setThreshold(Threshold);
 
// Double-check the "pulseSensor" object was created and "began" seeing a signal.
if (pulseSensor.begin()) {
lcd.setCursor(0,0);
lcd.print(" Heart  Monitor");
 
}
}
 
void loop() {
 
int myBPM = pulseSensor.getBeatsPerMinute(); // Calls function on our pulseSensor object that returns BPM as an "int".
// "myBPM" hold this BPM value now.
if (pulseSensor.sawStartOfBeat()) { // Constantly test to see if "a beat happened".
  lcd.setCursor(0,0);
Serial.println(myBPM); // Print the value inside of myBPM.
lcd.setCursor(0,0);
lcd.print("HeartBeat Rate"); // If test is "true", print a message "a heartbeat happened".
lcd.setCursor(0,1);
lcd.print("BPM: "); // Print phrase "BPM: "
lcd.print("                j");
lcd.setCursor(5,1);
lcd.print(myBPM);
}
delay(20); // considered best practice in a simple sketch.
}
