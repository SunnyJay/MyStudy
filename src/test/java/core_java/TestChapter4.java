package core_java;

import org.junit.Test;

import java.util.Date;

/**
 * Created by Administrator on 2017/9/20.
 * 对象和类
 */
public class TestChapter4
{
    /**
     * 虽然final修饰了date，但是date仍然是可变的
     */
    public final Date date = new Date();

    /**
     * 初始值可以是静态函数
     */
    private static int nextId;
    private int id =  getIdValue();

    private static int getIdValue()
    {
        int r = nextId;
        r++;
        return r;
    }

    public TestChapter4()
    {
        System.out.println("执行构造");
    }

    /**
     * 初始化块
     */
    private int c;
    {
        System.out.println("初始化块");
        c = 2;
    }

    /**
     * 静态初始化(注意与初始化块的区别) 类第一次加载的时只执行一次
     * 在不使用main函数的时候，可以执行函数
     */
    private static int d;
    static
    {
        d = 3;
        System.out.println("静态初始化块");
    }

    /**
     * 测试初始化块
     * 顺序：
     *   0.静态初始化（静态域、静态初始化块）
     *   1.数据域
     *   2.域初始化语句（如上面的getIdValue）、初始化块
     *   3.构造
     */
    @Test
    public void test2()
    {
        System.out.println(c);
    }

}
