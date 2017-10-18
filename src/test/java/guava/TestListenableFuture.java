package guava;

import com.google.common.util.concurrent.*;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/9/25.
 */
public class TestListenableFuture
{
    /*我们知道Future表示一个异步计算任务，当任务完成时可以得到计算结果。
    如果我们希望一旦计算完成就拿到结果展示给用户或者做另外的计算，就必须使用另一个线程不断的查询计算状态。
    这样做，不断代码复杂，而且效率低下。
    ListenableFuture，顾名思义，就是可以监听的Future。我们可以为ListenableFuture增加Listener监听器，
    当任务完成时，直接执行某个线程，或者我们可以直接为ListenableFuture设置回调函数，当任务完成时，自动执行该回调方法。*/

    /*
     * JDK原生future不提供回调，而guava提供回调
     * 两种方法
     */

    /**
     * 方法一
     *  推荐方法二
     */
    @Test
    public void test1() throws InterruptedException
    {
        ListeningExecutorService service =
                MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor()); //包装一层


        final ListenableFuture<Integer> future = service.submit(new Callable<Integer>()
        {
            private int i = 0;
            @Override
            public Integer call() throws Exception
            {
                System.out.println("运行任务");
                return ++i;
            }
        });


        future.addListener(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    System.out.println("结果：");
                    System.out.println(future.get());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, Executors.newSingleThreadExecutor()); //不要和上面的executor混淆

       // TimeUnit.SECONDS.sleep(1); //一定要休眠 否则无法调用回调，打印结果   why?
        System.out.println("主线程");
    }

    /**
     * 方法二 推荐
     */
    @Test
    public void test2() throws InterruptedException
    {
        ListeningExecutorService service =
                MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10)); //包装一层

        final ListenableFuture<Integer> future = service.submit(new Callable<Integer>()
        {
            private int i = 0;
            @Override
            public Integer call() throws Exception
            {
                return ++i;
            }
        });

        Futures.addCallback(future, new FutureCallback<Integer>()
        {
            @Override
            public void onSuccess(@Nullable Integer result)
            {
                System.out.println("成功");
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable t)
            {
                System.out.println("失败");
            }
        }, MoreExecutors.directExecutor()); //被废弃addCallback()


        System.out.println("主线程");
    }


}
