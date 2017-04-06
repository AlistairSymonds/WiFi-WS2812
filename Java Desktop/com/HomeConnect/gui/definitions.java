package com.HomeConnect.gui;
public class definitions{
	public static String getFunc(int id){
		if(id == 1){
			return "Lighting";
		} else if (id == 2){
			return "Environment Sensors";
		} else if (id == 3){
			return "GPIO 2";
		} else if(id == 6){
			return "Debug serial access";
		}else {
			return "Unknown function!";
		}
	}
}