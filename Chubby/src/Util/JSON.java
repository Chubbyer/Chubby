package Util;

public class JSON {
	public String kv;
	public String jsonString;
	public JSON() {
		// TODO Auto-generated constructor stub
	}
	public String append(String key,Object value) {
		String jsonStr = ",\""+key+"\":" + value;
		//this.jsonString=this.jsonString+
		return jsonStr;
	}
}
