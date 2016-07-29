package com.HomeConnect.gui;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class networkScanner{
	
	public static ArrayList<scanResult> scan(int port) throws InterruptedException, ExecutionException{
		  final ExecutorService es = Executors.newFixedThreadPool(200);
		  final String baseIp = "192.168.0.";
		  final int timeout = 200;
		  List<Future<scanResult>> futures = new ArrayList<>();
		  
		  for (int i = 0; i <= 255; i++) {
		    futures.add(portIsOpen(es, (baseIp+i), port, timeout));
		  }
		  es.shutdown();
		  ArrayList<scanResult> results = new ArrayList<scanResult>();
		  boolean allDone = false;
		  while(!allDone){
			  allDone = true;
			  for (int i = 0; i < futures.size(); i++){
				  if(futures.get(i).isDone() == false){
					  allDone = false;
				  }
			  }
		  }
		  System.out.println("all scanning done");
		  System.out.println((futures.size()));
		  for (int i = 0; i < futures.size(); i++) {
			    results.add(futures.get(i).get());
		  }
		  return results;
		}
	
	public static Future<scanResult> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
		  return es.submit(new Callable<scanResult>() {
		      @Override public scanResult call() {
		        try {
		          Socket socket = new Socket();
		          System.out.println("about to connect to " + ip + " on port " + port);
		          socket.connect(new InetSocketAddress(ip, port), timeout);
		          socket.close();
		          return new scanResult(ip, port, true);
		        } catch (Exception ex) {
		          return new scanResult(ip, port, false);
		        }
		      }
		   });
		}
}