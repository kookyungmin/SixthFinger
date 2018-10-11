#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
#include <SoftwareSerial.h>
#include <Servo.h>
#include <ArduinoJson.h> //5.13.2 버전

#include "DHT.h"
#define DHTPIN D2
#define DHTTYPE DHT11

/*  WL_NO_SSID_AVAIL = 1 AP 이름 오류
 *  WL_CONNECTED = 3 연결성공 
 *  WL_CONNECT_FAILED = 4 연결실패
 *  WL_CONNECTION_LOST = 5 연결 끊김
 *  WL_DISCONNECTED = 6 연결안됨
 */
 
const String productID = "sixfinger1";

char* ssid = "tjoeun604";
char* password = "029381119";

String jsondata = "";
int humidity = 0;
int temperature = 0;

char server_address[] = "192.168.0.72";
char sendData_uri[] = "/sixfinger/receiveArduino";

StaticJsonBuffer<200> jsonBuffer;
JsonObject& root = jsonBuffer.createObject();

WiFiClient client;
DHT dht(DHTPIN, DHTTYPE);

//와이파이 연결
void connectToWiFi(){
  WiFi.begin(ssid, password);
  while(WiFi.status() == WL_DISCONNECTED){
    Serial.println("\nWiFi.status() >>>>>>>>>" + (String)WiFi.status());
    delay(1000);
  }
  if(WiFi.status() == WL_CONNECTED){
    Serial.println("\nConnected");
    Serial.print("IP address: ");
    Serial.print(WiFi.localIP());
  }else{
    Serial.println("\nFailed");
 }
}

//온 습도 서버한테 보냄
void sendTemp(){
  root["temperature"] = temperature;
  root["humidity"] = humidity;
  root.printTo(jsondata);
  //Serial.println(jsondata);
  if(client.connect(server_address, 8080)){
    client.print(String("POST ") + sendData_uri + String("?id=") + productID);
    client.println(" HTTP/1.1");
    client.println(String("Host:") + server_address + String(":8080"));
    client.println("Content-Type: application/json");
    client.print("Content-Length: ");
    client.println(jsondata.length());
    client.println();
    client.print(jsondata);
    client.println();  
  }
  Serial.print(String("POST ") + sendData_uri + String("?id=") + productID);
  Serial.println(" HTTP/1.1");
  Serial.println(String("Host:") + server_address + String(":8080"));
  Serial.println("Content-Type: application/json");
  Serial.print("Content-Length: ");
  Serial.println(jsondata.length());
  Serial.println();
  Serial.print(jsondata);
  Serial.println();
  jsondata = "";
}

void setTemp(){
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();
}

void setup(){
  Serial.begin(9600);
  connectToWiFi();
}

void loop(){
  if(WiFi.status() == WL_CONNECTED){
    setTemp();
    sendTemp();
    delay(1000);  
  }
}
