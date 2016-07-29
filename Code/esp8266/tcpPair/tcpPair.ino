#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <stdint.h>

byte funcs[] = {1, 3, 6};//1 for led lights over serial, 2 for local temps
const char* ssid     = "symnet01";
const char* password = "GreenM00n";

WiFiServer server(2812);
boolean firstConnect = true;
int dots = 0;

void readMessage();
void writeMessage(byte[]);

void setup() {
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  WiFi.begin(ssid, password);

  //begin connection block
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(LED_BUILTIN, LOW);
    delay(1000);
    digitalWrite(LED_BUILTIN, HIGH);
    Serial.print(".");
    delay(1000);
  }

  digitalWrite(LED_BUILTIN, HIGH);
  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println(WiFi.RSSI());
  server.begin();


}

void loop() {
  WiFiClient client = server.available();
  if (client == true) {
    Serial.println("client here");
    if(firstConnect){
      Serial.println("Client connected");
      while (firstConnect == true && client.connected() == true){
        if(client.available() > 0){
          int shake = client.read();
          Serial.println(shake);
          if (shake == 17){//recived 17
            Serial.println("I recieved a 17, shake one complete, sending 12...");
            client.write(12); //send 12
            //basic handshake complete, sending device id and 
            byte id[4];
            long intID = ESP.getChipId();
            id[0] = (byte) ((intID >> 24) & 0xff);
            id[1] = (byte) ((intID >> 16) & 0xff);
            id[2] = (byte) ((intID >> 8) & 0xff);
            id[3] = (byte) (intID) & (0xff);
            firstConnect = false;
            //Serial.println(intID);
            for(int i = 0; i < 4; i++){
              client.write(id[i]);
              Serial.print(id[i]);
              Serial.print(" ");
            }
            Serial.println();
            client.write(sizeof(funcs));
            Serial.println(sizeof(funcs));
            for(int i = 0; i < sizeof(funcs); i++){
              client.write(funcs[i]);
              Serial.println(funcs[i]);
            }
            Serial.println();
           }
         }
       } 
     }
      
      
    
    while(client.connected() && firstConnect == false){
      while (client.available() > 0){
        Serial.println("made it through the shake");
        //reading in the message
        int messageInLength = client.read();
        byte messageIn[messageInLength];
        
        client.readBytes(messageIn, messageInLength);
        
        for(int i = 0; i < messageInLength; i++){
          Serial.println(messageIn[i]);
        }
        
        if(messageIn[0] > 0){//message to be passed along serial
          if(messageIn[1] == 0){
            //print back fake test info
            client.write((byte)11); //length of message
            client.write((byte)100); //testing hue
            client.write((byte)178); //testing saturation
            client.write((byte)64); //testing value
            byte tTime[4];
            long testTiming = 1234567;
            tTime[0] = (byte) ((testTiming >> 24) & 0xff);
            tTime[1] = (byte) ((testTiming >> 16) & 0xff);
            tTime[2] = (byte) ((testTiming >> 8) & 0xff);
            tTime[3] = (byte) (testTiming) & (0xff);
            client.write(tTime[0]);
            client.write(tTime[1]);
            client.write(tTime[2]);
            client.write(tTime[3]);
            client.write((byte)6); //current pid, lone runner in this case
            client.write((byte)0); //dH
            client.write((byte)0);
            client.write((byte)0);
            
          } else { //pass it along
            for (int i = 1; i < messageInLength; i++){
              Serial.write(messageIn[i]);
            }
          }
        } else{
          //execute command locally, eg read temp sensor
          Serial.println("temp sensors am I right");
        }
        client.flush();
      }
    }
  }

  if(client == false){
    firstConnect = true;
  }

  
  Serial.print(".");
  dots++;
  if(dots > 100){
    Serial.println(" ");
    dots = 0;
  }
  delay(500);
}



