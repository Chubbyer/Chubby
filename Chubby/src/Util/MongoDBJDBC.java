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
 * 使用MongoDBJDBC辅助类时应首先配置好属性：ip,port,hostName(本机的主机号)
 * 使用前先启动本地的MongoDB数据库服务
 * 对于同一数据库的操作连接一次就够了
 * 操作不同的数据库要重新连接
 */
public class MongoDBJDBC {
	public static String ip = "localhost";
	public static int port = 27017;
	// public String dbName = "Leung";
	public MongoClient mongoClient = null;// mongodb 服务
	public String dbName;

	public MongoDBJDBC(String host) {
		// TODO Auto-generated constructor stub
		this.dbName = host;
	}

	// 连接到MongoDB数据库
	public boolean connectionMongoDB() {
		try {
			// 连接到 mongodb 服务
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

	// 关闭数据库连接
	public void closeMongoDB() {
		this.mongoClient.close();
		this.mongoClient = null;
	}

	// 在指定的数据库下创建MonngoDB集合
	public void createCollection(String colleName) {
		if (this.connectionMongoDB() && this.mongoClient != null) {
			// 连接到数据库并创建集合
			this.mongoClient.getDatabase(dbName).createCollection(colleName);
			this.writeLog("创建了集合" + colleName);
		} else {
			System.out.println("MongoDB服务未打开");
		}
		this.closeMongoDB();
	}

	// 写操作日志到MongoDB数据库的myLogs集合中
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
	 * 在指定的数据库、集合中插入Event的JSON描述 这是一个会被连续多次调用的方法，在调用处统一连接统一关闭
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

	// 查找某个集合的全部文档
	public void findAll(String colleName) {
		this.connectionMongoDB();
		try {
			MongoCollection<Document> collection = this.mongoClient
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
				this.closeMongoDB();
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/*
	 * 查找某个数据库某个集合中的1000个Event 这是一个会被连续多次调用的方法，在调用处统一连接统一关闭
	 */
	@SuppressWarnings("unconnectionMongoDB")
	public ArrayList<Event> findEvents(String colleName, int startIndex,
			int endIndex) {
		// MongoDBJDBC.connectionMongoDB();//这个方法比较特殊，在调用出统一连接，统一关闭
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
	 * 查找某个同学关于他的文件信息,参数str表示为姓名或学号或主机名
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
	 * 查找某部分同学关于他们的文件信息
	 */
	public ArrayList<User> findUsersInfo(int startIndex,int endIndex) {
		// MongoDBJDBC.connectionMongoDB();//这个方法比较特殊，在调用出统一连接，统一关闭
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
	 * 更新User的信息
	 */
	public void updateUserInfo(String hostName, String key, Object value) {
		if (this.connectionMongoDB()) {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase("User").getCollection("Info");
			collection.updateOne(eq("Host", hostName), set(key, value));
			this.closeMongoDB();
		} else {
			System.out.println("更新" + hostName + "用户的信息失败！");
		}
	}

	/*
	 * 将我们分析的结果（所有的）一次性写入以R_打头的集合中
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
			// 更新User信息
			collection = this.mongoClient.getDatabase("User").getCollection(
					"Info");
			collection.updateOne(eq("Host", host), set("R_Flag", true));
			this.closeMongoDB();
		}

	}

	/*
	 * 将数据库中分析好的结果从R_集合读出来
	 */
	@SuppressWarnings("finally")
	public ArrayList<String> findAllChubbyers(String host) {
		this.connectionMongoDB();
		ArrayList<String> chubbyers = new ArrayList<String>();
		try {
			MongoCollection<Document> collection = this.mongoClient
					.getDatabase(host).getCollection("R_Security");
			// 检索所有文档
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
