package com.wenda.wenda;

import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.User;

import com.wenda.wenda.service.WendaServive;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLOutput;
import java.util.concurrent.*;


@SpringBootTest

public class InitDatabaseTests {
	@Autowired
	UserDao userDao;
	@Autowired
	WendaServive wendaServive;


	static class Consumer implements Runnable{
		private BlockingQueue<String>q;
		Consumer(BlockingQueue<String> q){
			this.q = q;
		}

		@Override
		public void run() {
			try {
				while (true){
					System.out.println(Thread.currentThread().getName()+":"+q.take());
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	static class Producer implements Runnable{
		private BlockingQueue<String> q;
		Producer(BlockingQueue<String> q){
			this.q = q;
		}

		@Override
		public void run() {
			try {
					for (int i = 0; i < 10; i++) {
						Thread.sleep(100);
						q.put(String.valueOf(i));
					}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void test(){
		BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
		new Thread(new Producer(q)).start();
		new Thread(new Consumer(q),"Consumer1").start();
		new Thread(new Consumer(q),"Consumer2").start();
	}
	private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
	private static int userId;

	public static void testThreadLocal(){
		for (int i = 0; i < 10; i++) {

			final int finalI = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					threadLocalUserIds.set(finalI);
						System.out.println("Threadlocal:"+threadLocalUserIds.get());
				}
			}).start();

		}

	}
	public static  void testExecutor(){
		ExecutorService service = Executors.newSingleThreadExecutor();
		
	}

	public static void main(String[] args) {
//		test();
		testThreadLocal();

	}

}
