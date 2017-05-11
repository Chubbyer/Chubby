package Util;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import Module.Event;
import Module.User;

public class JSONParser {

	/*
	 * 将MongoDB中记录Event的JSON字符串封装成Event
	 */
	public static Event getEventFromJSONStr(String jsonStr) {
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Event event = new Event();
			event.setEventID(jsonObj.getString("eventID"));
			event.setTimeCreated(jsonObj.getString("TimeCreated"));
			return event;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 将MongoDB中记录User的JSON字符串封装成User
	 */
	public static User getUserFromJSONStr(String jsonStr) {
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			User user = new User();
			user.setName(jsonObj.getString("Name"));
			user.setSno(jsonObj.getString("Sno"));
			user.setSys(jsonObj.getString("Sys"));
			user.setHost(jsonObj.getString("Host"));
			user.setLogLines(Double.parseDouble(jsonObj.getString("LogLines")));
			user.setFlag(jsonObj.getBoolean("Flag"));
			user.setR_Flag(jsonObj.getBoolean("R_Flag"));
			user.setOpen_Id(jsonObj.getString("Open_Id"));
			user.setClose_Id(jsonObj.getString("Close_Id"));
			return user;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getChubbyerFromJSON(String str) {
		try {
			JSONObject jsonObj = new JSONObject(str);
			return jsonObj.getString("point");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	

	public static void main(String[] args) {
		String	jsonStr="{'ot':'2017-12-03 12:21:21','ct':'2017-12-03 12:21:21'}";
		try {JSONObject jsonObj = new JSONObject(jsonStr);
//		String string=TimeParser.getChubbyerString(jsonObj.getString("ot"))
//		.substring(0, 10);
//		// Event event=JSONParser.getEventFromJSONStr(jsonStr);
//		 //jsonObj.getString("ot").replace('-', '/');
//			System.out.println(string);
//			String string2=TimeParser
//					.getChubbyerString(jsonObj.getString("ct")).substring(
//							11);
//			System.out.println(string2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String str = "301|Leung";
//		String[] strs = str.split("|");
//		for (int i = 0; i < strs.length; i++) {
//			System.out.println(strs[i]);
//		}
	}
}
