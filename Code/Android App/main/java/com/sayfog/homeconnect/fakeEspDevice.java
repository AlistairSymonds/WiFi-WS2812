package com.sayfog.homeconnect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by alist on 23/08/2016.
 */
public class fakeEspDevice implements Runnable{
    public String host = null;
    public int port;
    public byte deviceID[] = new byte[4];
    public ArrayList<Byte> funcs = new ArrayList<Byte>();
    public DataOutputStream out;
    public DataInputStream in;
    public Socket ElSocketo;
    public boolean validDevice = true;
    public String humanName;

    private volatile boolean running = false;
    private ConcurrentLinkedQueue<String> queue;

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
            System.out.println(bytesOut);
        } catch (Exception e) {
            System.out.println("couldn't write over socket outputstream");
            e.printStackTrace();
        }
    }

    public void terminate(){
        try {
            ElSocketo.close();
        } catch (IOException e) {
            System.out.println("couldn't close socket but who cares, closing anyway");
            e.printStackTrace();
        }
        running = false;
    }
}
