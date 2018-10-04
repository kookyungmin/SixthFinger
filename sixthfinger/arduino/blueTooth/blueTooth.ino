#include <SoftwareSerial.h>
#include <Servo.h>
Servo sv;
int servo = D4;
int blueRx = D8;
int blueTx = D7;
SoftwareSerial BTSerial(blueTx, blueRx);  

void setup(){
  Serial.begin(9600);
  BTSerial.begin(38400);
  sv.attach(servo); //서보모터 연결
}

void loop(){
   if(BTSerial.available()>0){
      char data = BTSerial.read();
      Serial.println(data);
      if(data == 'a'){
        sv.write(120); //120도
      }else if(data == 'b'){
        sv.write(0); //0도
      }
   }
}
