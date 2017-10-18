package core_java;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Administrator on 2017/9/25.
 * 多线程
 */
public class TestChapter14_2
{
    /**
     * ThreadLocal概念
     */
    @Test
    public void test1()
    {
        /*
		 * ThreadLocal，很多地方叫做线程本地变量，也有些地方叫做线程本地存储，其实意思差不多。
		 * 可能很多朋友都知道ThreadLocal为变量在每个线程中都创建了一个副本，那么每个线程可以访问自己内部的副本变量。
		 *
		 * 如连接数据库的例子 多个线程间如果共享connection的话，就需要同步connection，但是同步后会大大降低效率所以，要用线程局部变量
		 *
		 * get\set\remove\initialValue   initialValue用来重写
		 * 如果想在get之前不需要调用set就能正常访问的话，必须重写initialValue()方法。
		 *
		 * 最常见的ThreadLocal使用场景为 用来解决 数据库连接、Session管理等。
		 *
		 * ThreadLocal与同步机制是不同的：同步机制是为了共享资源、线程通信，ThreadLocal是为了隔离资源的共享！
		 */


        //在一个给定的线程中首次调用get()方法时，会调用initialValue方法。在此之后，get方法会返回属于当前线程的那个实例。
        String dateStamp = MyThreadLocal.dateFormat.get().format(new Date());
        System.out.println(dateStamp);

        //多线程中随机数也会遇到低效的问题 ，一般也用线程局部变量解决
        int random = ThreadLocalRandom.current().nextInt(20);
        System.out.println(random);
    }

    /**
     * 测试ThreadLoal
     * 使用构造器或set会出现空指针异常， 但是重写Initialvalue没有出现
     */
    @Test
    public void test2() throws InterruptedException
    {
        // Account account = new Account(12); //使用构造器

        Account account2 = new Account();

        new Mythread(account2,"thread1").start();
        new Mythread(account2,"thread2").start();

        Thread.sleep(1000);
    }

    /**
     * trylock和超时
     */
    @Test
    public void test3()
    {
		/*
		 * try lock返回true or false
		 *
		 * tryLock试图获得锁，成功返回true,否则立即返回false.不会阻塞
		 *
		 * 可以加超时参数mylock.tryLock(100, TimeUnit.MILESECONDS)
		 *
		 * 带有超时的trylock可以中断的，会抛出InterruptedException异常	 这是一个非常有用的特性，因为运行程序打破死锁
		 */
    }


    /**
     * 测试读写锁 重要
     */
    @Test
    public void test4()
    {
		/*
		 * 重要！
		 * 如果多个线程读取，而一个线程写入的话，用读写锁是非常方便的！
		 */
        ReadWriteLock rwl = new ReadWriteLock(); //包含ReentrantReadWriteLock

        //启动一个‘写’线程
        new Thread(new WriteThread(rwl)).start(); //构造传入读写锁


        //启动3个‘读’线程  这里保证了所有读线程读取的数值都是一样的
        for(int i = 0 ; i < 3; i++)
        {
            new Thread(new ReadThread(rwl)).start(); //构造传入读写锁
        }

    }

    /**
     * 阻塞队列
     */
    @Test
    public void test5()
    {
		/*
		 * BlokingQueue是个接口
		 * BlokingQueue的作用主要不是作为容器，而是作为线程同步的工具  常用于生产者消费者
		 * concurrent包提供了阻塞队列的实现：
		 * 		Linked、Array、Priority
		 * 阻塞方法：put take 记住这两个即可     方法比较多 用的时候看文档
		 *
		 * put和take会抛出重中断异常 貌似corejava和疯狂java都没有说
		 *
		 * 可以选择设置公平参数
		 *
		 */
        BlockingQueue<Integer> b1 = new ArrayBlockingQueue<>(2);  //一定要指明容量啊
        try {
            b1.put(1); //满阻塞
            b1.put(2);
            b1.add(2); //满抛异常
            //b1.put(3);
            b1.take(); //空阻塞
            b1.poll(); //空抛异常
            System.out.println(b1);

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        //LinkedBlockingQueue是双端队列！
        BlockingQueue<String> b2 = new LinkedBlockingQueue<>(2);  //一定要指明最大容量啊
    }


    /**
     * TransferQueue接口,用于消费者模型
     */
    @Test
    public void test6()
    {
		/*
		 * 生成者消费者模型！
		 * LinkedTransferQueue实现了这个接口
		 */

        TransferQueue<String> queue = new LinkedTransferQueue<String>();

        Thread producer = new Thread(new Producer(queue));
        producer.setDaemon(true); //设置为守护进程使得线程执行结束后程序自动结束运行
        producer.start();

        for (int i = 0; i < 10; i++) {
            Thread consumer = new Thread(new Consumer(queue));
            consumer.setDaemon(true);
            consumer.start();
            try {
                // 消费者进程休眠5秒钟，以便以便生产者获得CPU，从而生产产品
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



}



//******************************************测试对象*****************************************
//测试线程局部变量 使用initialValue
class MyThreadLocal
{

    ThreadLocal<Long> longLocal = new ThreadLocal<Long>();
    ThreadLocal<String> stringLocal = new ThreadLocal<String>();

    //有了set就不需要initalValue
    public void set() {
        longLocal.set(Thread.currentThread().getId());
        stringLocal.set(Thread.currentThread().getName());
    }

    public long getLong() {
        return longLocal.get();
    }

    public String getString() {
        return stringLocal.get();
    }


    static final ThreadLocal<SimpleDateFormat> dateFormat =
            new ThreadLocal<SimpleDateFormat> ()
            {
                @Override
                protected SimpleDateFormat initialValue()
                {
                    return new SimpleDateFormat("yyyy-MM-dd");
                }
            };
}


//测试线程局部变量 使用构造或set貌似会报空指针异常，这里使用了initialValue
class Account
{
    private static final ThreadLocal<Integer> value = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue()
        {
            return 1;
        }
    };

    public Account()
    {
    }

    public Account(int value)
    {
        this.setValue(value);
        System.out.println(this.value.get());
    }

    public void setValue(int value)
    {
        this.value.set(value);
    }

    public int getValue()
    {
        return this.value.get();
    }
}

//测试线程局部变量
class Mythread extends Thread
{
    private Account account;
    private String name;

    public Mythread(Account account, String name)
    {
        this.account = account;
        this.name = name;
    }

    @Override
    public void run()
    {
        int i = 0;
        while(i < 20)
        {
            account.setValue(account.getValue()+1);
            System.out.println(this.name + " " + account.getValue());

            i++;
        }
    }
}



//用于测试TryLock
class BankForTryLock
{
    //都使用可重入锁
    private Lock lockBank = new ReentrantLock();

    public BankForTryLock()
    {
    }


    //transfer同时只能一个线程进入
    public void transfer() throws InterruptedException //await抛出
    {
        //tryLock试图获得锁，成功返回true,否则立即返回false.不会阻塞
        if(lockBank.tryLock()) //if(lockBank.tryLock(100, TimeUnit.MILLISECONDS))
        //也可以调用lockBank.lockInterruptibly(); 是一个超时设为无限的trylock方法
        {
            try
            {

            }
            finally
            {
                //切记放在finally中
                lockBank.unlock();
            }
        }

    }
}

//测试读写锁
class ReadThread implements Runnable
{
    private ReadWriteLock rl;
    public ReadThread(ReadWriteLock readWriteLock)
    {
        this.rl = readWriteLock;
    }


    @Override
    public void run()
    {
        while(true)
        {
            rl.getNum();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
//测试读写锁
class WriteThread implements Runnable
{
    private ReadWriteLock rl;
    public WriteThread(ReadWriteLock readWriteLock)
    {
        this.rl = readWriteLock;
    }


    @Override
    public void run()
    {
        while(true)
        {
            rl.add();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

//测试读写锁
class ReadWriteLock
{
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    //抽取读锁
    private Lock readlock = rwl.readLock();

    //抽取写锁
    private Lock writelock = rwl.writeLock();

    private int num = 0;

    public void add()
    {
        writelock.lock();
        // System.out.println(Thread.currentThread().getName() + " be ready to write  data!");
        num += 3;
        System.out.println(Thread.currentThread().getName() + "have write data :" + num);
        writelock.unlock();
    }

    public void  getNum()
    {
        //如果不加锁，你会发现各个读线程读取的数据是不同的
        readlock.lock();
        System.out.println(Thread.currentThread().getName() + "have read data :" + num);
        readlock.unlock();
    }

}


//生产者
class Producer implements Runnable {
    private final TransferQueue<String> queue;

    public Producer(TransferQueue<String> queue) {
        this.queue = queue;
    }

    private String produce() {
        return " your lucky number " + (new Random().nextInt(100));
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (queue.hasWaitingConsumer())  //是否存在消费者线程 没有消费者就不生产
                {
                    queue.transfer(produce()); //transfer添加元素
                }
                TimeUnit.SECONDS.sleep(1);//生产者睡眠一秒钟,这样可以看出程序的执行过程
            }
        } catch (InterruptedException e) {
        }
    }
}

//消费者
class Consumer implements Runnable {
    private final TransferQueue<String> queue;

    public Consumer(TransferQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            System.out.println(" Consumer " + Thread.currentThread().getName()
                    + queue.take());
        } catch (InterruptedException e) {
        }
    }
}