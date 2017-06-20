package Control;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import Module.Event;
import Module.OrderChubbyer;
import Module.User;
import Protocol.EC;
import Util.MongoDBJDBC;

/*
 * @Leung
 * 专门用于分析日志事件，多线程
 * 这说明你可以利用Analyzer同时进行多种操作
 */
public class Analyzer implements Callable<Object> {
	private String eType;// 标记执行操作的类型
	private User user;
	private int oType;
	private int threadNum;// 线程序号

	public Analyzer(String eType, User user, int oType, int threadNum) {
		this.eType = eType;
		this.user = user;
		this.oType = oType;
		this.threadNum = threadNum;
	}

	/*
	 * 从MongoDB中提取日志事件的数据，分析出该用户的开关机记录
	 */
	public void name() {

	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		if (oType == 2) {
			// System.out.println("Thread:" + threadNum);
			ArrayList<String> chubbyers = this.getChubbyers(user.getHost(),
					threadNum);
			// Thread.sleep(2000);
			OrderChubbyer<String> orderChubbyer = new OrderChubbyer<String>(
					threadNum, chubbyers);
			return orderChubbyer;
		}
		return null;
	}

	/*
	 * @Leung 从数据库给定数量Event中分析所有的Chubbyer 整个系统的核心所在
	 */
	public ArrayList<String> getChubbyers(String hostName, int threadNum) {
		MongoDBJDBC mongoer = MongoDBJDBC.createMongoger(hostName);
		int readLines = (int) (this.user.getLogLines());
		if (readLines > 0) {
			int startIndex, endIndex;
			startIndex = readLines / 10 * threadNum;
			if (threadNum < 9) {
				endIndex = readLines / 10 * (threadNum + 1);
			} else {
				endIndex = readLines;
			}
			mongoer.connectionMongoDB();
			System.out.println("线程" + threadNum + "正在检索[" + startIndex + "-"
					+ endIndex + "]");
			ArrayList<Event> events = mongoer.findEvents("Security",
					startIndex, endIndex);
			mongoer.closeMongoDB();
			if (events.size() > 0) {
				ArrayList<String> chubbyers = new ArrayList<String>();
				String openId = user.getOpen_Id();// 标记该用户的计算机的开机事件ID
				String closeId = user.getClose_Id();// 标记该用户的计算机的关机事件ID
				for (int i = 0; i < events.size(); i++) {
					String chubbyer = null;
					if (events.get(i).getEventID().equals(closeId)) {
						String closeTime = events.get(i).getTimeCreated();
						for (int j = i + 1; j < events.size(); j++) {
							if (events.get(j).getEventID().equals(closeId)) {
								for (int k = j; k > i; k--) {
									if (events.get(k).getEventID()
											.equals(openId)) {
										String openTime = events.get(k)
												.getTimeCreated();
										chubbyer = "{'ot':'" + openTime
												+ "','ct':'" + closeTime + "'}";
										chubbyers.add(chubbyer);
										break;
									}
								}
								i = j;
								break;
							}
						}
					}
				}
				return chubbyers;
			}
		}
		System.out.println("Analyer:线程" + threadNum + "执行失败");
		return null;

	}

	public static void main(String[] args) {
		// MongoDBJDBC mongoer=new MongoDBJDBC("Leung");
		// mongoer.connectionMongoDB();
		// ArrayList<Event> events = mongoer.findEvents("Security",
		// 0, 1000);
		// ArrayList<String> chubbyers=new ArrayList<String>();
		// String openId="4798";
		// String closeId="4647";
		// System.out.println(events.size());
		// for (int i = 0; i < events.size(); i++) {
		// String chubbyer=null;
		// //closeIndex=i;
		// if(events.get(i).getEventID().equals(closeId)){
		//
		// System.out.println("i:"+i+" "+events.get(i).getEventID());
		//
		// String openTime=events.get(i).getTimeCreated();
		// for (int j = i+1; j < events.size(); j++) {
		// if(events.get(j).getEventID().equals(closeId)){
		// System.out.println("j:"+j+" "+events.get(j).getEventID());
		// for (int k = j; k >i; k--) {
		// if(events.get(k).getEventID().equals(openId)){
		// System.out.println("k:"+k+" "+events.get(k).getEventID());
		// String closeTime=events.get(k).getTimeCreated();
		// chubbyer="['ot':'"+openTime+"','ct':'"+closeTime+"']";
		// chubbyers.add(chubbyer);
		// break;
		// }
		//
		// }
		// i=j;
		// break;
		// }
		// }
		// }
		// }
		// for (String string : chubbyers) {
		// System.out.println("Chubbyer:"+string);
		// }
		// mongoer.closeMongoDB();
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		User user = mongoer.findUserInfo("Leung");
		Analyzer analyzer = new Analyzer(EC.E_301, user, 2, 3);
		ArrayList<String> aList = analyzer.getChubbyers("Leung", 3);
		for (int i = 0; i < 50; i++) {
			System.out.println(aList.get(0));
		}
	}
}
