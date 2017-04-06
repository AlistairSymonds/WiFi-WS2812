#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <EEPROM.h>
#include <stdint.h>

byte funcs[] = {1, 2};//1 for led lights over serial, 2 for local temps
const char* ssid     = "symnet01";
const char* password = "GreenM00n";

WiFiServer server(2812);
WiFiClient clients[3]; 


boolean shookHands[] = {false, false, false};
int c = 0;

void readMessage();
void writeMessage(byte[]);
boolean handShake(WiFiClient);
boolean handleMessage(byte[], int);
boolean ledStatus = true;

void setup() {
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);


  
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(LED_BUILTIN, LOW);
    delay(1000);
    digitalWrite(LED_BUILTIN, HIGH);
    delay(1000);
  }

  digitalWrite(LED_BUILTIN, HIGH);
  server.begin();


}

void loop() {
  for(c = 0; c<3; c++){
    
    if(clients[c].connected()){ 
      if (clients[c] == true && shookHands[c]== false) {
        shookHands[c] = handShake(clients[c]);
      }
    
      if(shookHands[c] == true){
        while (clients[c].available() > 0){
          
          //reading in the message
          int messageInLength = clients[c].read();
          byte messageIn[messageInLength];
          
          clients[c].readBytes(messageIn, messageInLength);
          
          
          clients[c].flush();
          boolean dcClient = handleMessage(messageIn, messageInLength);
          

          if(dcClient){
            shookHands[c] = false;
            clients[c].stop();
          }
        }
      }

      
      //finished with one client, about to advance to the next
      if(!clients[c].connected() && shookHands[c] == true){
        shookHands[c] = false;
        clients[c].stop();
      }
    } else{
      clients[c] = server.available();
    }

  }
  
  digitalWrite(LED_BUILTIN, ledStatus);
}


boolean handShake(WiFiClient client){
  
  while (client.connected()){
        if(client.available() > 0){
          int shake = client.read();
          if (shake == 17){//recived 17
            client.write(12); //send 12
            //basic handshake complete, sending device id and 
            byte id[4];
            long intID = ESP.getChipId();
            id[0] = (byte) ((intID >> 24) & 0xff);
            id[1] = (byte) ((intID >> 16) & 0xff);
            id[2] = (byte) ((intID >> 8) & 0xff);
            id[3] = (byte) (intID) & (0xff);
            //Serial.println(intID);
            
            for(int i = 0; i < 4; i++){
              client.write(id[i]);
            }
            
            client.write(sizeof(funcs));
            
            
            for(int i = 0; i < sizeof(funcs); i++){
              client.write(funcs[i]);
            }
            return true;
          }
        }
  }

  return false;
}

boolean handleMessage(byte messageIn[], int messageInLength){ //returning true means disconnect client!
  if(messageIn[0] == 0){//message to be passed along serial
    
    //serial cmds start, all serial commands should start with an odd number
      Serial.flush();
    //pass it along
      for (int i = 1; i < messageInLength; i++){
        Serial.write(messageIn[i]);
      }
    //serial cmds finish
    Serial.flush();
  } else if(messageIn[0] == 1){ // All local commands should start with an even number
     
     //local cmds start
     if(messageIn[1] == 2){
      digitalWrite(LED_BUILTIN, HIGH);
      //print back fake test info
      clients[c].write((byte)11); //length of message
      clients[c].write((byte)100); //testing hue
      clients[c].write((byte)178); //testing saturation
      clients[c].write((byte)64); //testing value
      byte tTime[4];
      long testTiming = 1234567;
      tTime[0] = (byte) ((testTiming >> 24) & 0xff);
      tTime[1] = (byte) ((testTiming >> 16) & 0xff);
      tTime[2] = (byte) ((testTiming >> 8) & 0xff);
      tTime[3] = (byte) (testTiming) & (0xff);
      clients[c].write(tTime[0]);
      clients[c].write(tTime[1]);
      clients[c].write(tTime[2]);
      clients[c].write(tTime[3]);
      clients[c].write((byte)6); //current pid, lone runner in this case
      clients[c].write((byte)0); //dH
      clients[c].write((byte)0);
      clients[c].write((byte)0);
      
    } else if (messageIn[1] == 4){
      
      if( messageIn[2] == 0){
        ledStatus = 0;
      } else if (messageIn[2] == 1){
        ledStatus = 1;
      }
    } else if (messageIn[1] == 6){
      int addr = 0;
      EEPROM.write(addr, messageIn[2]);
      for(int i = 3; i < messageInLength; i++){
        addr++;
        EEPROM.write(addr, messageIn[i]);
      }
    } else if (messageIn[1] == 8){
       clients[c].write(4);
       clients[c].write(EEPROM.read(0));
       clients[c].write(EEPROM.read(1));
       clients[c].write(EEPROM.read(2));
       //Serial.println(EEPROM.read(0));
    } else{
      //couldn't find
    }
    //local cmds finish
    Serial.flush();
    
  } else if (messageIn[0] == 9){//disonnect command!
    return true;
  }
  return false;
}

