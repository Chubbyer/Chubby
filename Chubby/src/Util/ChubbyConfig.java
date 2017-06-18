package Util;

public class ChubbyConfig {
	public String LOG_FILE_PATH = "D:\\LogFiles";
	public static String STATION_IP="127.0.0.1";
	public static int STATION_PORT=10000;
	public static String DEFAULT_MONGODB_IP="127.0.0.1";
	public static int DEFAULT_MONGODB_PORT=27017;
	public static int HEART_BEAT_PORT=9090;
	public static String DEFAULT_DS_IP="127.0.0.1";
	public static int DEFAULT_DS_PORT=10011;

	public static void main(String[] args) {
		// ¼�����е��û���Ϣ
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		mongoer.connectionMongoDB();

		mongoer.insertUser(1, "����", "631406010412", "Win10", "Leung",
				(double) 15000, false, false, "4798", "4647",false,0);
		
		mongoer.insertUser(2, "���", "631406010402", "Win8", "LiJie",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(3, "����", "631406010404", "Win8", "YangLin",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(4, "����", "631406010405", "Win10", "LiuJia",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(5, "����", "631406010406", "Win7", "ZhouYu",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(6, "���Ѻ�", "631406010408", "Win8", "LiuZhaoHong",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(7, "����", "631406010409", "Win10", "DongGang",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(8, "������", "631406010410", "Win7", "WuShouZeng",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(9, "���ȴ�", "631406010413", "Win7", "LiYiDa",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(10, "����", "631406010415", "Win7", "ZhangXiang",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(11, "�¾�", "631406010416", "Win8", "ChenJin",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(12, "����", "631406010418", "Win10", "ZhangLiang",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(13, "����", "631406010419", "Win10", "Chenxin",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(14, "����", "631406010422", "Win7", "GonYi",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(15, "����", "631406010423", "Win10", "LuoYi",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(16, "�³���", "631406010424", "Win10", "ChenZhaoYang",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(17, "��", "631406010426", "Win8", "XiangJian",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(18, "ŷС��", "631406010427", "Win10", "OuXiaoFeng",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(19, "����", "6314060104128", "Win10", "LiaoWang",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(20, "����", "631406010429", "Win7", "WuFei",
				(double) 15000, false, false, "4624", "4647",false,0);
		
		mongoer.insertUser(21, "�½���", "631406010430", "Win10", "ChenJianChuan",
				(double) 15000, false, false, "4624", "4647",false,0);
				
		mongoer.closeMongoDB();
	}
}
