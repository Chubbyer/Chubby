package Module;

public class User {
	private String name;
	private String sno;
	private String sys;
	private String host;
	private long logLines;
	private boolean flag;//true表示已从日志文件中提取数据到MongoDB中
	private boolean R_Flag;//true表示已从MongoDB中分析出关键数据并存储在数据库中
	private String open_Id;
	private String close_Id;
	private boolean web_Flag;
	private long webLogLines;
	public boolean getWeb_Flag() {
		return web_Flag;
	}
	public void setWeb_Flag(Boolean web_Flag) {
		this.web_Flag = web_Flag;
	}
	public long getWebLogLines() {
		return webLogLines;
	}
	public void setWebLogLines(long webLogLines) {
		this.webLogLines = webLogLines;
	}
	public void setLogLines(long logLines) {
		this.logLines = logLines;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSno() {
		return sno;
	}
	public void setSno(String sno) {
		this.sno = sno;
	}
	public String getSys() {
		return sys;
	}
	public void setSys(String sys) {
		this.sys = sys;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public long getLogLines() {
		return logLines;
	}
	public void setLogLines(Long logLines) {
		this.logLines = logLines;
	}
	public boolean getFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public boolean getR_Flag() {
		return R_Flag;
	}
	public void setR_Flag(boolean r_Flag) {
		R_Flag = r_Flag;
	}
	public String getOpen_Id() {
		return open_Id;
	}
	public void setOpen_Id(String open_Id) {
		this.open_Id = open_Id;
	}
	public String getClose_Id() {
		return close_Id;
	}
	public void setClose_Id(String close_Id) {
		this.close_Id = close_Id;
	}
	
}
