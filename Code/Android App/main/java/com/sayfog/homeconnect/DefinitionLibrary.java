package com.sayfog.homeconnect;


import java.util.HashMap;

/**
 * Created by alist on 26/08/2016.
 */
public class DefinitionLibrary {
    static HashMap<Integer, DeviceFunctionDefinition> allFuncs = new HashMap<Integer, DeviceFunctionDefinition>();

    public static void createLibrary(){
        DeviceFunctionDefinition LIGHTS = new DeviceFunctionDefinition((byte)1, "LIGHTS", BundleView.class);

        CmdBundle chooseLightAnimation = new CmdBundle("LIGHTS_PROGRAM_SELECT", CmdView.class);
        chooseLightAnimation.addCmd(new CmdDefinition("Rotating Rainbow", false, "0 2 0", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Single Colour", false, "0 2 1", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Lone Runner", false, "0 2 6", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Static Rainbow", false, "0 2 8", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Audio Modulation", false, "0 2 11", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Adv Audio Modulation", false, "0 2 12", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Linear VU Meter", false, "0 2 13", 0));
        chooseLightAnimation.addCmd(new CmdDefinition("Value VU Meter", false, "0 2 14", 0));
        LIGHTS.addBundle(chooseLightAnimation);


        CmdBundle chooseColours = new CmdBundle("LIGHTS_COLOUR_SELECT", CmdView.class);
        chooseColours.addCmd(new CmdDefinition("Set Hue", false, "0 3 86", 1));
        chooseColours.addCmd(new CmdDefinition("Set Saturation", false, "0 3 87", 1));
        chooseColours.addCmd(new CmdDefinition("Set Value", false, "0 3 88", 1));
        chooseColours.addCmd(new CmdDefinition("Set Delta Hue", false, "0 3 83", 1));
        chooseColours.addCmd(new CmdDefinition("Set Delta Saturation", false, "0 3 84", 1));
        chooseColours.addCmd(new CmdDefinition("Set Delta Value", false, "0 3 85", 1));
        LIGHTS.addBundle(chooseColours);

        allFuncs.put(1, LIGHTS);


    }

    public static DeviceFunctionDefinition getDevFunc(Integer byteKey){
        return allFuncs.get(byteKey);
    }
}
