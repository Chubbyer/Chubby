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
 * ��������ڶ�Ĺ���Chubbyer�ĸ�ʽת�����ӹ�
 */
public class ChubbyerParser {
	/*
	 * �ӹ�ArrayList<String> chubbyerString����Ľ��
	 * �����chubbyerString�ǹ�������{"ot":"2017-05-12 12:11:10"
	 * ,"ct":"2017-05-12 12:11:10"}���б� ������ҳ����չʾEC-301_1����Ľ��,�õ�ÿ��ʹ�ö���Сʱ
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
			System.out.println("Client�˵�JSON��������");
		}
		return null;
	}

	/*
	 * �ӹ�ArrayList<String> chubbyerString����Ľ��
	 * �����chubbyerString�ǹ�������{"ot":"2017-05-12 12:11:10"
	 * ,"ct":"2017-05-12 12:11:10"}���б� ������ҳ����չʾEC-301_3����Ľ��,�õ����ػ�ʱ���
	 * ���ص��б���ǰ�벿���ǿ���ʱ�㣬��벿���ǹػ�ʱ��
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
			System.out.println("Client�˵�JSON��������");
		}
		return null;
	}

	/*
	 * �ӹ�ArrayList<String> chubbyerString����Ľ��
	 * �����chubbyerString�ǹ�������{"name":"����","hours":3.2}���б�
	 * ������ҳ����չʾEC-301_3����Ľ��,�õ����ػ�ʱ���
	 */
	public static ArrayList<Chubbyer> sortChubbyersForRanking(
			ArrayList<String> chubbyerString) {
		return null;

	}

	/*
	 * ����ȱʡ��Chubbyer������ĳЩ�����ǿ���ûʹ�õ��ԣ��Ͳ����ڶ�Ӧ������ ���������ҳ���Щȱʡ�ģ�Ĭ�ϵ�ʹ��ʱ��Ϊ0
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
	 * ����ĳЩ������ʹ���˺ü��ε��ԣ�����ʹ��ڶ��Chubbyer �������Ǻϲ���Щ������ʹ��ʱ�����һ��
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
			System.out.println("Chubbyer�ϲ�ʧ��");
		}
		finalChubbyers1.add(chubbyers.get(chubbyers.size() - 1));
		return finalChubbyers1;
	}
}
