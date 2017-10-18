package core_java;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/9/22.
 * 多线程
 */
public class TestChapter14
{
    /**
     * 中断线程  比较复杂
     * interrupt、isinterrupted
     */
    @Test
    public void test1()
    {
        //注意runnabel的启动方法，作为thread的target穿进去
        Thread t = new Thread(new MyThread());
        t.start();
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {

            e.printStackTrace();
        }
        t.interrupt();//请求终止    对于非阻塞的线程这个方法会使中断标志置为true，
        //对于阻塞的进程，还会让阻塞的线程抛出InterruptedException异常，最后会使中断标志置为false。
        //interrupted是个静态方法
    }

    /**
     * 测试线程状态
     */
    @Test
    public void test2()
    {
        /*
		 * 6种状态 新创建、可运行（因为调度，所以可能运行也可能没有运行、被阻塞(锁)、被等待（通知）、计时等待、被终止）
		 */
    }

    /**
     * 测试线程优先级和守护进程
     */
    @Test
    public void test3()
    {
        //优先级少使用

		/*static void yield() 方法导致当前线程处于让步状态*/
		//理论上，yield意味着放手，放弃，投降。一个调用yield()方法的线程告诉虚拟机它乐意让其他线程占用自己的位置。
        //这表明该线程没有在做一些紧急的事情。注意，这仅是一个暗示，并不能保证不会产生任何影响。
        Thread.yield();

        //守护线程
        //守护线程的唯一作用就是为其他线程提供服务，如计时线程
        //当只剩下守护线程时，虚拟机就退出了,因为没必要继续运行程序了
        Thread t = new Thread(new MyThread());
        t.setDaemon(true); //必须在线程启动前调用
        t.start();
    }

    /**
     * 可重入锁 ReentrantLock
     */
    @Test
    public void test4()
    {

        Bank1 b = new Bank1();
    }

    /**
     * 条件对象(也称条件变量)
     */
    @Test
    public void test5()
    {
        //前提是已经获得了锁
		/*为什么需要条件对象：
		有时候你只获得了锁以后其实却没啥用处，因为你需要通过检测条件来控制线程运行，此时就需要条件*/

        //重要：再银行转账的例子中，锁是为了同步，而条件对象是为了防止没有钱，没有钱你就不能传出去
    }

    /**
     * Synchronized方法
     */
    @Test
    public void test6()
    {
		/*
		 * 大多数情况下用不到lock和condition 用Synchronized就足够了
		 * Synchronized使用的是内部锁
		 * java每个对象都有一个隐藏的内部锁，若一个方法用Synchronized修饰，则该锁就可以保护整个方法
		 * 另外，每个内部锁其实有一个隐藏的条件锁condition相对应. wait方法其实就等价于await  notify等价于signal 。。。
		 * 这就是为什么使用wait方法必须要求用Synchronized修饰方法的原因！重要
		 *
		 * 另外，为什么非要分开起名字，因为wait和notify/all是object的final方法，Condition用await signall就不会冲突
		 *
		 */

		/*
		 * Synchronized还可以修饰静态方法，这样就表示类对象的锁，如Bank.class的锁。这样，其他方法就不能调用同一个“类”的所有同步的静态方法！
		 */

		/*
		 * 尽量不要使用锁，而使用concurrent机制，如果非要用则尽量用Synchronized。
		 */

		/*
		 * 除了Synchronized同步方法，还有Synchronized同步块 下面介绍
		 */
    }

    /**
     * Synchronized块
     */
    @Test
    public void test7()
    {
		/*
		 * 明白什么是同步监视器，obj就是同步监视器，线程执行同步代码块之前，必须先“获得”同步监视器的锁定
		 */

        Object obj = new Object();

        //必须持有一个锁对象  任意找一个对象来即可  如银行账户account
        synchronized(obj) //obj同步监视器
        {

        }

    }


    volatile boolean isRunning; //volatile只能用修饰成员变量！切记！之前不知道
    /**
     * Synchronized块
     */
    @Test
    public void test8()
    {
		/*
		 * 一定要注意volatile的概念 它不是线程安全的 不具有原子性
		 * volatile只不过能立即使用到刷新值
		 * 一个线程的一个变量经常被另一个线程查询或设置，就最好设置为volatile，因为你从外部改变了该变量，原持有该变量的线程会马上感知到
		 * 上面的话也就是Brian Goetz的同步格言：
		 * 如果一个向一个变量写入值，而这个变量接下来可能会被另一个线程读取，或者，从一个变量读取值，而这个变量可能之前被另一个线程写入，此时必须使用同步
		 *
		 */

        //volatile boolean isRunning;  //错误的写法 为什么呢 因为你是线程直接的事，当然不能是局部变量啊

        //当然，final域也是线程安全的

        A14 a14 = new A14();
        new Thread(a14).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        a14.isRun = false; //会马上生效

    }

    /**
     * 原子类
     */
    @Test
    public void test9()
    {
		/*
		 * 原子基本类型有三类
		 * 		AtomicBoolean：原子更新布尔类型。AtomicInteger：原子更新整型。AtomicLong：原子更新长整型。
		 * 原子数组类有三类
		 * 		AtomicIntegerArray：原子更新整型数组里的元素。AtomicLongArray：原子更新长整型数组里的元素。AtomicReferenceArray：原子更新引用类型数组里的元素。
		 * 原子更新引用类型有三类：
		 * 		AtomicReference：原子更新引用类型。
		 * 原子更新字段类有三类：
		 */
        AtomicInteger a = new AtomicInteger(4);
        int sum  = a.addAndGet(4);
        System.out.println(sum);
        int ret = a.getAndIncrement();
        System.out.println(ret + ":" + a); //AtomicInteger重写了toString
        ret = a.getAndSet(98);
        System.out.println(ret + ":" + a);

        ret = a.getAndDecrement();
        System.out.println(ret + ":" + a);

        int[] value = {1,23,4};
        AtomicIntegerArray array = new AtomicIntegerArray(value);

        ret = array.addAndGet(2, 8);
        System.out.println(ret);
    }
}



//********************************************测试对象***********************************************
//用于测试interrupt
class MyThread implements Runnable
{

    @Override
    public void run()
    {
        //isInterrupted（）方法用来返回当前的线程中断标志
        while(!Thread.currentThread().isInterrupted())  //检查中断标志是true或false
        {
            try
            {
				/*当sleep被中断时，抛出InterruptedException异常，会把中断标示为false,原因如下：
				 *   对于处于sleep，join等操作的线程，如果被调用interrupt()后，会抛出InterruptedException，然后线程的中断标志位会由true重置为false，
				 *   因为线程为了处理异常已经重新处于就绪状态（你必须就绪才能处理异常啊）。
				 *
				 * 然后catch捕获，然后调用interrupt()，interrupt方法会重置
				 * 然后，下次while循环就会检测到
				 * */
                Thread.sleep(100000);
            }
            catch (InterruptedException e)
            {
                System.out.println(Thread.currentThread().isInterrupted()?"true":"fasle"); //被interrupt方法置为false
                Thread.currentThread().interrupt();//再次手动设置中断标示为true
            }
        }
        System.out.println(Thread.currentThread().isInterrupted()?"true":"fasle");
        System.out.println("while循环检测到了中断标志为true,所以循环结束");
    }
}


//用于测试ReentrantLock锁
class Bank1
{
    //每一个Bank对象都有一个独立的ReentrantLock锁。
    private Lock lockBank = new ReentrantLock(); //可重入锁

    //transfer同时只能一个线程进入
    public void transfer()
    {
        try
        {
            //重要， 一旦一个线程封住了lockBank锁对象，其他线程都无法通过lock语句，会被阻塞
            lockBank.lock();
        }
        finally
        {
            //切记放在finally中
            lockBank.unlock();
        }
    }
}


//用于测试条件对象
class Bank2
{
    private Condition cond;
    private Lock lockBank = new ReentrantLock();

    public Bank2()
    {
        //通过锁获得
        cond = lockBank.newCondition();
    }


    //transfer同时只能一个线程进入
    public void transfer() throws InterruptedException //await抛出
    {
        lockBank.lock();
        try
        {

            if(true/*xx条件不满足*/) //xx肯定是外部控制的 若满足了直接略过If到cond.signalAll();
            {
                cond.await();//不满足**条件，阻塞一下，等待**条件成立，用sinalAll方法唤醒
            }

            //sinalAll不会立即激活某个等待线程，仅仅解除其阻塞
            //signal方法从等待集中随机选择一个线程解除阻塞，但不推荐使用signal方法，容易死锁deadlock
            cond.signalAll(); //如果满足条件则唤醒。
        }
        finally
        {
            //切记放在finally中
            lockBank.unlock();
        }
    }
}

//用于测试Synchronized
class Bank3
{
    synchronized void  test() throws InterruptedException
    {
        wait();//wait当然要抛出InterruptedException异常
    }

}


//用于测试volatile
class A14 implements Runnable
{
    public  volatile boolean isRun = true;

    @Override
    public void run()
    {
        while(isRun)
        {
            System.out.println("running...");
		/*	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }
    }

}

class MyThread2 implements Runnable
{

    @Override
    public void run() /*throws IOException //run里面不能抛checked异常*/{
    }
}