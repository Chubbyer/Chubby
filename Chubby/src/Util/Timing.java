package Util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Timing extends Thread {
	public long startTime=0L;
	public long delay;
	public TimeOut object;

	public Timing(long delay, TimeOut object) {
		this.startTime= System.currentTimeMillis();
		this.delay = delay;
		this.object = object;
		//System.out.println("Created a object");
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
		
		while (true) {
			
			System.out.println("-----");
			TimingTest tt = new TimingTest();
		Timing timing = new Timing(5000, tt);
			timing.start();
			System.out.println("LLLLL");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class TimingTest implements TimeOut {

	@Override
	public void timeUp() {
		// TODO Auto-generated method stub
		System.out.println("时间到了之后应该做的事情"+(new Date()).toLocaleString());
	}

}
