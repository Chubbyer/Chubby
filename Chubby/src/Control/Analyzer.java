package Control;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import Module.Chubbyer;
import Module.OrderChubbyer;
import Module.User;

/*
 * @Leung
 * ר�����ڷ�����־�¼������߳�
 * ��˵�����������Analyzerͬʱ���ж��ֲ���
 */
public class Analyzer implements Callable<Object>{
	private String eType;//���ִ�в���������
	private User user;
	private int oType;
	private int threadNum;//�߳����
	public Analyzer(String eType,User user,int oType,int threadNum) {
		this.eType=eType;
		this.user=user;
		this.oType=oType;
		this.threadNum=threadNum;
	}
	/*
	 * ��MongoDB����ȡ��־�¼������ݣ����������û��Ŀ��ػ���¼
	 */
	public void name() {
		
	}
	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		if(oType==2){
			ArrayList<String> alStrings0=new ArrayList<String>();
			alStrings0.add("[day:'2017-03-12',point:'09:10:20-12:10:20']");
			
			Thread.sleep(2000);
			System.out.println("Thread:"+threadNum);
			OrderChubbyer<String> orderChubbyer=new OrderChubbyer<String>(threadNum, alStrings0);
			//System.out.println(orderChubbyer.chubbyers.get(0).day);
			return orderChubbyer;
		}
		return null;
	}
}
