package com.HomeConnect.gui;
public class definitions{
	public static String getFunc(int id){
		if(id == 1){
			return "Lighting";
		} else if (id == 2){
			return "Environment Sensors";
		} else {
			return "Unknown function!";
		}
	}
}