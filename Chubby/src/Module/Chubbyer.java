package Module;

import java.util.ArrayList;

/*
 * @Leung
 * 该类用于描述向前端展示的数据节点
 */
public class Chubbyer {
	public String day;// day属性可以用来表示某为同学PC使用记录的某一天，也可以表示某位同学，当表示某位同学时，point就表示平均使用时间
	public double point;

	public Chubbyer(String day, double point) {
		this.day = day;
		this.point = point;
	}
	public Chubbyer() {
		// TODO Auto-generated constructor stub
	}

	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public double getPoint() {
		return point;
	}
	public void setPoint(double point) {
		this.point = point;
	}
	public String toString() {
		return "[\"" + day + "\"" + "," + point + "]";
	}

	public static void main(String[] args) {
		ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
		Chubbyer c1 = new Chubbyer("2017-05-7", 21.0);
		System.out.println(c1.toString());
		for (int i = 0; i < 6; i++) {
			chubbyers.add(c1);
		}
		// String jsonStr=null;
		// ArrayList<String> days=new ArrayList<String>();
		// ArrayList<Double> points=new ArrayList<Double>();
		// for (Chubbyer chubbyer : chubbyers) {
		// days.add("\""+chubbyer.day+"\"");
		// points.add(chubbyer.point);
		// }
		// jsonStr="{"+"\"days\""+":"+days+","+"\"points\""+":"+points+"}";
		// System.out.println(jsonStr);
		// 把ArrayList转换成JSON
		String jsonStr = null;
		ArrayList<String> openPoints = new ArrayList<String>();
		ArrayList<String> closePoints = new ArrayList<String>();

		for (int i = 0; i < chubbyers.size(); i++) {
			if (i < chubbyers.size() / 2) {
				// 前半部分的数据是开机的节点
				openPoints.add(chubbyers.get(i).toString());
			} else {
				// 后半部分的数据是关机的节点
				closePoints.add(chubbyers.get(i).toString());
			}
		}
		// {"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}
		jsonStr = "{" + "\"openPoints\"" + ":" + openPoints + ","
				+ "\"closePoints\"" + ":" + closePoints + "}";
		System.out.println(jsonStr);
	}
}
