#include <FastLED.h>
#include <Audio.h>
#define NUM_LEDS 144
#define DATA_PIN 6

CRGB leds[NUM_LEDS];


void setup() {
  // put your setup code here, to run once:
  FastLED.addLeds<NEOPIXEL, DATA_PIN>(leds, NUM_LEDS);
  fill_rainbow(leds, NUM_LEDS, 0, 7);
  FastLED.show();
}

void loop() {
  // put your main code here, to run repeatedly:

}
