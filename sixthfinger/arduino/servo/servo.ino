//건전지 9V 연결 후에 가능
#include <Servo.h>
int touchSensor = D3;
int servo = D4;
int touchValue = LOW;
String lastMessage = "";
int state = LOW;
Servo sv;

void setup() {
  pinMode(touchSensor, INPUT);
}

void loop() {
    int touchValue = digitalRead(touchSensor);
    if(touchValue == HIGH) {
        state = !state;
        if(state == HIGH){
          sv.attach(servo);
          sv.write(120); //120도
          delay(1500);
          sv.detach();
          lastMessage = "on";
    }else if(state == LOW){
          sv.attach(servo);
          sv.write(0); //120도
          delay(1500);
          sv.detach();
          lastMessage = "off";
      }  
    }
}
