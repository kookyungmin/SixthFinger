//github.com/hellerchr/esp8266-websocketclient
#include "WebSocketClient.h"

#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <SoftwareSerial.h>
#include <Servo.h>

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

int servo = D4;
int blueRx = D8;
int blueTx = D7;
char ssid[30] = {0};
char password[30] = {0};
String ssidStr = "";
String passwordStr = "";

int humidity = 0;
int temperature = 0;

String host = "35.189.144.126";
String path = "/CommunicationToArduino";
String lastMessage = "";

Servo sv;
SoftwareSerial BTSerial(blueTx, blueRx);

WebSocketClient ws(false);
  
DHT dht(DHTPIN, DHTTYPE);

//SSID, PW 설정
void setWiFiInfo(){
  if(BTSerial.available() > 0){
      ws.disconnect();
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
    //웹 소켓과 연결
    connectWebSocket();
    lastMessage = "";
 }else{
    Serial.println("\nFailed");
    BTSerial.write("FAIL\n");
 }
}

void connectWebSocket(){
  ws.connect(host, path , 8080);
  if (ws.isConnected()) {
    Serial.println("\nWebSocketConnected!");
    ws.send("connected,arduino," + productID + ",connect!!");
  } else {
    Serial.println("\nWebSocketFailed");
  }
}

void setTemp(){
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();
  Serial.println("\ntemperature: " + String(temperature) + " humidity: " + String(humidity));
}

void setup(){
  Serial.begin(9600);
  BTSerial.begin(9600);
  WiFi.disconnect();
  ws.disconnect();
}

void loop(){
  setWiFiInfo();
  if(ws.isConnected()){
      String msg;
      ws.getMessage(msg);
      if (msg.length() > 0) {
        Serial.print("\nReceived data: ");
        Serial.println(msg);
        if(msg == "on" && msg != lastMessage){
           sv.attach(servo);
           sv.write(120);
           delay(1000);
           sv.detach();
           lastMessage = msg;
        }else if(msg == "off" && msg != lastMessage){
           sv.attach(servo);
           sv.write(0);
           delay(1000);
           sv.detach();
           lastMessage = msg;
        }else if(msg == "requestTemp"){
          setTemp();
          ws.send("temperature,arduino," + productID + "," + String(temperature) + "/" + String(humidity));
        }
      }
  }
}
