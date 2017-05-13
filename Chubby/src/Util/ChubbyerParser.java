package Util;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import Module.Chubbyer;

/*
 * @Leung
 * ��������ڶ�Ĺ���Chubbyer�ĸ�ʽת�����ӹ�
 */
public class ChubbyerParser {
	/*
	 * �ӹ�ArrayList<String> chubbyerString����Ľ��
	 * �����chubbyerString�ǹ�������{"ot":"2017-05-12 12:11:10","ct":"2017-05-12 12:11:10"}���б�
	 * ������ҳ����չʾEC-301_1����Ľ��,�õ�ÿ��ʹ�ö���Сʱ
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
			System.out.println("Client�˵�JSON��������");
		}
		return null;
	}
}
