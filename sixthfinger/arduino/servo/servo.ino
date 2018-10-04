//건전지 9V 연결 후에 가능
#include <Servo.h>
int servo = D4;
Servo sv;

void setup() {
  sv.attach(servo); //서보모터 연결
}

void loop() {
    sv.write(120); //120도
    delay(1000);
    sv.write(0); //0도
    delay(1000);
}
