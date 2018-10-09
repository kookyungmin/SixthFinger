#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
#include <SoftwareSerial.h>

const char *ssid = "SO070VOIPA3EC";
const char *password = "8D38AEA3EB";

char server_address[] = "35.189.144.126";
char server_uri[] = "/sixfinger/sendArduino";


WiFiClient client;

void connectToWiFi(){
  Serial.print("connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED){
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\n connected");
  Serial.print("IP address: ");
  Serial.print(WiFi.localIP());
}

void receiveMessage(){
  if(client.connect(server_address, 80)){
    Serial.println("\nConnected to Server");
    client.println(String("GET ") + server_uri);
    while(client.available() == 0);
    if(client.available() > 0){
      String str = client.readString();
      Serial.println(str);
    }  
  }else{
    Serial.println("실패");
  }
}

void setup(void) {
  Serial.begin(115200);
  connectToWiFi();  
}

void loop(void) {
 receiveMessage(); 
}
