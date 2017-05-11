package Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.crypto.Data;

import Module.Chubbyer;

import com.mongodb.client.model.geojson.Point;

public class TimeParser {

	/*
	 * 把日志文件中的时间字符串提取出常规格式
	 */
	public static String getTimeStr(String str) {
		String formatTimeStr=str.substring(0, 19).replace('T', ' ');
		return formatTimeStr;
	}
	public static String getNowTimeStr() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date);
	}
	/*
	 * 把后台传过来的时间调整过来并转换成在页面上展示的时间节点
	 */
	public static Chubbyer getChubbyerFromString(String str){
		Date date=null;  
	    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    long timeLong;
	    double point;
	    try {
			date=formatter.parse(str);
			timeLong=date.getTime()+1000*60*60*8;//调整时区，加8个小时
			date=new Date(timeLong);
			point=(date.getHours()*60+date.getMinutes())/60.0;//把时分秒调整为以小时衡量的指标
			//return sdf.format(date);
			return new Chubbyer(sdf.format(date), Math.round(point*10)/10.0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("时间转换出错");
			return null;
		}
	}
	public static void main(String[] args) throws ParseException {
		System.out.println(TimeParser.getTimeStr("2017-04-18T14:49:02.667513600Z"));
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//System.out.println(sdf.format(date));
		date=formatter.parse("2017-04-18 14:49:02");
		
		System.out.println(date.getHours());
			String time="2017-05-18 10:49:02";  
			
		    System.out.println(TimeParser.getChubbyerFromString(time).point);
		   // System.out.println(	Math.round(18.816666666666666*10)/10.0); 
		
	}
}
