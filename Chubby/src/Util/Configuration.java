package Util;

public class Configuration {
	public String LOG_FILE_PATH = "D:\\LogFiles";

	public static void main(String[] args) {
		// ¼�����е��û���Ϣ
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		mongoer.connectionMongoDB();
		mongoer.insertUser(1, "����", "631406010412", "Win10", "Leung",
				(double) 15000, false, false, "4798", "4647");
		
		
		mongoer.closeMongoDB();
	}
}
