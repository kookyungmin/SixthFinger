#include <SoftwareSerial.h>
#include <Servo.h>
Servo sv;
int servo = D4;
int blueRx = D8;
int blueTx = D7;
SoftwareSerial BTSerial(blueTx, blueRx);  

void setup(){
  Serial.begin(9600);
  BTSerial.begin(9600);
  sv.attach(servo); //서보모터 연결
}

void loop(){
   if(BTSerial.available() > 0){
      String data = BTSerial.readString();
      Serial.println(data);
   }
}
