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

	// ���ӵ�MongoDB���ݿ�
	public static MongoDatabase connectionMongoDB() {
		try {
			// ���ӵ� mongodb ����
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(MongoDBJDBC.ip,
					MongoDBJDBC.port);
			// ���ӵ����ݿ�
			MongoDatabase mongoDatabase = mongoClient
					.getDatabase(MongoDBJDBC.dbName);
			return mongoDatabase;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
	}

	// ����MonngoDB����
	public static void createCollection(String colleName) {
		MongoDBJDBC.connectionMongoDB().createCollection(colleName);
		MongoDBJDBC.writeLog("�����˼���" + colleName);
	}

	// д������־��MongoDB���ݿ��myLogs������
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

	// ����ĳ�����ϵ�ȫ���ĵ�
	public static void findAll(String colleName) {
		try {
			MongoCollection<Document> collection = MongoDBJDBC
					.connectionMongoDB().getCollection(colleName);
			//���������ĵ�  
	         /** 
	         * 1. ��ȡ������FindIterable<Document> 
	         * 2. ��ȡ�α�MongoCursor<Document> 
	         * 3. ͨ���α�������������ĵ����� 
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
