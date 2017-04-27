package Util;

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
			JSONObject jsonObj=new JSONObject(jsonStr);
			Event event=new Event();
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
			JSONObject jsonObj=new JSONObject(jsonStr);
			User user=new User();
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
	public static void main(String[] args) {
//		String jsonStr="{ '_id' : { '$oid' : '59005de2dca30f934892ed0e' }, 'i' : 7, 'eventID' : '4798', 'TimeCreated' : '2017-04-18 13:49:01' }";
//		Event event=JSONParser.getEventFromJSONStr(jsonStr);
//		System.out.println(event.getEventID());
		String str="301|Leung";
		String[] strs=str.split("|");
		for (int i = 0; i < strs.length; i++) {
			System.out.println(strs[i]);
		}
	}
}
