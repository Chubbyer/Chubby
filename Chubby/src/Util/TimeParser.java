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
	 * ����־�ļ��е�ʱ���ַ�����ȡ�������ʽ
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
	 * �Ѻ�̨���ݷ�������������ʱ��ε���������ת������ҳ����չʾ��ʱ��ڵ�
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
			stratDate = formatter.parse(startTime);// ����ʱ��
			endDate = formatter.parse(endTime);// �ػ�ʱ��
			point = (endDate.getTime() - stratDate.getTime()) / 3600000.0;// �Ѻ������Ϊ��Сʱ������ָ��
			timeLong = stratDate.getTime() + 1000 * 60 * 60 * 8;// ����ʱ������8��Сʱ
			stratDate = new Date(timeLong);
			// return sdf.format(date);
			return new Chubbyer(sdf.format(stratDate),
					Math.round(point * 10) / 10.0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ʱ��ת������");
			return null;
		}
	}
	/*
	 * �Ѻ�̨���ݷ�������������ʱ��ε���������ת�����ڿ��ػ�ʱ���
	 */
	public static Chubbyer getTimeScatter(String timeString) {
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		long timeLong;
		double point;
		try {
			date = formatter.parse(timeString);
			timeLong = date.getTime() + 1000 * 60 * 60 * 8;// ����ʱ������8��Сʱ
			date = new Date(timeLong);
			point = (date.getHours()*60+date.getMinutes())/60.0;// ��Сʱ������ָ��
			
			
			// return sdf.format(date);
			return new Chubbyer(sdf.format(date),
					Math.round(point * 10) / 10.0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ʱ��ת������");
			return null;
		}
	}
	/*
	 * ͳ�����ݷ�������������ʱ����һ���еķֲ�
	 */
	public double getUseHours(String type, String startTime, String endTime) {
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		long timeLong;
		double morning = 0, afternoon = 0, evening = 0;
		try {
			startDate = formatter.parse(startTime);// ����ʱ��
			endDate = formatter.parse(endTime);// �ػ�ʱ��
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
