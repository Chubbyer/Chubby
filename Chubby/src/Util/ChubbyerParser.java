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
		if(chubbyerString==null)
			return null;
		ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
		//System.out.println(chubbyerString.size());
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
		if(chubbyerString==null)
			return null;
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
	 * ������ҳ����չʾEC-301_3����Ľ��,�õ����ػ�ʱ���,������������
	 */
	public static ArrayList<Chubbyer> sortChubbyersForRanking(
			ArrayList<String> chubbyerString) {
		if (chubbyerString != null) {
			try {
				JSONObject jsonObj = null;
				ArrayList<Chubbyer> chubbyers=new ArrayList<Chubbyer>();
				for (int i = 0; i < chubbyerString.size(); i++) {
					jsonObj = new JSONObject(chubbyerString.get(i));
					chubbyers.add(new Chubbyer(jsonObj.getString("name"), jsonObj.getDouble("hours")));
				}
				ArrayList<Chubbyer> finallChubbyers=new ArrayList<Chubbyer>();
				int minIndex = 0;
				int size = chubbyers.size();
				for (int i = 0; i < size - 1; i++) {
					// ÿһ���ҵ��б���Point��С���Ǹ�
					double minPoint = chubbyers.get(0).point;
					for (int j = 0; j < chubbyers.size(); j++) {
						if (chubbyers.get(j).point < minPoint) {
							minPoint = chubbyers.get(j).point;
							minIndex = j;
						}
					}
					finallChubbyers.add(chubbyers.get(minIndex));
					chubbyers.remove(minIndex);
					minIndex = 0;
				}
				// ʣ�µ����һ������Piont�����Ǹ�
				finallChubbyers.add(chubbyers.get(0));
				return finallChubbyers;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * �ӹ�ArrayList<String> chubbyerString����Ľ��
	 * �����chubbyerString�ǹ�������{"ot":"2017-05-12 12:11:10"
	 * ,"ct":"2017-05-12 12:11:10"}���б�,�õ�ʹ��ʱ����һ���еķֲ�
	 */
	public static ArrayList<Double> getUseHoursDistribut(
			ArrayList<String> chubbyerString) {
		if(chubbyerString==null)
			return null;
		try {
			double[] temp;
			double morning = 0, aftermoon = 0, evening = 0;
			JSONObject jsonObj = null;
			Chubbyer open = new Chubbyer();
			Chubbyer close = new Chubbyer();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date oDate = null, cDate = null;
			for (int i = 0; i < chubbyerString.size(); i++) {
				jsonObj = new JSONObject(chubbyerString.get(i));
				open = TimeParser.getTimeScatter(jsonObj.getString("ot"));
				close = TimeParser.getTimeScatter(jsonObj.getString("ct"));
				// System.out.println("OP:"+open.point);
				// System.out.println("CP:"+close.point);
				oDate = sdf.parse(open.day);
				cDate = sdf.parse(close.day);
				if (oDate.equals(cDate)) {
					// ͬһ��
					temp = ChubbyerParser.getDayHoursDistribut(open.point,
							close.point);
					morning += temp[0];
					aftermoon += temp[1];
					evening += temp[2];
				} else {
					int intervalDays = cDate.getDay() - oDate.getDay();// �������,����=1
					temp = ChubbyerParser.getDayHoursDistribut(open.point, 24);
					morning += temp[0];
					aftermoon += temp[1];
					evening += temp[2];
					temp = ChubbyerParser.getDayHoursDistribut(0, close.point);
					morning += temp[0];
					aftermoon += temp[1];
					evening += temp[2];
					for (int j = 0; j < intervalDays - 1; j++) {
						morning += 6;
						aftermoon += 7;
						evening += 11;
					}
				}
			}
			morning = Math.round(morning * 10) / 10.0;
			aftermoon = Math.round(aftermoon * 10) / 10.0;
			evening = Math.round(evening * 10) / 10.0;
//			System.out.println("M:" + morning);
//			System.out.println("A:" + aftermoon);
//			System.out.println("E:" + evening);
			ArrayList<Double> ret = new ArrayList<Double>();
			ret.add(morning);
			ret.add(aftermoon);
			ret.add(evening);
//			System.out.println("CP:" + ret);
			return ret;
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * ����׼�Ŀ��ػ�������ͬһ����������ʱ��ֲ�
	 */
	public static double[] getDayHoursDistribut(double open, double close) {
		double morning = 0, aftermoon = 0, evening = 0;
		if (close <= 6 && open < close) {
			// ���ػ�������6����ǰ
			evening += close - open;
		}
		if (open <= 6 && 6 < close && close <= 12) {
			// ������6��֮ǰ���ػ���6����12��֮��
			evening += 6 - open;
			morning += close - 6;
		}
		if (open <= 6 && 12 < close && close <= 19) {
			// ������6����ǰ���ػ���12����19��֮��
			morning += 6;
			aftermoon += close - 12;
			evening+=6-open;
		}
		if (open <= 6 && 19 < close && close <= 24) {
			// ������6����ǰ���ػ���19����24��֮��
			evening += 6 - open;
			morning += 6;
			aftermoon += 7;
			evening += close - 19;
		}
		// -------------
		if (6 < open && close <= 12 && open < close) {
			// ���ػ�������12����ǰ
			morning += close - open;
		}
		if (6 < open && open <= 12 && 12 < close && close <= 19) {
			// ������12����ǰ���ػ���12����19��֮��
			morning += 12 - open;
			aftermoon += close - 12;
		}
		if (6 < open && open <= 12 && 19 < close && close <= 24) {
			// ������12����ǰ���ػ���19����24��֮��
			morning += 12 - open;
			aftermoon += 7;
			evening += close - 19;
		}
		// ---------------
		if (6 < open && 12 < open && close <= 19 && open < close) {
			// ���ػ�������12����19��֮��
			aftermoon += close - open;
		}
		if (6 < open && 12 < open && open <= 19 && 19 < close && close <= 24) {
			// ������12����19��֮�䣬�ػ���19����24��֮��
			aftermoon += 19 - open;
			evening += close - 19;
		}
		// ----------------
		if (19 < open && close <= 24 && open < close) {
			// ���ػ�����19����24��֮��
			evening += close - open;
		}
		double[] distribut = { morning, aftermoon, evening };
		return distribut;
	}

	/*
	 * ����ȱʡ��Chubbyer������ĳЩ�����ǿ���ûʹ�õ��ԣ��Ͳ����ڶ�Ӧ������ ���������ҳ���Щȱʡ�ģ�Ĭ�ϵ�ʹ��ʱ��Ϊ0
	 */
	public static ArrayList<Chubbyer> supplementChubbyers(
			ArrayList<Chubbyer> chubbyers) {
		if(chubbyers==null)
			return null;
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

	/*
	 * ����ĳЩ������ʹ���˺ü��ε��ԣ�����ʹ��ڶ��Chubbyer �������Ǻϲ���Щ������ʹ��ʱ�����һ��
	 */
	public static ArrayList<Chubbyer> remoneRepChubbyers(
			ArrayList<Chubbyer> chubbyers) {
		if (chubbyers==null)
			return null;
		ArrayList<Chubbyer> finalChubbyers1 = new ArrayList<Chubbyer>();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		long longTime1, longTime2;
		try {
			for (int i = 0; i < chubbyers.size(); i++) {
				// System.out.println("i:" + chubbyers.get(i).day);
				Date date1;
				date1 = df.parse(chubbyers.get(i).day);
				longTime1 = date1.getTime();
				for (int j = i + 1; j < chubbyers.size(); j++) {
					Date date2 = df.parse(chubbyers.get(j).day);
					// System.out.println("j:" + chubbyers.get(j).day);
					longTime2 = date2.getTime();
					int k = 0;
					k = (int) ((longTime2 - longTime1) / (24 * 60 * 60 * 1000));
					// System.out.println("K=" + k);
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

	public static void main(String[] args) {
		double[] d = ChubbyerParser.getDayHoursDistribut(0, 24);
		// System.out.println("M:" + d[0]);
		// System.out.println("A:" + d[1]);
		// System.out.println("E:" + d[2]);
		String string = "{\"ot\":\"2017-05-12 11:11:10\",\"ct\":\"2017-05-12 12:11:10\"}";
		ArrayList<String> al = new ArrayList<String>();
		al.add(string);
		ArrayList<Double>dd = ChubbyerParser.getUseHoursDistribut(al);
		System.out.println("M:" + dd.get(0));
		System.out.println("A:" + dd.get(1));
		System.out.println("E:" + dd.get(2));
//		String string1="{\"name\":\"����\",\"hours\":3.2}";
//		String string2="{\"name\":\"Leung\",\"hours\":2.2}";
//		String string3="{\"name\":\"����\",\"hours\":4.2}";
//		ArrayList<String> al=new ArrayList<String>();
//		al.add(string1);
//		al.add(string2);
//		al.add(string3);
//		ArrayList<Chubbyer> chubbyers=new ArrayList<Chubbyer>();
//		chubbyers=ChubbyerParser.sortChubbyersForRanking(al);
//		for (Chubbyer chubbyer : chubbyers) {
//			System.out.println("Name:"+chubbyer.day+" Hours:"+chubbyer.point);
//		}
	}
}
