package Util;

import java.awt.List;
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
	// public String dbName = "Leung";
	public MongoClient mongoClient = null;// mongodb ����
	public String dbName;

	public MongoDBJDBC(String host) {
		// TODO Auto-generated constructor stub
		this.dbName = host;
	}

	// ���ӵ�MongoDB���ݿ�
	public boolean connectionMongoDB() {
		try {
			// ���ӵ� mongodb ����
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(MongoDBJDBC.ip,
					MongoDBJDBC.port);
			this.mongoClient = mongoClient;
			return true;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
	}

	// �ر����ݿ�����
	public void closeMongoDB() {
		this.mongoClient.close();
		this.mongoClient = null;
	}

	// ��ָ�������ݿ��´���MonngoDB����
	public void createCollection(String colleName) {
		if (this.connectionMongoDB() && this.mongoClient != null) {
			// ���ӵ����ݿⲢ��������
			this.mongoClient.getDatabase(dbName).createCollection(colleName);
			this.writeLog("�����˼���" + colleName);
		} else {
			System.out.println("MongoDB����δ��");
		}
		this.closeMongoDB();
	}

	// д������־��MongoDB���ݿ��myLogs������
	private void writeLog(String info) {
		try {
			String nowTime = TimeParser.getNowTimeStr();
			String logString = nowTime + " " + info;
			Document document = new Document("author", dbName).append("info",
					logString);
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection("myLogs");
			collection.insertOne(document);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * ��ָ�������ݿ⡢�����в���Event��JSON���� ����һ���ᱻ������ε��õķ������ڵ��ô�ͳһ����ͳһ�ر�
	 */
	@SuppressWarnings("unconnectionMongoDB")
	public void insertEvent(String colleName, Event event) {
		// MongoDBJDBC.connectionMongoDB();
		if (this.mongoClient != null) {
			Document document = new Document("i", event.getI()).append(
					"eventID", event.getEventID()).append("TimeCreated",
					event.getTimeCreated());
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			collection.insertOne(document);
		}
		// MongoDBJDBC.closeMongoDB();
	}

	// ����ĳ�����ϵ�ȫ���ĵ�
	public void findAll(String colleName) {
		this.connectionMongoDB();
		try {
			MongoCollection<Document> collection = this.mongoClient
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
				this.closeMongoDB();
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * ����ĳ�����ݿ�ĳ�������е�1000��Event ����һ���ᱻ������ε��õķ������ڵ��ô�ͳһ����ͳһ�ر�
	 */
	@SuppressWarnings("unconnectionMongoDB")
	public ArrayList<Event> findEvents(String colleName, int startIndex,
			int endIndex) {
		// MongoDBJDBC.connectionMongoDB();//��������Ƚ����⣬�ڵ��ó�ͳһ���ӣ�ͳһ�ر�
		try {
			final ArrayList<Event> events = new ArrayList<Event>();
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(this.dbName).getCollection(colleName);
			Block<Document> printBlock = new Block<Document>() {
				@Override
				public void apply(final Document document) {
					events.add(JSONParser.getEventFromJSONStr(document.toJson()));
					// System.out.println(document.toJson());
				}
			};
			collection.find(and(gt("i", startIndex), lte("i", endIndex)))
					.forEach(printBlock);
			return events;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// e.printStackTrace();
			return null;
		}
	}

	/*
	 * ����ĳ��ͬѧ���������ļ���Ϣ,����str��ʾΪ������ѧ�Ż�������
	 */
	public User findUserInfo(String str) {
		if (this.connectionMongoDB()) {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase("User").getCollection("Info");
			Document document = new Document();
			document = collection.find(
					or(eq("Name", str), eq("Sno", str), eq("Host", str)))
					.first();
			// System.out.println(document.toJson());
			this.closeMongoDB();
			if (document != null)
				return JSONParser.getUserFromJSONStr(document.toJson());
			return null;
		}
		return null;
	}
	/*
	 * ����ĳ����ͬѧ�������ǵ��ļ���Ϣ
	 */
	public ArrayList<User> findUsersInfo(int startIndex,int endIndex) {
		// MongoDBJDBC.connectionMongoDB();//��������Ƚ����⣬�ڵ��ó�ͳһ���ӣ�ͳһ�ر�
		try {
			final ArrayList<User> users = new ArrayList<User>();
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase("User").getCollection("Info");
			Block<Document> printBlock = new Block<Document>() {
				@Override
				public void apply(final Document document) {
					users.add(JSONParser.getUserFromJSONStr(document.toJson()));
					// System.out.println(document.toJson());
				}
			};
			collection.find(and(gt("i", startIndex), lte("i", endIndex)))
					.forEach(printBlock);
			return users;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// e.printStackTrace();
			return null;
		}
	}

	/*
	 * ����User����Ϣ
	 */
	public void updateUserInfo(String hostName, String key, Object value) {
		if (this.connectionMongoDB()) {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase("User").getCollection("Info");
			collection.updateOne(eq("Host", hostName), set(key, value));
			this.closeMongoDB();
		} else {
			System.out.println("����" + hostName + "�û�����Ϣʧ�ܣ�");
		}
	}

	/*
	 * �����Ƿ����Ľ�������еģ�һ����д����R_��ͷ�ļ�����
	 */
	public void insertChubbyers(String host, ArrayList<String> cbs) {
		ArrayList<Document> documents = new ArrayList<Document>();
		if (this.connectionMongoDB() && this.mongoClient != null) {
			for (int i = 0; i < cbs.size(); i++) {
				documents.add(new Document("point", cbs.get(i)));
			}
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(host).getCollection("R_Security");
			collection.insertMany(documents);
			// ����User��Ϣ
			collection = this.mongoClient.getDatabase("User").getCollection(
					"Info");
			collection.updateOne(eq("Host", host), set("R_Flag", true));
			this.closeMongoDB();
		}

	}

	/*
	 * �����ݿ��з����õĽ����R_���϶�����
	 */
	@SuppressWarnings("finally")
	public ArrayList<String> findAllChubbyers(String host) {
		this.connectionMongoDB();
		ArrayList<String> chubbyers = new ArrayList<String>();
		try {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(host).getCollection("R_Security");
			// ���������ĵ�
			MongoCursor<Document> cursor = collection.find().iterator();
			try {
				while (cursor.hasNext()) {
					String chubbyer = JSONParser.getChubbyerFromJSON(cursor
							.next().toJson());
					System.out.println(chubbyer);
					if (chubbyer.equals(null))
						chubbyers.add(chubbyer);

				}
			} finally {
				cursor.close();
				this.closeMongoDB();
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return chubbyers;
	}

	public static void main(String[] args) {
		// MongoDBJDBC.createCollection("Security");
		ArrayList<String> cbs = new ArrayList<String>();
		cbs.add("{'ot':'123','ct':'234'}");
		cbs.add("{'ot':'123','ct':'234'}");
		// MongoDBJDBC mongoer = new MongoDBJDBC("Leung");
		// mongoer.connectionMongoDB();
		// mongoer.insertChubbyers("Chubby", cbs);
		// mongoer.findAllChubbyers("Chubby");
		// mongoer.connectionMongoDB();
		// MongoDBJDBC.findAll("Chubby", "myLogs");
		// mongoer.findEvents("Security", 0,30);
		// System.out.println(mongoer.findUserInfo("Leung").getFlag());
		// mongoer.updateUserInfo("Leung", "Flag", false);
		// boolean flag=MongoDBJDBC.findUserInfo("Leung").getFlag();
		// if(flag==false)
		// System.out.println("qq");
		// mongoer.closeMongoDB();

		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		mongoer.updateUserInfo("Leung", "LogLines", 15000);
		// System.out.println(mongoer.findUserInfo("Leung").getName());
	}
}
