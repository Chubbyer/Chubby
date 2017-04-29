package Module;

import java.util.ArrayList;

/*
 * @Leung
 * 线程池当中每一个线程应该返回的子结果
 * 最终可以拼接成最终的结果
 */
public class OrderChubbyer<T> {
	public int order;//线程序号
	public ArrayList<T> chubbyers;//子线程的结果集

	public OrderChubbyer(int order, ArrayList<T> chubbyers) {
		// TODO Auto-generated constructor stub
		this.order = order;
		this.chubbyers = chubbyers;
	}

	public ArrayList<Object> sortChubbyers(
			ArrayList<OrderChubbyer<Object>> orderChubbyers) {
		ArrayList<Object> al = new ArrayList<Object>();
		int minIndex = 0;
		int size = orderChubbyers.size();
		for (int i = 0; i < size - 1; i++) {
			int minOrder = orderChubbyers.get(0).order;
			for (int j = 0; j < orderChubbyers.size(); j++) {
				if (orderChubbyers.get(j).order < minOrder) {
					minOrder = orderChubbyers.get(j).order;
					minIndex = j;
				}
			}
			al.addAll(orderChubbyers.get(minIndex).chubbyers);
			orderChubbyers.remove(minIndex);
			minIndex = 0;
		}
		return al;
	}

	public static void main(String[] args) {
		ArrayList<String> alStrings0 = new ArrayList<String>();
		alStrings0.add("0000");
		ArrayList<String> alStrings1 = new ArrayList<String>();
		alStrings1.add("1111");
		ArrayList<String> alStrings2 = new ArrayList<String>();
		alStrings2.add("2222");
		ArrayList<String> alStrings3 = new ArrayList<String>();
		alStrings3.add("3333");
		ArrayList<String> alStrings4 = new ArrayList<String>();
		alStrings4.add("4444");
		ArrayList<OrderChubbyer<String>> orderChubbyers = new ArrayList<OrderChubbyer<String>>();
		orderChubbyers.add(new OrderChubbyer<String>(2, alStrings2));
		orderChubbyers.add(new OrderChubbyer<String>(4, alStrings4));
		orderChubbyers.add(new OrderChubbyer<String>(3, alStrings3));
		orderChubbyers.add(new OrderChubbyer<String>(1, alStrings1));
		orderChubbyers.add(new OrderChubbyer<String>(0, alStrings0));
		orderChubbyers.add(new OrderChubbyer<String>(3, alStrings3));
		orderChubbyers.add(new OrderChubbyer<String>(1, alStrings1));
		// OrderChubbyer<String> oc=new OrderChubbyer<String>(11, alStrings2);
		// ArrayList<Object>=oc.sortChubbyers(orderChubbyers);
		ArrayList<Object> al = new ArrayList<Object>();

		int minIndex = 0;
		int size = orderChubbyers.size();

		System.out.println("初始Length:" + size);
		for (int i = 0; i < size - 1; i++) {
			int minOrder = orderChubbyers.get(0).order;
			System.out.println("New Length:" + orderChubbyers.size());
			for (int j = 0; j < orderChubbyers.size(); j++) {
				if (orderChubbyers.get(j).order < minOrder) {
					minOrder = orderChubbyers.get(j).order;
					minIndex = j;
					System.out.println("j=" + j);
				}
			}
			System.out.println("minIndex:" + minIndex);
			System.out.println(orderChubbyers.get(minIndex).chubbyers);
			al.addAll(orderChubbyers.get(minIndex).chubbyers);
			orderChubbyers.remove(minIndex);
			minIndex = 0;
		}
		for (Object object : al) {
			System.out.println(object.toString());
		}
	}
}
