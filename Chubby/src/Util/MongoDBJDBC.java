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
 * 使用MongoDBJDBC辅助类时应首先配置好属性：ip,port,hostName(本机的主机号)
 * 使用前先启动本地的MongoDB数据库服务
 * 对于同一数据库的操作连接一次就够了
 * 操作不同的数据库要重新连接
 */
public class MongoDBJDBC {
	public static String ip = "localhost";
	public static int port = 27017;
	public static String hostName = "Leung";
	public static MongoClient mongoClient = null;// mongodb 服务
	public static String dbName;

	// 连接到MongoDB数据库
	public static void connectionMongoDB() {
		try {
			// 连接到 mongodb 服务
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(MongoDBJDBC.ip,
					MongoDBJDBC.port);
			MongoDBJDBC.mongoClient = mongoClient;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	// 关闭数据库连接
	public static void closeMongoDB() {
		MongoDBJDBC.mongoClient.close();
		MongoDBJDBC.mongoClient = null;
	}

	// 在指定的数据库下创建MonngoDB集合
	public static void createCollection(String dbName, String colleName) {
		MongoDBJDBC.connectionMongoDB();
		if (MongoDBJDBC.mongoClient != null) {
			// 连接到数据库并创建集合
			MongoDBJDBC.mongoClient.getDatabase(dbName).createCollection(
					colleName);
			MongoDBJDBC.writeLog(dbName, "创建了集合" + colleName);
		} else {
			System.out.println("MongoDB服务未打开");
		}
	}

	// 写操作日志到MongoDB数据库的myLogs集合中
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
	 * 在指定的数据库、集合中插入Event的JSON描述
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

	// 查找某个集合的全部文档
	public static void findAll(String dbName, String colleName) {
		MongoDBJDBC.connectionMongoDB();
		try {
			MongoCollection<Document> collection = MongoDBJDBC.mongoClient
					.getDatabase(dbName).getCollection(colleName);
			// 检索所有文档
			/**
			 * 1. 获取迭代器FindIterable<Document> 2. 获取游标MongoCursor<Document> 3.
			 * 通过游标遍历检索出的文档集合
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
	 * 查找某个数据库某个集合中的100个Event
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
	 * 查找某个同学关于他的文件信息,参数str表示为姓名或学号或主机名
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
	 * 更新User的信息
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
