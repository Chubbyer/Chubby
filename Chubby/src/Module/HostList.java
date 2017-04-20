package Module;

import java.util.ArrayList;
import java.util.Random;

/*
 * @Leung
 * 数据处理机Chubbyer
 */
public class HostList {
	private static Host[] hosts={
			new Host("127.0.0.1",10001),
			new Host("127.0.0.1",10001),
			new Host("127.0.0.1",10002),
	};
	public static int hostsCount=hosts.length;
	public static Host nextHost() {
		Random r=new Random();
		int index=r.nextInt(hosts.length);
		if(index<hosts.length)
			return hosts[index];
		return null;
	}
}
