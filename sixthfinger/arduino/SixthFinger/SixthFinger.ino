#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
#include <SoftwareSerial.h>
#include <Servo.h>
#include <ArduinoJson.h>

#include "DHT.h"
#define DHTPIN D2
#define DHTTYPE DHT11

const String productID = "sixfinger1";

int servo = D4;
int blueRx = D8;
int blueTx = D7;
char ssid[30] = {0};
char password[30] = {0};
String ssidStr = "";
String passwordStr = "";
/*String jsondata = "";
int humidity = 0;
int temperature = 0;
*/

char server_address[] = "35.189.144.126";
char server_uri[] = "/sixfinger/sendArduino";
String lastMessage = "";

/*StaticJsonBuffer<200> jsonBuffer;
JsonObject& root = jsonBuffer.createObject();
root["tempvalue"] = temperature;
root["humivalue"] = humi;*/

/*  WL_NO_SSID_AVAIL = 1 AP 이름 오류
 *  WL_CONNECTED = 3 연결성공 
 *  WL_CONNECT_FAILED = 4 연결실패
 *  WL_CONNECTION_LOST = 5 연결 끊김
 *  WL_DISCONNECTED = 6 연결안됨
 */
 
Servo sv;
SoftwareSerial BTSerial(blueTx, blueRx);  
WiFiClient client;
DHT dht(DHTPIN, DHTTYPE);

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
    String str = productID + "\n";
    char id[30] = {0};
    str.toCharArray(id, str.length() + 1);
    BTSerial.write(id);
    lastMessage = "";
  }else{
    Serial.println("\nFailed");
    BTSerial.write("FAIL\n");
 }
}


void receiveMessage(){
    if(client.connect(server_address, 80)){
      client.println(String("GET ") + server_uri + String("?id=") + productID);
      while(client.available() == 0);
      if(client.available() > 0){
        String msg = client.readString();
        if(msg == "on" && msg != lastMessage){
          Serial.println("switch " + msg);
          sv.write(120);
        }else if(msg == "off" && msg != lastMessage){
          Serial.println("switch " + msg);
          sv.write(0);
        }
        lastMessage = msg;
      }  
   }
   if(!client.connected()){
      client.stop();
    }
}

/*void sendTemp(){
  if(WiFi.status() == WL_DISCONNECTED){
    if(client.connect(server_address, 80)){
      client.  
    }
    if(!client.connected()){
      client.stop();
    }
  }
}*/
/*
void setTemp(){
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();
  Serial.print("Humidity: ");
  Serial.print(humidity);
  Serial.print(" %\t");
  Serial.print("Temperature: ");
  Serial.print(temperature);
  Serial.println(" C");
}
*/
void setup(){
  Serial.begin(9600);
  BTSerial.begin(9600);
  sv.attach(servo); //서보모터 연결
}

void loop(){
  setWiFiInfo();
  receiveMessage();
  /*setTemp();*/
}
