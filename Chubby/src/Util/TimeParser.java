package Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeParser {

	public static String getTimeStr(String str) {
		String formatTimeStr=str.substring(0, 19).replace('T', ' ');
		return formatTimeStr;
	}
	public static String getNowTimeStr() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	public static void main(String[] args) {
		System.out.println(TimeParser.getTimeStr("2017-04-18T14:49:02.667513600Z"));
	}
}
