package Util;

import java.util.ArrayList;

import org.bson.Document;

import Module.Event;
import Module.User;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

/*
 * @Leung
 * ʹ��MongoDBJDBC������ʱӦ�������ú����ԣ�ip,port,hostName(������������)
 * ʹ��ǰ���������ص�MongoDB���ݿ����
 * ����ͬһ���ݿ�Ĳ�������һ�ξ͹���
 * ������ͬ�����ݿ�Ҫ��������
 */
public class MongoDBJDBC {
	public static String ip = "localhost";
	public static int port = 27017;
	public static String hostName = "Leung";
	public static MongoClient mongoClient = null;// mongodb ����
	public static String dbName;

	// ���ӵ�MongoDB���ݿ�
	public static void connectionMongoDB() {
		try {
			// ���ӵ� mongodb ����
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(MongoDBJDBC.ip,
					MongoDBJDBC.port);
			MongoDBJDBC.mongoClient = mongoClient;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	// �ر����ݿ�����
	public static void closeMongoDB() {
		MongoDBJDBC.mongoClient.close();
		MongoDBJDBC.mongoClient = null;
	}

	// ��ָ�������ݿ��´���MonngoDB����
	public static void createCollection(String dbName, String colleName) {
		MongoDBJDBC.connectionMongoDB();
		if (MongoDBJDBC.mongoClient != null) {
			// ���ӵ����ݿⲢ��������
			MongoDBJDBC.mongoClient.getDatabase(dbName).createCollection(
					colleName);
			MongoDBJDBC.writeLog(dbName, "�����˼���" + colleName);
		} else {
			System.out.println("MongoDB����δ��");
		}
	}

	// д������־��MongoDB���ݿ��myLogs������
	private static void writeLog(String dbName, String info) {
		try {
			String nowTime = TimeParser.getNowTimeStr();
			String logString = nowTime + " " + info;
			Document document = new Document("author", MongoDBJDBC.hostName)
					.append("info", logString);
			MongoCollection<Document> collection = MongoDBJDBC.mongoClient
					.getDatabase(dbName).getCollection("myLogs");
			collection.insertOne(document);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * ��ָ�������ݿ⡢�����в���Event��JSON����
	 */
	public static void insertEvent(String dbName, String colleName, Event event) {
		MongoDBJDBC.connectionMongoDB();
		if (MongoDBJDBC.mongoClient != null) {
			Document document = new Document("i", event.getI()).append(
					"eventID", event.getEventID()).append("TimeCreated",
					event.getTimeCreated());
			MongoCollection<Document> collection = MongoDBJDBC.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			collection.insertOne(document);
		}
	}

	// ����ĳ�����ϵ�ȫ���ĵ�
	public static void findAll(String dbName, String colleName) {
		MongoDBJDBC.connectionMongoDB();
		try {
			MongoCollection<Document> collection = MongoDBJDBC.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			// ���������ĵ�
			/**
			 * 1. ��ȡ������FindIterable<Document> 2. ��ȡ�α�MongoCursor<Document> 3.
			 * ͨ���α�������������ĵ�����
			 * */
			// FindIterable<Document> findIterable = collection.find();
			// MongoCursor<Document> mongoCursor = findIterable.iterator();
			MongoCursor<Document> cursor = collection.find().iterator();
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * ����ĳ�����ݿ�ĳ�������е�100��Event
	 */
	public static ArrayList<Event> findEvents(String dbName, String colleName,
			int startIndex) {
		MongoDBJDBC.connectionMongoDB();
		try {
			final ArrayList<Event> events = new ArrayList<Event>();
			MongoCollection<Document> collection = MongoDBJDBC.mongoClient
					.getDatabase(dbName).getCollection(colleName);

			Block<Document> printBlock = new Block<Document>() {
				@Override
				public void apply(final Document document) {
					events.add(JSONParser.getEventFromJSONStr(document.toJson()));
					System.out.println(document.toJson());
				}
			};
			// startIndex<=i<startIndex+100
			collection
					.find(and(gt("i", startIndex), lte("i", startIndex + 10)))
					.forEach(printBlock);
			return events;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
	}

	/*
	 * ����ĳ��ͬѧ���������ļ���Ϣ,����str��ʾΪ������ѧ�Ż�������
	 */
	public static User findUserInfo(String str) {
		MongoDBJDBC.connectionMongoDB();
		MongoCollection<Document> collection = MongoDBJDBC.mongoClient
				.getDatabase("User").getCollection("Info");
		Document document = new Document();
		document = collection.find(
				or(eq("Name", str), eq("Sno", str), eq("Host", str))).first();
		System.out.println(document.toJson());
		return JSONParser.getUserFromJSONStr(document.toJson());
	}
	/*
	 * ����User����Ϣ
	 */
	public static void updateUserInfo(String host,String key,Object value) {
		MongoCollection<Document> collection = MongoDBJDBC.mongoClient
				.getDatabase("User").getCollection("Info");
		collection.updateOne(eq("Host", host), set(key, value));
	}

	public static void main(String[] args) {
		// MongoDBJDBC.createCollection("Security");
		//MongoDBJDBC.connectionMongoDB();
		// MongoDBJDBC.findAll("Chubby", "myLogs");
		// MongoDBJDBC.findEvents("Chubby", "Security", 0);
		System.out.println(MongoDBJDBC.findUserInfo("Leung").getFlag());
		MongoDBJDBC.updateUserInfo("Leung", "Flag", false);
		boolean flag=MongoDBJDBC.findUserInfo("Leung").getFlag();
		if(flag==false)
		 System.out.println("qq");
		MongoDBJDBC.closeMongoDB();
	}
}
