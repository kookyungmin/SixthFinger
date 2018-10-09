#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
#include <SoftwareSerial.h>
#include <Servo.h>

int servo = D4;
int blueRx = D8;
int blueTx = D7;
char ssid[30] = {0};
char password[30] = {0};
String ssidStr = "";
String passwordStr = "";

/*  WL_NO_SSID_AVAIL = 1 AP 이름 오류
 *  WL_CONNECTED = 3 연결성공 
 *  WL_CONNECT_FAILED = 4 연결실패
 *  WL_CONNECTION_LOST = 5 연결 끊김
 *  WL_DISCONNECTED = 6 연결안됨
 */

ESP8266WebServer server(80); //localhost:80
Servo sv;
SoftwareSerial BTSerial(blueTx, blueRx);  


//SSID, PW 설정
void setWiFiInfo(){
  if(BTSerial.available() > 0){
      String data = BTSerial.readString();
      int pos = data.indexOf('\n');
      if(pos > 0){
        String state = data.substring(0, pos);
        data = data.substring(pos + 1);
        if(state == "BlueToothConnected"){
          pos = data.indexOf('\n');
          ssidStr = data.substring(0, pos);
          passwordStr = data.substring(pos + 1, data.length() - 1);
          Serial.println("ssid = " + ssidStr);
          Serial.println("pw = " + passwordStr);
          connectToWiFi(); 
        }  
      }
   }
}
//와이파이 연결
void connectToWiFi(){
  WiFi.disconnect();
  ssidStr.toCharArray(ssid, ssidStr.length() + 1);
  passwordStr.toCharArray(password, passwordStr.length() + 1);
  
  if(passwordStr == ""){
    WiFi.begin(ssid);
  }else{
    WiFi.begin(ssid, password);
  }
  while(WiFi.status() == WL_DISCONNECTED){
    Serial.println("\nWiFi.status() >>>>>>>>>" + (String)WiFi.status());
    delay(1000);
  }
  if(WiFi.status() == WL_CONNECTED){
    Serial.println("\nConnected");
    Serial.print("IP address: ");
    Serial.print(WiFi.localIP());
    BTSerial.write("SUCCESS\n");
  }else{
    Serial.println("\nFailed");
    BTSerial.write("FAIL\n");
 }
}

void setup(){
  Serial.begin(9600);
  BTSerial.begin(9600);
  sv.attach(servo); //서보모터 연결
}

void loop(){
  setWiFiInfo();
}
