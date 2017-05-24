package Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import Module.Chubbyer;

/*
 * @Leung
 * 此类包含众多的关于Chubbyer的格式转换及加工
 */
public class ChubbyerParser {
	/*
	 * 加工ArrayList<String> chubbyerString对象的结果
	 * 这里的chubbyerString是关于诸如{"ot":"2017-05-12 12:11:10"
	 * ,"ct":"2017-05-12 12:11:10"}的列表 方便在页面上展示EC-301_1任务的结果,得到每天使用多少小时
	 */
	public static ArrayList<Chubbyer> getUseTime(
			ArrayList<String> chubbyerString) {
		ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
		System.out.println(chubbyerString.size());
		JSONObject jsonObj = null;
		try {
			for (int i = 0; i < chubbyerString.size(); i++) {
				jsonObj = new JSONObject(chubbyerString.get(i));
				chubbyers.add(TimeParser.getChubbyerFromString(
						jsonObj.getString("ot"), jsonObj.getString("ct")));
				// System.out.println(chubbyerString.get(i));
			}
			return chubbyers;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Client端的JSON解析出错");
		}
		return null;
	}

	/*
	 * 加工ArrayList<String> chubbyerString对象的结果
	 * 这里的chubbyerString是关于诸如{"ot":"2017-05-12 12:11:10"
	 * ,"ct":"2017-05-12 12:11:10"}的列表 方便在页面上展示EC-301_3任务的结果,得到开关机时间点
	 * 返回的列表中前半部分是开机时点，后半部分是关机时点
	 */
	public static ArrayList<Chubbyer> getUseTimeScatter(
			ArrayList<String> chubbyerString) {
		ArrayList<Chubbyer> openChubbyers = new ArrayList<Chubbyer>();
		ArrayList<Chubbyer> closeChubbyers = new ArrayList<Chubbyer>();
		JSONObject jsonObj = null;
		try {
			for (int i = 0; i < chubbyerString.size(); i++) {
				jsonObj = new JSONObject(chubbyerString.get(i));
				openChubbyers.add(TimeParser.getTimeScatter(jsonObj
						.getString("ot")));
				closeChubbyers.add(TimeParser.getTimeScatter(jsonObj
						.getString("ct")));
				// System.out.println(chubbyerString.get(i));
			}
			openChubbyers.addAll(closeChubbyers);
			return openChubbyers;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Client端的JSON解析出错");
		}
		return null;
	}

	/*
	 * 加工ArrayList<String> chubbyerString对象的结果
	 * 这里的chubbyerString是关于诸如{"name":"梁健","hours":3.2}的列表
	 * 方便在页面上展示EC-301_3任务的结果,得到开关机时间点
	 */
	public static ArrayList<Chubbyer> sortChubbyersForRanking(
			ArrayList<String> chubbyerString) {
		return null;

	}

	/*
	 * 补充缺省的Chubbyer，可能某些天我们可能没使用电脑，就不存在对应的数据 这里我们找出这些缺省的，默认的使用时间为0
	 */
	public static ArrayList<Chubbyer> supplementChubbyers(
			ArrayList<Chubbyer> chubbyers) {
		ArrayList<Chubbyer> finalChubbyers = new ArrayList<Chubbyer>();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
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
					}
					else
						break;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalChubbyers;
	}

	/*
	 * 可能某些天我们使用了好几次电脑，这天就存在多个Chubbyer 这里我们合并这些，并把使用时间加在一起
	 */
	public static ArrayList<Chubbyer> remoneRepChubbyers(
			ArrayList<Chubbyer> chubbyers) {
		ArrayList<Chubbyer> finalChubbyers1 = new ArrayList<Chubbyer>();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		long longTime1, longTime2;
		try {
			for (int i = 0; i < chubbyers.size(); i++) {
				System.out.println("i:" + chubbyers.get(i).day);
				Date date1;
				date1 = df.parse(chubbyers.get(i).day);
				longTime1 = date1.getTime();
				for (int j = i + 1; j < chubbyers.size(); j++) {
					Date date2 = df.parse(chubbyers.get(j).day);
					System.out.println("j:" + chubbyers.get(j).day);
					longTime2 = date2.getTime();
					int k = 0;
					k = (int) ((longTime2 - longTime1) / (24 * 60 * 60 * 1000));
					System.out.println("K=" + k);
					if (k != 0) {
						finalChubbyers1.add(chubbyers.get(i));
						break;
					} else if (k == 0) {
						chubbyers.get(j).point += chubbyers.get(i).point;
						break;
					} else
						break;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Chubbyer合并失败");
		}
		finalChubbyers1.add(chubbyers.get(chubbyers.size() - 1));
		return finalChubbyers1;
	}
}
