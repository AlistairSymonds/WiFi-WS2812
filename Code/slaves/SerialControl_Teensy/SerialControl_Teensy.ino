#define FASTLED_ALLOW_INTERRUPTS 0
#include <FastLED.h>
#include <Audio.h>
#define NUM_LEDS 360
#define DATA_PIN 6
#define USE_OCTOWS2811
#include<OctoWS2811.h>

#define NUM_LEDS_PER_STRIP 360
#define NUM_STRIPS 1

CRGB leds[NUM_STRIPS * NUM_LEDS_PER_STRIP];



CHSV ledsHSV[NUM_LEDS];

int LM35Pin = A0;

int inByte = 0;
int program = 0;
int i = 0;
int modifier = 0;
int pid = 1;
int rainbowSolidHue = 0;
int iHue = 0;
long timing = 50;
int temp = 150;
int spectrumValue[7];

float amplitudes[15];
float oldAmps[15];





AudioInputAnalog         adc1;           //xy=164,115
AudioAnalyzeFFT1024      fft1024;      //xy=408,120
AudioAnalyzePeak         peak;
AudioConnection          patchCord1(adc1, fft1024);
AudioConnection          patchCord2(adc1, peak);




int h = 164;
int s = 0;
int v = 64;

int deltaH = 0;
int deltaS = 0;
int deltaV = 0;

boolean useHSVForFFT = true;

int p = 0;
const int analogOutPin = 9;

//method declarations:
void setUniform(int h, int s, int v);
void rainbow(int iHue);
void staticRainbow();
void heat(int temp);
void fadeall();
void desat(float val);
void deval(float val);
void audioModInit();
void audioMod();
double getTemp();
void confetti();
void theatreTicker();
void updateLEDS();
void serialGiveInfo();
void audioFFTUpdate();
void audioFFTUpdateImproved();
void linearVUMeter();
void valueVUMeter();

void setup() {
  delay(500);
  AudioMemory(12);
  Serial1.begin(9600);
  Serial.begin(9600);
  Serial.print("usb serial");
  Serial1.write(88);

  FastLED.addLeds<NEOPIXEL, DATA_PIN>(leds, NUM_LEDS);
  for(int x = 0; x < NUM_LEDS; x++){
          leds[x] = CHSV(255,0,255);
          FastLED.show();
          delay(5);
  }
}



//modifier 80: timing [int timing]
//modifier 81: hsv colour select [int hue, int saturation, int value] (All from 0 - 255)
//modifier 82: get temp
//modifier 83: cycle hue [int] {"-4 0" disables cycle}
//modifier 84: cycle saturation [int] {"-5 0" disables cycle}
//modifier 85: cycle value [int] {"-6 0" disables cycle}
//modifier 86: set h
//modifier 86: set s
//modifier 87: set v

//command 90: get info on serial

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
//pid 11: fft audio modulation
//pid 12
//pid 13: value vu meter
//pid 14: linear vu meter

void Serial1_Event(byte messageIn[], int messageInLength){

  if (messageIn[0] > 79) {
    modifier = messageIn[0];

    if (modifier == 80){
      timing = messageIn[1];
    } else if (modifier == 81){
      h = messageIn[1];
      s = messageIn[2];
      v = messageIn[3];
    } else if (modifier == 82) {
      double Temp = getTemp();
    } else if (modifier == 83) {
      deltaH = messageIn[1];
    }  else if (modifier == 84) {
      deltaS = messageIn[1];
    }  else if (modifier == 85) {
      deltaV = messageIn[1];
    } else if(modifier == 86){
      h = messageIn[1];
    } else if(modifier == 87){
      s = messageIn[1];
    } else if(modifier == 88){
      v = messageIn[1];
    } else if( modifier == 90){
      serialGiveInfo();
    }
    
  } else {
    pid = messageIn[0];

    if (pid == 0) {
     iHue = messageIn[1];
     
    } else if (pid == 1) {
      setUniform(h, s, v);
      FastLED.show();

    } else if (pid == 2) {

    } else if (pid == 3) {
      s = 255;
      v = 255;
      
    } else if (pid == 4) {
      temp = messageIn[1];
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

  

  
  //Serial.println(pid);
  delay(1);

}


void loop() {
    if(Serial1.available() > 0){
      delay(10);
      byte messageInLength = Serial1.read();
      Serial.print("msg len: ");
      Serial.println(messageInLength);
      byte messageIn[messageInLength];
      Serial1.readBytes(messageIn, messageInLength);
      Serial.println("msg start");
      for(int i = 0; i < messageInLength; i++){
        Serial.println(messageIn[i]);
      }
      Serial.println("Msg done");
      Serial1_Event(messageIn, messageInLength);
      Serial1.flush();
    }
    
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
      //FastLED.show();
      fadeall();
      delay(timing);
      if (p == NUM_LEDS){
        p = 0;
      }
      
    } else if (pid == 7) {
      int amplitude = 512; //analogRead(A0)
      //Serial.println(amplitude);
      
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
    } else if (pid == 11){
      if(fft1024.available()){
        audioFFTUpdate();
      }
    } else if (pid == 12){
      if(fft1024.available()){
        audioFFTUpdateImproved();
      }
    } else if(pid == 13){
      if(peak.available()){
        linearVUMeter();
      }
      
      //delay(50);
    } else if(pid == 14){
      valueVUMeter();
      //delay(50);
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
  delay(1000);       
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
      //Serial.print("  currently on led ");
      //Serial.print(j);
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
  //Serial.println(rawData);
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

void audioFFTUpdate(){
  /*
  float level[16];

  level[0] =  fft1024.read(0); //1 bins
  level[1] =  fft1024.read(1); //1
  level[2] =  fft1024.read(2, 3); // 2
  level[3] =  fft1024.read(4, 6);// 3
  level[4] =  fft1024.read(7, 10); //4
  level[5] =  fft1024.read(11, 15);//5
  level[6] =  fft1024.read(16, 22);//7
  level[7] =  fft1024.read(23, 32);// 10
  level[8] =  fft1024.read(33, 46);//14
  level[9] =  fft1024.read(47, 66);//19
  level[10] = fft1024.read(67, 93);// 27
  level[11] = fft1024.read(94, 131); //37
  level[12] = fft1024.read(132, 184); //52
  level[13] = fft1024.read(185, 257); // 73
  level[14] = fft1024.read(258, 359);
  level[15] = fft1024.read(360, 511);

  int ledsPerLevel = NUM_LEDS/16;
  int currentLED = 0;

  */
  
  if(useHSVForFFT){
    Serial.println();
    for(int i = 0; i < NUM_LEDS; i++){
      float floatMult = 255;
      int val = floatMult * fft1024.read(i);
      if(i == 0 || i== 1){
        //do nothing
      } else{
        val = val * val;
      }
      
      Serial.print("-");
      Serial.print(val);
      if(val < 8){
        val = 8;
      }
      leds[i] = CHSV(h, 255, val);
    }
    /*
    for(int i = 0; i < 16; i++){
      Serial.print("-");
      Serial.print(level[i]);
      for(;currentLED < i*ledsPerLevel; currentLED++){
        if(i < NUM_LEDS){
          leds[currentLED] = CHSV(h, 255, level[i]*128);
        }
      }
    }
    Serial.println();
    */
  }else{
    
  }

  FastLED.show();
}

void audioFFTUpdateImproved(){
  int samplesPerBin[] = {
    1, 1, 2, 3,
    4, 5, 7, 10,
    14, 19, 27, 37,
    52, 72, 101};

   
   
  //amplitudes[15];
  //oldAmps[15];
  for(int i = 0; i < 15; i++){
    oldAmps[i] = amplitudes[i];
  }
  
  int currentBin = 0;
  
  for(int i = 0; i < 15; i++){
    float newAmp = 0;
    for(int j = 0; j < samplesPerBin[i]; j++){
      newAmp = newAmp + fft1024.read(currentBin);
      currentBin++;
    }
    
    newAmp = newAmp/samplesPerBin[i];
    
    if(newAmp < oldAmps[i]){
      //amplitudes[i] = oldAmps[i] - 0.001; 
    } else{
      amplitudes[i] = newAmp;
    }
  }

  for(int i = 0; i < 15; i++){
    Serial.print(amplitudes[i]);
    Serial.print(" ");
    float floatMult = 255.0;
    int val = floatMult * amplitudes[i];
    if(val < 8){
        val = 8;
    } else {
      val = val * val;
    }
    leds[i] = CHSV(h, s, val);
  }

  fadeall();
  FastLED.show();
  Serial.println();
  
}

void valueVUMeter(){
  
  float level = peak.read();
  level = level *10;
  int val = level * level;
  Serial.println(val);
  for(int i = 0; i < NUM_LEDS; i++){
    leds[i] = CHSV(h, s, val);
  }
  FastLED.show();
}

void linearVUMeter(){
  float peakVal = peak.read();
  int vHeight = peakVal * NUM_LEDS;
  Serial.println(peakVal);
  /*
  for(int i = 0; i < NUM_LEDS; i++){
    leds[i] = CHSV(0,0,0);
  }
  */
  fadeall();
  fadeall();
  for(int i = 0; i < vHeight; i++){
    leds[i] = CHSV(h,s,v);
  }
  FastLED.show();
}

void serialGiveInfo(){
   //print back fake test info
      Serial1.write((byte)11); //length of message
      Serial1.write((byte)h); //testing hue
      Serial1.write((byte)s); //testing saturation
      Serial1.write((byte)v); //testing value
      byte tTime[4];
      tTime[0] = (byte) ((timing >> 24) & 0xff);
      tTime[1] = (byte) ((timing >> 16) & 0xff);
      tTime[2] = (byte) ((timing >> 8) & 0xff);
      tTime[3] = (byte) (timing) & (0xff);
      Serial1.write(tTime[0]);
      Serial1.write(tTime[1]);
      Serial1.write(tTime[2]);
      Serial1.write(tTime[3]);
      Serial1.write((byte)pid); //current pid, lone runner in this case
      Serial1.write((byte)deltaH); //dH
      Serial1.write((byte)deltaS);
      Serial1.write((byte)deltaV);
}



