package Protocol;

/*
 * @Leung
 * 服务-客服机状态报告协议
 */
public class SC {
	public static String CHECK_CONNECTION="101";//检查连接
	public static String SERVER_OK="200";//可以正常连接
	public static String SERVER_BUSY="201";//忙
	public static String SERVER_ERROR="202";//错误
	public static String CHUBBYER_REPORT="203";//数据服务器报告
	public static String CLIENT_REQUEST="204";//客服机请求
	public static String HEART_BEAT="205";//心跳测试
}
