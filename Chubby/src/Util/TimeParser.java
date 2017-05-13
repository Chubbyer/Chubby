package Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.xml.crypto.Data;

import Module.Chubbyer;

import com.mongodb.client.model.geojson.Point;

public class TimeParser {

	/*
	 * 把日志文件中的时间字符串提取出常规格式
	 */
	public static String getTimeStr(String str) {
		String formatTimeStr = str.substring(0, 19).replace('T', ' ');
		return formatTimeStr;
	}

	public static String getNowTimeStr() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date);
	}

	/*
	 * 把后台数据服务器传过来的时间段调整过来并转换成在页面上展示的时间节点
	 */
	public static Chubbyer getChubbyerFromString(String startTime,
			String endTime) {
		Date stratDate = null;
		Date endDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		long timeLong;
		double point;
		try {
			stratDate = formatter.parse(startTime);// 开机时间
			endDate = formatter.parse(endTime);// 关机时间
			point = (endDate.getTime() - stratDate.getTime()) / 3600000.0;// 把毫秒调整为以小时衡量的指标
			timeLong = stratDate.getTime() + 1000 * 60 * 60 * 8;// 调整时区，加8个小时
			stratDate = new Date(timeLong);
			// return sdf.format(date);
			return new Chubbyer(sdf.format(stratDate),
					Math.round(point * 10) / 10.0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("时间转换出错");
			return null;
		}
	}
	/*
	 * 把后台数据服务器传过来的时间段调整过来并转换成在开关机时间点
	 */
	public static Chubbyer getTimeScatter(String timeString) {
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		long timeLong;
		double point;
		try {
			date = formatter.parse(timeString);
			timeLong = date.getTime() + 1000 * 60 * 60 * 8;// 调整时区，加8个小时
			date = new Date(timeLong);
			point = (date.getHours()*60+date.getMinutes())/60.0;// 以小时衡量的指标
			
			
			// return sdf.format(date);
			return new Chubbyer(sdf.format(date),
					Math.round(point * 10) / 10.0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("时间转换出错");
			return null;
		}
	}
	/*
	 * 统计数据服务器传过来的时间在一天中的分布
	 */
	public double getUseHours(String type, String startTime, String endTime) {
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		long timeLong;
		double morning = 0, afternoon = 0, evening = 0;
		try {
			startDate = formatter.parse(startTime);// 开机时间
			endDate = formatter.parse(endTime);// 关机时间
			int startHour = startDate.getHours();
			int endHour = endDate.getHours();
			if (startHour < 12) {
				if (endHour < 12) {
					morning += (endDate.getTime() - startDate.getTime()) / 6000.0;
				}
				else if(endHour>=12&&endHour<19){
					morning+=(12-startHour)*60-startDate.getMinutes();
					afternoon+=(endHour-12)*60+endDate.getMinutes();
				}else{
					morning+=(12-startHour)*60-startDate.getMinutes();
					afternoon+=7*60;//7=19-12
					evening+=(endHour-19)*60+endDate.getMinutes();
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return evening;

	}

	public static void main(String[] args) throws ParseException {
		System.out.println(TimeParser
				.getTimeStr("2017-04-18T14:49:02.667513600Z"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd ");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// System.out.println(sdf.format(date));
		// date = formatter.parse("2017-04-18 14:49:02");

		// System.out.println(date.getHours());
		String time1 = "2017-05-18 21:29:02";
		String time2 = "2017-05-19 01:49:02";
		System.out.println(TimeParser.getTimeScatter(time1).day);
		// System.out.println( Math.round(18.816666666666666*10)/10.0);
		
	}
}
