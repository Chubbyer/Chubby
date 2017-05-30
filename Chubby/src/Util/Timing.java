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
