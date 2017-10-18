package core_java;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;

/**
 * Created by Administrator on 2017/9/20.
 * 测试异常、断言
 */
public class TestChapter11
{

    /**
     * 测试异常
     * 手动抛
     */
    @Test
    public void test1() throws EOFException
    {
        /*throw就是自己进行异常处理，处理的时候有两种方式，
        要么自己捕获异常（也就是try catch进行捕捉），要么声明抛出一个异常（就是throws 异常~~）*/
        int a = 6;
        if(a < 10)
        {
            throw new EOFException(); //注意new，表示抛出异常对象。若throw，则要么try捕获，要么向上抛
        }
    }


    /**
     * 测试异常
     * 多个异常
     */
    @Test
    public void test2() throws EOFException, ServerException
    {
        /*
		 * 多个异常间用逗号
		 * 不要抛出未检查异常，比如什么ArrayIndexException NullPointerException IndexOutOfBoundsException
		 */

        int a = 6;
        try
        {
            a += 1;
        }
        catch(Exception e) //还可以catch(Exception1 | Exception2 e)
        {
            //e.printStackTrace();
            //再次抛出异常
            throw new ServerException("down:" + e.getMessage());
        }
    }

    /**
     * 测试异常
     * 包装原始异常
     */
    @Test
    public void test3() throws Throwable
    {
        //包装原始异常
        //强烈建议多使用这种方式, 抛出高级异常，而不丢失原始信息
        int a = 6;
        try
        {
            a += 1;
        }
        catch(Exception e) //还可以catch(Exception1 | Exception2 e)
        {

            Throwable se = new ServerException("down:");
            se.initCause(e);
            throw se;
        }

        try
        {
            a += 1;
        }
        catch(Exception e) //有时候你想直接抛出不做任何改变
        {
            throw e;
        }
    }


    /**
     * 用于测试期间向代码插入一些检查语句，代码发布的时候就删除了！
     * java默认是关闭assert的，在eclipse中需要打开
     * VM arguments文本框中加上断言开启的标志:-enableassertions 或者-ea 就可以了
     */
    @Test
    public void testAssert()
    {
        int i = 3;
        assert i == 3;

        //表达式唯一的目的是产生消息字符串
        assert i == 4:"sd"; //sd将传给异常
    }

    /**
     * 测试finally
     * 关闭资源的三种方式:
     * 1.final中嵌套try catch 因为close也需要捕获
     * 2.try中嵌套try finally 强烈推荐
     * 3.JDK7的方式 放在try后，分号分割  自动close
     */
    @Test
    public void test4()
    {
        InputStream in = System.in;

        //第一种
        //这种方式需要对close再进行trycatch, 看起来程序很复杂
        try
        {
            in.read();
        }
        catch(Exception e)
        {

        }
        finally
        {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //第二种 强烈建议这种方式 清晰简洁
        try
        {
            try
            {
                in.read();
            }
            finally
            {
                in.close();
            }

        }
        catch(Exception e)
        {

        }

        //第三种 JDK 7的方式，即带资源的trycatch 可以不用再显示close 也推荐使用
        //1.7的资源类几乎全部改写，可放心大胆使用
        //try(InputStream in2 = System.in) //可以同时几个资源 分号间隔 //try(InputStream in2 = System.in;InputStream in3 = System.in)
        try(InputStream in2 = System.in;InputStream in3 = System.in)
        {
            //try块退出的时候会自动调用close
        }
        catch(Exception e)
        {
            //
        }
    }

    /**
     * 测试finally
     * 测试finally中的return 记住finally永远会执行，会覆盖之前的return
     */
    @Test
    @SuppressWarnings("finally")
    public void  test5()
    {
        System.out.println(fun1());
    }
    int fun1()
    {
        try
        {
            return 1;
        }
        catch(Exception e)
        {

        }
        finally
        {
            return 3;
        }
    }

    /**
     * 测试堆栈跟踪
     * 不一定非要通过捕获异常来生成堆栈跟踪，调用Thread.dumpStack()即可
     */
    @Test
    public void  test6()
    {
        //只要调用这个就能产生堆栈跟踪
        Thread.dumpStack();
    }

}
