#include <FastLED.h>
#define NUM_LEDS 30
#define DATA_PIN 6
CRGB leds[NUM_LEDS];
CHSV ledsHSV[NUM_LEDS];

int LM35Pin = A0;

int inByte = 0;
int program = 0;
int i = 0;
int modifier = 0;
int pid = 4;
int rainbowSolidHue = 0;
int iHue = 0;
int timing = 5;
int temp = 150;
int spectrumValue[7];


int h = 0;
int s = 0;
int v = 0;

int deltaH = 0;
int deltaS = 0;
int deltaV = 0;

int p = 0;
const int analogOutPin = 9;

void setup() {
  delay(500);
  Serial.begin(115200);

  FastLED.addLeds<NEOPIXEL, DATA_PIN>(leds, NUM_LEDS);
  for(int x = 0; x < NUM_LEDS; x++){
          leds[x] = CHSV(130,160,100);
          FastLED.show();
          delay(5);
  }
}



//modifier -1: timing [int timing]
//modifier -2: hsv colour select [int hue, int saturation, int value] (All from 0 - 255)
//modifier -3: get temp
//modifier -4: cycle hue [int] {"-4 0" disables cycle}
//modifier -5: cycle saturation [int] {"-5 0" disables cycle}
//modifier -6: cycle value [int] {"-6 0" disables cycle}

//pid 0: rotating rainbow [int initialHue]
//pid 1: set uniform 
//pid 2: confetti
//pid 3: 
//pid 4: set heat [int temp]
//pid 5: fade all
//pid 6: lone runner
//pid 7: Audio modulated (WIP, crashes on AVR)
//pid 8: static rainbow (dimmable)
//pid 9: desaturate hsv rainbow used for {pid 8}
//pid 10: theatre ticker


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
    } else if (modifier == -3) {
      double extTemp = getTemp();
      Serial.println(extTemp);
    } else if (modifier == -4) {
      deltaH = Serial.parseInt();
    }  else if (modifier == -5) {
      deltaS = Serial.parseInt();
    }  else if (modifier == -6) {
      deltaV = Serial.parseInt();
    }
    
  } else {
    pid = input;

    if (pid == 0) {
     iHue = Serial.parseInt();
     
    } else if (pid == 1) {
      setUniform(h, s, v);
      FastLED.show();

    } else if (pid == 2) {

    } else if (pid == 3) {
      s = 255;
      v = 255;
      
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
  delay(1);
}


void loop() {

    
    if (pid == 0) {
      rainbow(iHue);
      iHue += 1;
      delay(timing);
      
    } else if (pid == 1) {
      setUniform(h, s, v);
      FastLED.show();
      delay(timing);

    } else if (pid == 2) {
      confetti();
      delay(timing);
      
    } else if (pid == 3) {
      fill_solid(leds, NUM_LEDS, CHSV(rainbowSolidHue,s,v));
      rainbowSolidHue++;
      FastLED.show();
      delay(timing);
      
    } else if (pid == 4) {
      //nothing to loop for heat
      
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
      int amplitude = 512; //analogRead(A0)
      Serial.println(amplitude);
      
      audioMod();
      FastLED.show();
      
    } else if (pid == 10) {

      int pixelDistance = 6;
      for (int i = 0; i < NUM_LEDS; i++){
        if (i % pixelDistance == 0 + p) {
          leds[i] = CHSV(h,s,v);
        }
      }

      p++;
      FastLED.show();
      int fadeFactor = v/64;
      for (int i = 0; i < fadeFactor; i++){
        fadeall();        
      }
      
      delay(timing);
      if (p == pixelDistance){
        p = 0;
      }
    }

  //Modifiers
  h = h + deltaH;
  s = s + deltaS;
  v = v + deltaV;

  //looping the modifiers
  if (h == 0 || h == 255 && deltaH != 0){
    if(deltaH > 0 && h == 255){
      h = 0;
    } else if (deltaH < 0 && h == 0){
      h = 255;
    }
  }

  if (s == 0 || s == 255 && deltaS != 0){
    if(deltaS > 0 && s == 255){
      deltaS = deltaS * -1;
    } else if (deltaS < 0 && s == 0){
      deltaS = deltaS * -1;
    }
  }

  if (v == 0 || v == 255 && deltaV != 0){
    if(deltaV > 0 && v == 255){
      deltaV = deltaV * -1;
    } else if (deltaV < 0 && v == 0){
      deltaV = deltaV * -1;
    }
  }       
}

void setUniform(int h, int s, int v){
  for(int x = 0; x < NUM_LEDS; x++){
        leds[x] = CHSV(h,s,v);
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
    for(int j = 0; j < NUM_LEDS; j++){
      Serial.print("  currently on led ");
      Serial.print(j);
      int homeled = i * 32;
      for(int k = homeled; k < homeled + 128; k++) {
        ledsHSV[k].s = ledsHSV[k].s * spectrumValue[i];
      }
      
    }
  }
  for (int i = 0; i < NUM_LEDS; i++) {
       leds[i] = CHSV(ledsHSV[i].h, ledsHSV[i].s, ledsHSV[i].v);
  }
  FastLED.show();
}

double getTemp() {
  int rawData = analogRead(LM35Pin);
  Serial.println(rawData);
  //each value of the ADC (when using 5V) is 0.004982V (or 4.882 mV)
  double extTemp = rawData*0.004882;
  extTemp = extTemp*100;
  return extTemp;
}

void confetti() 
{
  // random colored speckles that blink in and fade smoothly
  fadeToBlackBy( leds, NUM_LEDS, 10);
  int pos = random16(NUM_LEDS);
  leds[pos] += CHSV( iHue + random8(64), 200, 255);
}

void theatreTicker() {
  int movingPixels = 4;
  for(int i = 0; i < NUM_LEDS; i++){
    leds[i] = CHSV(h,s,v);
    fadeall();
  }
  
}



