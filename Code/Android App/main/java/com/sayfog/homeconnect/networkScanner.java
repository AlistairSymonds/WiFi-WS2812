package com.sayfog.homeconnect;
import android.os.AsyncTask;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class networkScanner implements Runnable {

	static ArrayList<espDevice> deviceList = new ArrayList<espDevice>();
	boolean isCheckingPorts = true;
	boolean deviceListSafe;

	private ConcurrentLinkedQueue<String> queue;
	private volatile boolean running = false;
	private Thread t;

	public void start(){
		this.queue = new ConcurrentLinkedQueue<String>();
		running = true;
		t = new Thread(this);
		t.setName("NETWORK_THREAD");
		t.start();
		System.out.println("Started: " + t.getName());
	}

	@Override
	public synchronized void run(){
		while(running){
			while(!queue.isEmpty()){
				String cmd = queue.poll();
				System.out.println("Command on: "+ t.getName()+ " is " + cmd);

				switch (cmd){
					case "SCAN_AND_ADD": scanAndAdd();
					break;
				}

			}
			try {
				this.wait();
			} catch (InterruptedException e){
				terminate();
			}
		}
	}

	public void terminate(){
		running = false;
	}

	public synchronized void addCmd(String msg) {
		queue.add(msg);
		this.notify();
	}


	public ArrayList<espDevice> scanAndAdd(){ //entry method

		deviceListSafe = false;
		/*
		this section removes and disconnects all currently connected devices
		plans to make this ignore these devices in the future
		 */

		for(int i = 0; i < deviceList.size(); i++){
			deviceList.get(i).sendMessage("9");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			deviceList.get(i).terminate();
			System.out.println(deviceList.get(i).isRunning());
		}
		deviceList.clear();
		//all devices have now been removed

		isCheckingPorts = true;
		ScanTask scanner = new ScanTask();
		scanner.execute(2812);

		while (isCheckingPorts){
			//kill time
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("BACK FROM THE SCAN");

		//devicesList has been created, do gui stuff
		//MainActivity.addList(deviceList);
		deviceListSafe = true;
		return deviceList;

	}


	private void addDevices (ArrayList<scanResult> results){

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

	}

	private ArrayList<scanResult> scanPorts(int port) throws InterruptedException, ExecutionException {
		isCheckingPorts = true;
		boolean allFuturesDone = false;

		final ExecutorService es = Executors.newFixedThreadPool(200);
		final String baseIp = "192.168.0.";
		final int timeout = 200;

		List<Future<scanResult>> futures = new ArrayList<>();
		ArrayList<scanResult> results = new ArrayList<scanResult>();

		for (int i = 0; i <= 255; i++) {
			futures.add(portIsOpen(es, (baseIp + i), port, timeout));
		}
		es.shutdown();



		while (!allFuturesDone) {
			allFuturesDone = true;
			for (int i = 0; i < futures.size(); i++) {
				if (futures.get(i).isDone() == false) {
					allFuturesDone = false;
				}
			}
		}


		for (int i = 0; i < futures.size(); i++) {
			results.add(futures.get(i).get());
		}

		isCheckingPorts = false;
		return results;
	}

	private static Future<scanResult> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
		return es.submit(new Callable<scanResult>() {
			@Override
			public scanResult call() {
				try {
					Socket socket = new Socket();
					//System.out.println("about to connect to " + ip + " on port " + port);
					socket.connect(new InetSocketAddress(ip, port), timeout);
					socket.close();
					return new scanResult(ip, port, true);
				} catch (Exception ex) {
					return new scanResult(ip, port, false);
				}
			}
		});
	}

	private class ScanTask extends AsyncTask<Integer, Void, ArrayList<scanResult>> {
		protected ArrayList<scanResult> doInBackground(Integer... ints) {
			ArrayList<scanResult> out = new ArrayList<scanResult>();
			try {
				isCheckingPorts = true;
				out = scanPorts(2812);
				while (isCheckingPorts){

				}
				addDevices(out);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return out;
		}

		@Override
		protected void onPostExecute(ArrayList<scanResult> scanResults) {
			isCheckingPorts = false;

		}
	}
}