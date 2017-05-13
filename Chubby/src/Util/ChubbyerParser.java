package Util;

import java.util.ArrayList;

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
	 * 这里的chubbyerString是关于诸如{"ot":"2017-05-12 12:11:10","ct":"2017-05-12 12:11:10"}的列表
	 * 方便在页面上展示EC-301_1任务的结果,得到每天使用多少小时
	 */
	public static ArrayList<Chubbyer> getUseTime(ArrayList<String> chubbyerString) {
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
}
