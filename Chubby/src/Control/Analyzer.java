package Control;

/*
 * @Leung
 * 专门用于分析日志事件，多线程
 * 这说明你可以利用Analyzer同时进行多种操作
 */
public class Analyzer implements Runnable{
	private String oType;//标记执行操作的类型
	
	public Analyzer(String oType) {
		this.oType=oType;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	/*
	 * 从MongoDB中提取日志事件的数据，分析出该用户的开关机记录
	 */
	public void name() {
		
	}
}
