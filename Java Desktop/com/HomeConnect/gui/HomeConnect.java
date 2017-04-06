package com.HomeConnect.gui;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import org.eclipse.swt.widgets.Display;

public class HomeConnect {
	static ArrayList<espDevice> deviceList = new ArrayList<espDevice>();
	ArrayList<scanResult> results = null;
	static MainWindow window;
	
    public static void main(String[] args) throws IOException, Exception {
    	
    	window = new MainWindow();
    	Thread t = new Thread(window);
    	
    	
    	
    	t.start();
    	Thread.sleep(10);
    	
    	scan();
    	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    	Display d = window.getDisplay();
    	
    	for(int i = 0; i<100; i++)
        {           
            //System.out.println(i + "  " + d);
            //window.update(i);  
            Thread.sleep(500);
        }
    	
        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
        	takeInput(userInput);
        }


    }
    
    public static void handleStringCmd(String cmd){
        if (cmd.equals("exit")){
            System.out.println("exiting program");
            System.exit(0);
            
        } else if (cmd.equals("devices")){
        	for(int i = 0; i < deviceList.size(); i++){
        		System.out.println(deviceList.get(i).getHost());
        		
        		
        		//byte tempFuncs[] = deviceList.get(i).getFuncs().toArray();
        		System.out.println(deviceList.get(i).getFuncs());
        		System.out.println("Device function bytes are: ");
        		for(int j = 0; j < deviceList.get(i).getFuncs().size(); j++){
        			System.out.print(Integer.toBinaryString(deviceList.get(i).getFuncs().get(j)) + " ");
        		}
        		
        		System.out.println();
        		byte tempID[] = deviceList.get(i).getDeviceID();
        		System.out.print("Device ID bytes are: ");
        		for(int j = 0; j < tempID.length; j++){
        			System.out.print(Integer.toBinaryString(tempID[j]) + " ");
        		}
        		
        	}
        } else if (cmd.equals("scan")){
        	scan();
        }
    }
    
    public static void takeInput(String s){
    	try{
    		Integer.parseInt(s.split(" ")[0]);
    		String inputSplit[] = s.split(" ");
    		byte[] targetID = new byte[4];
    		for(int i = 0; i < 4; i++){
    			if(Integer.parseInt(inputSplit[i]) > 127){
                    targetID[i] = (byte)(Integer.parseInt(inputSplit[i]) & 0xFF);
                } else {
                    targetID[i] = Byte.parseByte(inputSplit[i]);
                }
    		}
    		String messageArray[] = Arrays.copyOfRange(inputSplit, 4, inputSplit.length);
    		String finalMessage = "";
    		for(int i = 0; i < messageArray.length; i++){
    			finalMessage = finalMessage + messageArray[i] + " ";
    		}
    		for (int i = 0; i < deviceList.size(); i++){
    			if(Arrays.equals(targetID, deviceList.get(i).getDeviceID()));
    			System.out.println("found matching device, sending: " + finalMessage);
    			deviceList.get(i).sendMessage(finalMessage);
    		}
    	} catch (NumberFormatException e){
    		handleStringCmd(s);
    	}
    }

    public static String[] convertFuncInt2String(int funcs[]){
        String funcsStr[] = new String[funcs.length];
        for (int i = 0; i < funcs.length; i++){
            if (funcs[i] == 1){
                funcsStr[i] = "LEDS";
            } else if (funcs[i] == 2){
                funcsStr[2] = "Environmental Sensing";
            }
        }
        return funcsStr;
    }
    
    public static ArrayList<scanResult> scan(){
    	for(int i = 0; i < deviceList.size(); i++){
    		deviceList.get(i).sendMessage("9");
    		try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		deviceList.get(i).terminate();
    		System.out.println(deviceList.get(i).isRunning());
    	}
    	
    	deviceList.clear();
    	ArrayList<scanResult> results = new ArrayList<scanResult>();
    	try {
			results = networkScanner.scan(2812);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	System.out.println("Back from the scan");
    	for (int i = 0; i < results.size(); i++){
    		if(results.get(i).isOpen()){
    			System.out.println("adding device at "+ results.get(i).getIP());
    			espDevice dev = new espDevice(results.get(i).getIP(), results.get(i).getPort());
    			dev.start();
    			if(dev.isValid()){
    				deviceList.add(dev);
    				System.out.println("device added");
    				
    				
    			} else{
    				dev.terminate();
    			}
    		}
    	}
    	//devicesList has been created, do gui stuff 
    	window.addList(deviceList);
    	
    	return results;
    	
    }
    
    
}