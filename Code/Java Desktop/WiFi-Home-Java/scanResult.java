public class scanResult{
	private int[] IPV4;
	private int port;
	private boolean isOpen;
	private String ip;
	
	public scanResult(String ip, int port, boolean isOpen){
		this.ip = ip;
		this.port = port;
		this.isOpen = isOpen;
		String temp[] = ip.split(".");
		/*for (int i = 0; i < 3; i++){
			IPV4[i] = Integer.parseInt(temp[i]);
		}*/
	}
	
	public String getIP(){
		return this.ip;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public boolean isOpen(){
		return this.isOpen;
	}
	
	public int[] getIPV4(){
		return this.IPV4;
	}
}