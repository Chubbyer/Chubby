package Module;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Util.MongoDBJDBC;

public class WebAnalyzer {
	public ArrayList<Chubbyer> webNodes1 = new ArrayList<Chubbyer>();
	public ArrayList<Chubbyer> webNodes2 = new ArrayList<Chubbyer>();
	public ArrayList<Chubbyer> webNodes3 = new ArrayList<Chubbyer>();
	public ArrayList<Chubbyer> browsers = new ArrayList<Chubbyer>();
	public ArrayList<Chubbyer> onlineTimes = new ArrayList<Chubbyer>();
	public String host;

	public WebAnalyzer(String host) {
		// TODO Auto-generated constructor stub
		this.host = host;
	}

	public void nodesCount() {
		MongoDBJDBC mongoer = new MongoDBJDBC(host);
		mongoer.connectionMongoDB();
		ArrayList<WebVisiter> webVisiters = mongoer.findWebVisiters("WebLogs",
				1, 2768);
		WebVisiter visiter = null;
		Date preDate = null, latDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			for (int i = webVisiters.size(); i > 1; i--) {
				visiter = webVisiters.get(i - 2);
				preDate = sdf.parse(visiter.visit_time);
				latDate = sdf.parse(webVisiters.get(i - 1).visit_time);
				double useTime = Math.round((preDate.getTime() - latDate
						.getTime()) / (60 * 1000));// 分钟
				if (useTime > 30)
					useTime = 30;
				if (onlineTimes.size() > 0) {
					if (onlineTimes.get(onlineTimes.size() - 1).day
							.equals(formatter.format(preDate))) {
						onlineTimes.get(onlineTimes.size() - 1).point += useTime;
					} else {
						onlineTimes.add(new Chubbyer(formatter.format(preDate),
								useTime));
					}
				} else {
					onlineTimes.add(new Chubbyer(formatter.format(preDate),
							useTime));
				}
				String[] nodes = this.urlParser(visiter.url);
				if (nodes.length > 0) {
					this.webNodes1 = WebAnalyzer.remoneRepChubbyers(webNodes1,
							new Chubbyer(nodes[nodes.length - 1], 1));
				}
				if (nodes.length > 1) {
					this.webNodes2= WebAnalyzer.remoneRepChubbyers(webNodes2,new Chubbyer(nodes[nodes.length - 2], 1));
				}
				this.browsers= WebAnalyzer.remoneRepChubbyers(browsers,new Chubbyer(visiter.web_browser, 1));
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] urlParser(String url) {
		String[] strings = url.split("//");
		if (strings.length > 1) {
			strings = strings[1].split("/");
			if (strings.length > 0)
				strings = strings[0].replace(".", "/").split("/");
		}
		return strings;
	}

	public Date dateParser(String visit_time) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println(sdf.format(date));
		try {
			date = sdf.parse(visit_time);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Chubbyer> remoneRepChubbyers(
			ArrayList<Chubbyer> chubbyers, Chubbyer chubbyer) {
		if (chubbyers == null)
			return null;
		boolean join = true;
		for (int i = 0; i < chubbyers.size(); i++) {
			if (chubbyers.get(i).day.equals(chubbyer.day)) {
				chubbyers.get(i).point += 1;
				join = false;
			}
		}
		if (join)
			chubbyers.add(chubbyer);
		return chubbyers;
	}
	public static ArrayList<Chubbyer> sortChubbyers(
			ArrayList<Chubbyer> chubbyers) {
		if (chubbyers.size() > 0) {
			ArrayList<Chubbyer> finallChubbyers = new ArrayList<Chubbyer>();
			int minIndex = 0;
			int size = chubbyers.size();
			for (int i = 0; i < size - 1; i++) {
				// 每一次找到列表中Point最小的那个
				double minPoint = chubbyers.get(0).point;
				for (int j = 0; j < chubbyers.size(); j++) {
					if (chubbyers.get(j).point > minPoint) {
						minPoint = chubbyers.get(j).point;
						minIndex = j;
					}
				}
				finallChubbyers.add(chubbyers.get(minIndex));
				chubbyers.remove(minIndex);
				minIndex = 0;
			}
			// 剩下的最后一个就是Piont最大的那个
			finallChubbyers.add(chubbyers.get(0));
			return finallChubbyers;
		}
		return null;
	}

	/*
	 * 补充缺省的Chubbyer，可能某些天我们可能没使用电脑，就不存在对应的数据 这里我们找出这些缺省的，默认的使用时间为0
	 */
	public static ArrayList<Chubbyer> supplementChubbyers(
			ArrayList<Chubbyer> chubbyers) {
		if (chubbyers == null)
			return null;
		ArrayList<Chubbyer> finalChubbyers = new ArrayList<Chubbyer>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long longTime1, longTime2;
		try {
			for (int i = 0; i < chubbyers.size(); i++) {
				Date date1;
				date1 = df.parse(chubbyers.get(i).day);
				longTime1 = date1.getTime();
				finalChubbyers.add(chubbyers.get(i));
				for (int j = i + 1; j < chubbyers.size(); j++) {
					Date date2 = df.parse(chubbyers.get(j).day);
					longTime2 = date2.getTime();
					int k = 0;
					k = (int) ((longTime2 - longTime1) / (24 * 60 * 60 * 1000));
					if (k > 1) {
						for (int y = 0; y < k - 1; y++) {
							long t = 24 * 60 * 60 * 1000 * (y + 1);
							Date d = new Date(longTime1 + t);
							String day = df.format(d);
							Chubbyer c = new Chubbyer(day, 0);
							finalChubbyers.add(c);
						}
						break;
					} else
						break;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalChubbyers;
	}
	public static void main(String[] args) {
		WebAnalyzer webAnalyzer = new WebAnalyzer("Leung");
		webAnalyzer.nodesCount();
		System.out.println(webAnalyzer.onlineTimes.size());
		MongoDBJDBC mongoer=new MongoDBJDBC("Leung");
		mongoer.writeOnlineTimes("WebOnline", webAnalyzer.onlineTimes);
		mongoer.writeBrowserInfo("WebBrowser", webAnalyzer.browsers);
		webAnalyzer.webNodes1=WebAnalyzer.sortChubbyers(webAnalyzer.webNodes1);
		mongoer.writeNodes("WebNode1", webAnalyzer.webNodes1);
		webAnalyzer.webNodes2=WebAnalyzer.sortChubbyers(webAnalyzer.webNodes2);
		mongoer.writeNodes("WebNode2", webAnalyzer.webNodes2);
//		for (Chubbyer chubbyer : webAnalyzer.onlineTimes) {
//			System.out.println("Day:" + chubbyer.day + " Time:"
//					+ chubbyer.point);
//		}
//		System.out.println(webAnalyzer.browsers.size());
//
//		for (Chubbyer chubbyer : webAnalyzer.browsers) {
//			System.out.println("Browsers:" + chubbyer.day + " Times:"
//					+ chubbyer.point);
//		}
//		webAnalyzer.webNodes2=WebAnalyzer.sortChubbyers(webAnalyzer.webNodes2);
//		for (Chubbyer chubbyer : webAnalyzer.webNodes2) {
//			System.out.println("webNodes1:" + chubbyer.day + " Times:"
//					+ chubbyer.point);
//		}
	}
}
