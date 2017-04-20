package Module;

public class Host {
	private String serverIP;
	private int port;
	public Host(String serverIp,int port){
		this.serverIP=serverIp;
		this.port=port;
	}
	public int getPort() {
		return port;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
