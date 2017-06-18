package Util;

import java.awt.List;
import java.util.ArrayList;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import Control.SocketServer;
import Module.Chubbyer;
import Module.Event;
import Module.User;
import Module.WebAnalyzer;
import Module.WebVisiter;

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
	public String ip = "localhost";
	public int port = 27017;
	public MongoClient mongoClient = null;// mongodb ����
	public String dbName;

	public MongoDBJDBC(String host) {
		// TODO Auto-generated constructor stub
		this.dbName = host;
	}

	public MongoDBJDBC(String ip, int port, String host) {
		// TODO Auto-generated constructor stub
		this.ip = ip;
		this.port = port;
		this.dbName = host;
	}

	/*
	 * �������ݷ�������ʹ�õ����ݿⴴ����ͬ��ʵ��
	 */
	public static MongoDBJDBC createMongoger(String host) {
		if (SocketServer.dbPosition == 0) {
			return new MongoDBJDBC(host);
		} else {
			return new MongoDBJDBC(ChubbyConfig.DEFAULT_MONGODB_IP,
					ChubbyConfig.DEFAULT_MONGODB_PORT, host);
		}
	}

	// ���ӵ�MongoDB���ݿ�
	public boolean connectionMongoDB() {
		try {
			// ���ӵ� mongodb ����
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(this.ip, this.port);
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
	// public void createCollection(String colleName) {
	// if (this.connectionMongoDB() && this.mongoClient != null) {
	// // ���ӵ����ݿⲢ��������
	// this.mongoClient.getDatabase(dbName).createCollection(colleName);
	// this.writeLog("�����˼���" + colleName);
	// this.closeMongoDB();
	// } else {
	// System.out.println("MongoDB����δ��");
	// }
	// }

	// д������־��MongoDB���ݿ��myLogs������
	public boolean writeLog(String author, String info)
			throws com.mongodb.MongoSocketOpenException {
		try {
			this.connectionMongoDB();
			String nowTime = TimeParser.getNowTimeStr();
			String logString = nowTime + " " + info;
			Document document = new Document("author", author).append("info",
					logString);
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection("myLogs");
			collection.insertOne(document);
			this.closeMongoDB();
			return true;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			this.closeMongoDB();
			return false;
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
		try {
			if (this.connectionMongoDB()) {
				MongoCollection<Document> collection = this.mongoClient
						.getDatabase(dbName).getCollection(colleName);
				// ���������ĵ�
				/**
				 * 1. ��ȡ������FindIterable<Document> 2. ��ȡ�α�MongoCursor<Document>
				 * 3. ͨ���α�������������ĵ�����
				 * */
				MongoCursor<Document> cursor = collection.find().iterator();
				try {
					while (cursor.hasNext()) {
						System.out.println(cursor.next().toJson());
					}
				} finally {
					cursor.close();
					this.closeMongoDB();
				}
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			this.closeMongoDB();
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
			return JSONParser.getUserFromJSONStr(document.toJson());
		}
		return null;
	}

	/*
	 * ����ĳ����ͬѧ�������ǵ��ļ���Ϣ
	 */
	@SuppressWarnings("unconnectionMongoDB")
	public ArrayList<User> findUsersInfo(int startIndex, int endIndex) {
		// this.connectionMongoDB();//��������Ƚ����⣬�ڵ��ó�ͳһ���ӣ�ͳһ�ر�
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
	 * ����ĳλͬѧ��������Ϣ
	 */
	public void insertUser(int i, String name, String sno, String sys,
			String host, Double logLines, boolean flag, boolean r_Flag,
			String open_Id, String close_Id, Boolean web_Flag, int webLogLines) {
		// ({i:1,Name:"����",Sno:"631406010412",Sys:"Windows10",Host:"Leung",
		// LogLines:10000,Flag:0,R_Flag:0,Open_Id:"4798",Close_Id:"4647"})
		if (this.mongoClient != null) {
			Document document = new Document("i", i).append("Name", name)
					.append("Sno", sno).append("Sys", sys).append("Host", host)
					.append("LogLines", logLines).append("Flag", flag)
					.append("R_Flag", r_Flag).append("Open_Id", open_Id)
					.append("Close_Id", close_Id).append("Web_Flag", web_Flag)
					.append("WebLogLines", webLogLines);
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase("User").getCollection("Info");
			collection.insertOne(document);
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
					// System.out.println("MG:"+chubbyer);
					if (chubbyer != null)
						chubbyers.add(chubbyer);
				}
			} finally {
				cursor.close();
				this.closeMongoDB();
				return chubbyers;
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return chubbyers;
	}

	/*
	 * ��WebLog��Ϣд�����ݿ�
	 */
	@SuppressWarnings("unconnectionMongoDB")
	public void insertWebVisiter(String colleName, WebVisiter visiter) {
		// MongoDBJDBC.connectionMongoDB();
		if (this.mongoClient != null) {
			Document document = new Document("i", (int) visiter.index)
					.append("url", visiter.url)
					.append("visit_time", visiter.visit_time)
					.append("visit_count", visiter.visit_count)
					.append("web_browser", visiter.web_browser);
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			collection.insertOne(document);
		}
		// MongoDBJDBC.closeMongoDB();
	}

	/*
	 * ����ĳ���û����ݿ����������־��¼
	 */
	@SuppressWarnings("unconnectionMongoDB")
	public ArrayList<WebVisiter> findWebVisiters(String colleName,
			int startIndex, int endIndex) {
		// MongoDBJDBC.connectionMongoDB();//��������Ƚ����⣬�ڵ��ó�ͳһ���ӣ�ͳһ�ر�
		try {
			final ArrayList<WebVisiter> webVisiters = new ArrayList<WebVisiter>();
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(this.dbName).getCollection(colleName);
			Block<Document> printBlock = new Block<Document>() {
				@Override
				public void apply(final Document document) {
					webVisiters.add(JSONParser
							.getWebVisiterFromJSONStr(document.toJson()));
					// System.out.println(document.toJson());
				}
			};
			collection.find(and(gt("i", startIndex), lte("i", endIndex)))
					.forEach(printBlock);
			return webVisiters;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// e.printStackTrace();
			return null;
		}
	}

	/*
	 * ��ÿ�����ߵ�ʱ��������ݿ�
	 */
	public void writeOnlineTimes(String colleName, ArrayList<Chubbyer> chubbyers) {
		this.connectionMongoDB();
		if (this.mongoClient != null) {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			for (int i = 0; i < chubbyers.size(); i++) {
				Document document = new Document("Day", chubbyers.get(i).day)
						.append("Usetime", chubbyers.get(i).point);
				collection.insertOne(document);
			}
		}
		this.closeMongoDB();
	}

	/*
	 * ���������ʹ�����д�����ݿ�
	 */
	public void writeBrowserInfo(String colleName, ArrayList<Chubbyer> chubbyers) {
		this.connectionMongoDB();
		if (this.mongoClient != null) {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			for (int i = 0; i < chubbyers.size(); i++) {
				Document document = new Document("Browser",
						chubbyers.get(i).day).append("Visit_count",
						chubbyers.get(i).point);
				collection.insertOne(document);
			}
		}
		this.closeMongoDB();
	}

	/*
	 * ����վ�ķ�������������ݿ�
	 */
	public void writeNodes(String colleName, ArrayList<Chubbyer> chubbyers) {
		this.connectionMongoDB();
		if (this.mongoClient != null) {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			for (int i = 0; i < chubbyers.size(); i++) {
				Document document = new Document("Site", chubbyers.get(i).day)
						.append("Counts", chubbyers.get(i).point);
				collection.insertOne(document);
			}
		}
		this.closeMongoDB();
	}

	/*
	 * ����ĳ���û����ݿ��У���վ�������
	 */
	public ArrayList<Chubbyer> findWebNodes(String colleName,
			final String key1, final String key2) {
		this.connectionMongoDB();//
		try {
			final ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(this.dbName).getCollection(colleName);
			Block<Document> printBlock = new Block<Document>() {
				@Override
				public void apply(final Document document) {
					try {
						JSONObject jsonObj = new JSONObject(document.toJson());
						chubbyers.add(new Chubbyer(jsonObj.getString(key1),
								jsonObj.getDouble(key2)));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println(document.toJson());
				}
			};
			collection.find().forEach(printBlock);
			this.closeMongoDB();
			return chubbyers;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			// e.printStackTrace();
			this.closeMongoDB();
			return null;
		}
	}

	public static void main(String[] args) {
		// MongoDBJDBC.createCollection("Security");
//		MongoDBJDBC mongoer = new MongoDBJDBC("User");
//		User user = mongoer.findUserInfo("Leung");
//		System.out.println(user.getWeb_Flag());
		// mongoer.insertChubbyers("Chubby", cbs);
		// mongoer.findAllChubbyers("Chubby");
		// mongoer.connectionMongoDB();
		// MongoDBJDBC.findAll("Chubby", "myLogs");
		// mongoer.findEvents("Security", 0,30);
		//System.out.println(mongoer.findUserInfo("Leung").getWebLogLines());
		// mongoer.updateUserInfo("Leung", "Flag", false);
		// boolean flag=MongoDBJDBC.findUserInfo("Leung").getFlag();
		// if(flag==false)
		// System.out.println("qq");
		// mongoer.closeMongoDB();

		// MongoDBJDBC mongoer = new MongoDBJDBC("Log");
		// mongoer.updateUserInfo("Leung", "LogLines", 15000);
		// mongoer.connectionMongoDB();
		// mongoer.writeLog("Leung", "2017-05-28 10:00:00");
		// System.out.println(mongoer.findUserInfo("Leung").getName());
	}
}
