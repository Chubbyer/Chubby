package Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC {
	public static String ip = "localhost";
	public static int port = 27017;
	public static String hostName = "Leung";
	public static String dbName = "Chubby";

	// 连接到MongoDB数据库
	public static MongoDatabase connectionMongoDB() {
		try {
			// 连接到 mongodb 服务
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(MongoDBJDBC.ip,
					MongoDBJDBC.port);
			// 连接到数据库
			MongoDatabase mongoDatabase = mongoClient
					.getDatabase(MongoDBJDBC.dbName);
			return mongoDatabase;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
	}

	// 创建MonngoDB集合
	public static void createCollection(String colleName) {
		MongoDBJDBC.connectionMongoDB().createCollection(colleName);
		MongoDBJDBC.writeLog("创建了集合" + colleName);
	}

	// 写操作日志到MongoDB数据库的myLogs集合中
	private static void writeLog(String info) {
		try {
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowTime = sdf.format(d);
			String logString = nowTime + " " + info;
			Document document = new Document("author", MongoDBJDBC.hostName).append("info",
					logString);
			MongoCollection<Document> collection = MongoDBJDBC
					.connectionMongoDB().getCollection("myLogs");
			collection.insertOne(document);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	// 查找某个集合的全部文档
	public static void findAll(String colleName) {
		try {
			MongoCollection<Document> collection = MongoDBJDBC
					.connectionMongoDB().getCollection(colleName);
			//检索所有文档  
	         /** 
	         * 1. 获取迭代器FindIterable<Document> 
	         * 2. 获取游标MongoCursor<Document> 
	         * 3. 通过游标遍历检索出的文档集合 
	         * */  
	         //FindIterable<Document> findIterable = collection.find();  
	         //MongoCursor<Document> mongoCursor = findIterable.iterator();  
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
	public static void main(String[] args) {
		//MongoDBJDBC.createCollection("Security");
		MongoDBJDBC.findAll("myLogs");
	}
}
