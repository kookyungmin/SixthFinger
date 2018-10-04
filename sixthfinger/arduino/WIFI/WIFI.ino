#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
#include <SoftwareSerial.h>

int blueRx = D7;
int blueTx = D8;
const char *ssid = "SO070VOIPA3EC";
const char *password = "8D38AEA3EB";
ESP8266WebServer server(80); //localhost:80
SoftwareSerial BTSerial(blueRx,blueTx);

void handleRoot(){
  String message = "<html><body>\n";
  if(BTSerial.available() > 0){
    String data = BTSerial.readString();
    message += "<p>BlueData=";
    message += data;
    message += "</p>";
  }
  message += "</body></html>\n";

  server.send(200, "text/html", message);
}

void connectToWiFi(){
  Serial.print("connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\n connected");
  Serial.print("IP address: ");
  Serial.print(WiFi.localIP());
}

void setup(void) {
  Serial.begin(115200);
  BTSerial.begin(9600);
  connectToWiFi();
  server.on("/", handleRoot); //root handleRoot 실행
  server.begin();
  Serial.println("HTTP server started");
  
}

void loop(void) {
 server.handleClient();
}
