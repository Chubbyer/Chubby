package Util;

public class ChubbyConfig {
	public String LOG_FILE_PATH = "D:\\LogFiles";
	public static String STATION_IP="127.0.0.1";
	public static int STATION_PORT=10000;
	public static String DEFAULT_MONGODB_IP="127.0.0.1";
	public static int DEFAULT_MONGODB_PORT=27017;
	public static int HEART_BEAT_PORT=9090;

	public static void main(String[] args) {
		// 录入所有的用户信息
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		mongoer.connectionMongoDB();
		mongoer.insertUser(1, "梁健", "631406010412", "Win10", "Leung",
				(double) 15000, false, false, "4798", "4647");
		
		
		mongoer.closeMongoDB();
	}
}
