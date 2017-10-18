package guava;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

/**
 * Created by Administrator on 2017/9/25.
 */
public class TestRateLimiter
{
    /*
    常用的限流算法有两种：漏桶算法和令牌桶算法
    guava的RateLimiter使用的是令牌桶算法，也就是以固定的频率向桶中放入令牌，
    例如一秒钟10枚令牌，实际业务在每次响应请求之前都从桶中获取令牌，
    只有取到令牌的请求才会被成功响应，获取的方式有两种：阻塞等待令牌或者取不到立即返回失败
     */

    /**
     * 最简单的示例 限制打印速度
     */
    @Test
    public void test1()
    {
        Long start = System.currentTimeMillis();

        //create函数：创建具有指定稳定吞吐量的RateLimiter类，传入允许每秒提交的任务数量
        RateLimiter rateLimiter = RateLimiter.create(10); //每秒最多10个
        for (int i = 0; i < 20; i++)
        {
            rateLimiter.acquire();
            System.out.println("请求:" + i);
        }

        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * 不限速打印
     */
    @Test
    public void test2()
    {
        Long start = System.currentTimeMillis();

        for (int i = 0; i < 20; i++)
        {
            System.out.println("请求:" + i);
        }

        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
