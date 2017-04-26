package Util;

import org.json.JSONException;
import org.json.JSONObject;

import Module.Event;

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
	public static void main(String[] args) {
		String jsonStr="{ '_id' : { '$oid' : '59005de2dca30f934892ed0e' }, 'i' : 7, 'eventID' : '4798', 'TimeCreated' : '2017-04-18 13:49:01' }";
		Event event=JSONParser.getEventFromJSONStr(jsonStr);
		System.out.println(event.getEventID());
	}
}
