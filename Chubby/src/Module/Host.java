package Module;

public class Host {
	private String hostName;
	private String ip;
	private String status;
	public Host(String hostName,String ip,String status) {
		this.hostName=hostName;
		this.ip=ip;
		this.status=status;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
