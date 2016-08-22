package com.HomeConnect.gui;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class espDevice implements Runnable{
    private String host = null;
    private int port;
    private byte deviceID[] = new byte[4];
    private ArrayList<Byte> funcs = null;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket ElSocketo;
    private boolean validDevice = true;
    private String humanName;
    
    private volatile boolean running = false;
    private ConcurrentLinkedQueue<String> queue;

    public espDevice(String host, int port){
    	this.host = host;
    	this.port = port;
    	this.out = null;
    	this.in = null;
    	this.ElSocketo = null;
    	this.queue = new ConcurrentLinkedQueue<String>();
    	this.funcs = new ArrayList<Byte>();
    	this.humanName = "Name not set";
    }
    
    
    
    public boolean handShake() throws IOException, Exception  {
    	System.out.println("Beginning handshake with device at " + host);
    	out.writeByte(17);
    	System.out.println("Sleeping to wait for response");
    	Thread.sleep(250);
    	System.out.println("Woken up");
    	int shake = 0;
    	if(in.available() > 0){
    		shake = in.readByte();
    	} else {
    		System.out.println("invalid or no handshake recieved back: " + shake);
    		return false;
    	}
    	
    	System.out.println("recieved back: " + shake);
    	if (shake == 12){
    		for(int i = 0; i < 4; i++){
    			deviceID[i] = (byte)in.read();
    			System.out.println("device id part "+ (i+1) + " is " + deviceID[i] + 
    					" binary form:  " + Integer.toBinaryString(deviceID[i]));
    		}
    		
			byte rawFuncs[] = readMessage(50);
			for (int i = 0; i < rawFuncs.length; i++){
				System.out.println(rawFuncs[i]);
				funcs.add(rawFuncs[i]);
			}
			return true;
    	} else {
    		return false;
    	}
    	
    }
    
    public void start() {
        try{
	        this.ElSocketo = new Socket();
	        ElSocketo.connect(new InetSocketAddress(host, 2812), 200);
	        this.out = new DataOutputStream(ElSocketo.getOutputStream());
	        this.in = new DataInputStream(ElSocketo.getInputStream());
        } catch (IOException e) {
        	System.out.println("couldn't open socket with host: " + host + " on port: " + port);
            this.validDevice = false;
        	return;
        }
        try {
			this.validDevice = handShake();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't get IO info with device at: "+host+ " on port: "+port);
			//System.out.println("Device at: "+host+ " on port: "+port+" is invalid, terminating thread");
		}
        running = true;
        new Thread(this).start();
    }
    

    
    public byte[] readMessage(int timeout) throws IOException, Exception{
    	Thread.sleep(timeout);
    	if(in.available() == 0){
    		byte[] noData = {-1};
    		return noData;
    	}
    	int len = in.readByte();
    	System.out.println("recieved " + len + " for the message length");
    	Thread.sleep(len*15);
    	if(in.available() > 0){
        	byte bytesIn[] = new byte[len];
        	for (int i = 0; i < len; i++){
        		bytesIn[i] = in.readByte();
        		System.out.println("byte " + i + " is " + bytesIn[i]);
        	}
        	return bytesIn;
    	} else {
    		byte noByte[] = {0};
    		return noByte;
    	}
    	
    }
    
    public void writeOut(String input){
    	System.out.println("Sending message of " + input);
    	String inputSplit[] = input.split(" ");
    	byte bytesOut[] = new byte[inputSplit.length + 1];
        bytesOut[0] = (byte)bytesOut.length;
        System.out.println("Sending: " + bytesOut[0] + "(this is the length of the message)"); 
        for(int i = 1; i < bytesOut.length; i++){
            try{
                if(Integer.parseInt(inputSplit[i-1]) > 127){
                    bytesOut[i] = (byte)(Integer.parseInt(inputSplit[i-1]) & 0xFF);
                } else {
                    bytesOut[i] = Byte.parseByte(inputSplit[i-1]);
                }
                System.out.println("Sending: " + bytesOut[i] + " | " + Integer.toBinaryString(bytesOut[i]));
            } catch (NumberFormatException e){
                System.out.println("Something went wrong, an non-int got to the bytes out stage!");
            }
        }
        
        try {
			out.write(bytesOut);
		} catch (IOException e) {
			System.out.println("couldn't write over socket outputstream");
			e.printStackTrace();
		}
    }
    
    @Override
    public synchronized void run(){
    	while(running){
    		while(!queue.isEmpty()){
    			writeOut(queue.poll());
    		}
    		
    		try {
    			this.wait();
    		} catch (InterruptedException e){
    			terminate();
    		}
    		
    	}
    }
    
    public void setName(String name){
    	char nameArray[] = name.toCharArray();
    	byte charBytes[] = new byte[nameArray.length];
    	
    	try{
    		for(int i = 0; i < charBytes.length; i++){
        		charBytes[i] = (byte)nameArray[i];
        	}
    		String out = "";
    		for(int i = 0; i < charBytes.length; i++){
    			out = out + " " + (int)charBytes[i];
    		}
    		
    		out = "1 6 " + charBytes.length + out;
    		this.sendMessage(out);
    	} catch (Exception e){
    		
    	}
    }
    
    public String getName(){
    	this.sendMessage("1 8");
    	String name = "No name";
    	
    	try {
			byte nameBytes[] = this.readMessage(100);
			for(int i = 0; i < nameBytes.length; i++){
				System.out.println((int)nameBytes[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return name;
    }
    
    
    public void terminate(){
    	try {
			ElSocketo.close();
		} catch (IOException e) {
			System.out.println("couldn't close socket but who care, shits already gone yo");
			e.printStackTrace();
		}
    	running = false;
    }
    
    public boolean isRunning(){
    	return running;
    }
    
    public synchronized void sendMessage(String msg) {
        queue.add(msg);
        this.notify();
    }
    
    public boolean isValid(){
    	return this.validDevice;
    }
    public String getHost(){
    	return this.host;
    }
    public ArrayList<Byte> getFuncs(){
    	return this.funcs;
    }
    
    public byte[] getDeviceID(){
    	return this.deviceID;
    }
    
    public String getHumanName(){
    	return this.humanName;
    }
    
    
}