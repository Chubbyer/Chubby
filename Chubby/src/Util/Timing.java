package Util;

import java.io.PrintWriter;
import java.util.Date;

/*
 * 计时器，传入计时时间和实现了TimeOut接口的对象，该对象需要重载
 * timeUp函数，该函数在计时时间到了之后被调用
 * 
 */
public class Timing extends Thread {
	public long startTime = 0L;
	public long delay;
	public TimeOut object;

	public Timing(long delay, TimeOut object) {
		this.startTime = System.currentTimeMillis();
		this.delay = delay;
		this.object = object;
		// System.out.println("Created a object");
	}

	public void run() {
		// TODO Auto-generated method stub
		long nowTime;
		while (true) {
			try {
				Thread.sleep(100);
				nowTime = System.currentTimeMillis();
				if ((nowTime - startTime) > delay) {
					// System.out.println(object.toString());
					object.timeUp();
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
			System.out.println("-----");
//			TimingTest tt = new TimingTest();
//			Timing timing = new Timing(5000, tt);
//			timing.start();
			//2017/02/20
			double[] d={
					1.2, 0.7, 4.4, 0.0, 7.9, 0.0, 0.0, 
					4.4, 5.2, 1.8, 4.5, 0.0, 0.1, 9.2,
					0.0, 0.0, 1.0, 0.0, 5.9, 8.9, 0.0,
					0.0, 3.9, 0.4, 1.8, 0.0, 0.0, 2.8,
					0.5, 0.0, 0.1, 0.4, 0.0, 6.4, 0.0,
					0.8, 0.2, 1.1, 3.6, 3.5, 0.0, 3.0, 
					5.1, 0.0, 0.0, 3.9, 0.0, 1.5, 0.0, 
					0.0, 3.8, 3.4, 1.9, 3.4, 0.0, 1.7, 
					0.0, 1.6, 0.8, 2.1, 0.0, 2.6, 0.0, 
					3.0, 2.9, 5.7, 4.5, 3.6, 9.8
					};
			TimeOutHandle timeOutHandle=new TimeOutHandle(null, 7*1000);
			timeOutHandle.startTiming();
			System.out.println("LLLLL");
			try {
				Thread.sleep(5000);
				timeOutHandle.closeTiming();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

class TimeOutHandle implements TimeOut {
	public Timing timing;
	public long delay;// 倒计时时间
	public PrintWriter out;
	public boolean timingFlag = true;

	public TimeOutHandle(PrintWriter out, long delay) {
		// TODO Auto-generated constructor stub
		this.delay = delay;
		timing = new Timing(delay, this);
	}

	public void startTiming() {
		if (timingFlag)
			timing.start();
	}

	public void closeTiming() {
		timingFlag = false;
	}

	@Override
	public void timeUp() {
		// TODO Auto-generated method stub
		if (timingFlag)
			System.out.println("TimeOut");
	}
}
