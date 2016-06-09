#include <FastLED.h>
#define NUM_LEDS 30
#define DATA_PIN 6
CRGB leds[NUM_LEDS];
CHSV ledsHSV[NUM_LEDS];

int inByte = 0;
int program = 0;
int i = 0;
int modifier = 0;
int pid = 4;
boolean loopingpid = false;
int iHue = 0;
int timing = 5;
int temp = 150;
int spectrumValue[7];

int h = 0;
int s = 0;
int v = 0;

int p = 0;
const int analogOutPin = 9;

void setup() {
  delay(1500);
  Serial.begin(9600);

  FastLED.addLeds<NEOPIXEL, DATA_PIN>(leds, NUM_LEDS);
  for(int x = 0; x < NUM_LEDS; x++){
          leds[x] = CHSV(200,230,230);
          FastLED.show();
          delay(5);
  }
}

void serialEvent(){
  Serial.println("serial event was called");
  int input = Serial.parseInt();

  if (input < 0) {
    modifier = input;

    if (modifier == -1){
      timing = Serial.parseInt();
      Serial.println("Timing is now");
      Serial.print(timing);
    } else if (modifier == -2){
      h = Serial.parseInt();
      s = Serial.parseInt();
      v = Serial.parseInt();    
    } 
    
  } else {
    pid = input;

    if (pid == 0) {
     iHue = Serial.parseInt();
     
    } else if (pid == 1) {
      setUniform(h, s, v);
      
    } else if (pid == 4) {
      temp = Serial.parseInt();
      heat(temp);
    } else if (pid == 5) {
      fadeall();

    } else if (pid == 7){
      fill_rainbow(ledsHSV, NUM_LEDS, 0, 7); 
          
      for (int i = 0; i < NUM_LEDS; i++) {
        leds[i] = CHSV(ledsHSV[i].h, ledsHSV[i].s, ledsHSV[i].v);
      }
      
      FastLED.show();
      audioModInit();
      spectrumValue[4] = 512;
      
    } else if (pid == 8) {
      fill_rainbow(ledsHSV, NUM_LEDS, 0, 7);
      for (int i = 0; i < 30; i++) {
        Serial.print(ledsHSV[i].h);
        Serial.print(ledsHSV[i].s);
        Serial.print(ledsHSV[i].v);
        Serial.println();
      }

      for (int i = 0; i < NUM_LEDS; i++) {
       leds[i] = CHSV(ledsHSV[i].h, ledsHSV[i].s, ledsHSV[i].v);
      }
      FastLED.show();   
      
    } else if (pid == 9) { //desaturate hsv array rainbow
      desat(0.9);
      for (int i = 0; i < NUM_LEDS; i++) {
       leds[i] = CHSV(ledsHSV[i].h, ledsHSV[i].s, ledsHSV[i].v);
      }
      FastLED.show();

      
    }
    // pid 6 (lone runner) needs no setup args
  }

  

  
  Serial.println(pid);
  delay(5);
}


void loop() {
  //while(Serial.available() > 0) {

    
    if (pid == 0) {
      rainbow(iHue);
      iHue += 1;
      delay(timing);
      
    } else if (pid == 1) {

      
    } else if (pid == 4) {
 
    } else if (pid == 6) {
      leds[p] = CHSV(h,s,v);
      p++;
      FastLED.show();
      fadeall();
      delay(timing);
      if (p == NUM_LEDS){
        p = 0;
      }
    } else if (pid == 7) {
      int amplitude = analogRead(A0);
      Serial.println(amplitude);
      
      audioMod();
      FastLED.show();
    }
    


       
}

void setUniform(int h, int s, int v){
  for(int x = 0; x < NUM_LEDS; x++){
        leds[x] = CHSV(h,s,v);
        FastLED.show();
        delay(timing);
  }
}

void rainbow(int iHue){
  // FastLED's built-in rainbow generator
  fill_rainbow(leds, NUM_LEDS, iHue, 7);
  FastLED.show();
}

void staticRainbow(){
  fill_rainbow(leds, NUM_LEDS, 0, 7);
  FastLED.show();
}

void heat(int temp){
  CRGB RGBTemp = HeatColor(temp);
  fill_solid(leds, NUM_LEDS, RGBTemp);
  FastLED.show();
}


void fadeall() { 
  for(int i = 0; i < NUM_LEDS; i++) { 
    leds[i].nscale8(192); 
  } 
  FastLED.show();
}

void desat(float val) {
  for(int i = 0; i < NUM_LEDS; i++){
    ledsHSV[i].s = ledsHSV[i].s * val;
    Serial.print(ledsHSV[i].s); 
  }
}

void deval(float val) {
  for(int i = 0; i < NUM_LEDS; i++){
    ledsHSV[i].v = ledsHSV[i].v * val;
  }
}

//void modulationTest(CHSV

void audioModInit() {
  desat(0.5f);
  deval(0.75f);
  for (int i = 0; i < NUM_LEDS; i++) {
       leds[i] = CHSV(ledsHSV[i].h, ledsHSV[i].s, ledsHSV[i].v);
  }
  FastLED.show();
  delay(1000);
}

void audioMod() {
  
  for(int i = 0; i < 7; i++){
    spectrumValue[i]=map(spectrumValue[i], 0,1023,0,255);
    Serial.print("Spectrum values mapped!");
    //for(int j = 0; j < NUM_LEDS; j++){
      //Serial.print("  currently on led ");
      //Serial.print(j);
      //int homeled = i * 32;
      //for(int k = homeled; k < homeled + 128; k++) {
        ledsHSV[25].s = ledsHSV[25].s * spectrumValue[25];
      //}
      
    //}
  }
  for (int i = 0; i < NUM_LEDS; i++) {
       leds[i] = CHSV(ledsHSV[i].h, ledsHSV[i].s, ledsHSV[i].v);
  }
  FastLED.show();
}

        
 




