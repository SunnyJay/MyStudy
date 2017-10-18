package core_java;

import org.junit.Test;

import java.io.Console;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Administrator on 2017/9/20.
 * 基本程序设计结构
 */
public class TestChapter3
{
    /**
     * 测试变量名 变量名除了_ 还可以用$开头 之前不知道
     */
    @Test
    public void test1()
    {
        int $num = 3;
        System.out.println($num);
    }

    /**
     * 测试二进制整数
     * 只在1.7以上版本支持
     */
    @Test
    public void test2()
    {
        int binaryInt = 0b1101; //int binaryInt = 34_34_34;
        System.out.println(binaryInt);
    }

    /**
     * 测试局部变量不初始化直接使用
     */
    @Test
    public void test3()
    {
        int b;
        //System.out.println(b); //这里会报错
    }

    /**
     * 测试强制转换 舍入
     */
    @Test
    public void test4()
    {
        double a = 9.64;
        int b = (int) a; //直接截取
        int c = (int) Math.round(a); //四舍五入 round返回long  round圆、约
        System.out.println(b + "  " + c); //9    10
    }

    /**
     * 测试subString 最后一个是endindex-1
     */
    @Test
    public void test5()
    {
        String name = "sunnanjun";
        String xing = name.substring(0, 3);
        System.out.println(xing); //sun
    }

    /**
     * 测试==
     */
    @Test
    public void test6()
    {
        String  a = "sunnanjun";
        String  b = "sunnanjun";
        String  c = "jack";
        System.out.println(a == b?"true":"false"); //保存同一个字符串，地址相同，所以是true
        System.out.println(a == c?"true":"false");
    }

    /**
     * 测试StringBuilder 线程不安全
     */
    @Test
    public void test7()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("sun");
        sb.append("nan");
        String name = sb.toString();
        System.out.println(name);
    }

    /**
     * 测试Scanner、Console
     */
    @Test
    public void test8()
    {
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        String word = sc.next();
        System.out.println(name);
        System.out.println(word);

        Console cs = System.console();
        String userName = cs.readLine("UserName");
        char password[] = cs.readPassword("Password");

        System.out.println(userName);
    }

    /**
     * 测试printf
     */
    @Test
    public void test9()
    {
        int a = 343;
        double b = 33.66;
        System.out.printf("孙楠军%5d说到底%4f", a, b);
    }

    /**
     * 测试是否能同名
     * 不能在嵌套的两个块中声明同名变量
     */
    @Test
    public void test10()
    {
        int a;
        {
            //int a = 4; //两个a一个块中，所以这里会报错
        }
    }

    /**
     * 测试case标签是否能是字符串 1.7+可以
     * 1.7以前只能是int、short、byte、char以及对应包装类的表达式；还有枚举常量
     */
    @Test
    public void test11()
    {
        String name = "sunnanjun";
        switch(name) //注意这里是表达式
        {
            case "jack":
            case "sunnanjun":
            {
                System.out.println("yes!");
                break;
            }
            default:break;
        }
    }

    /**
     * 测试大数值  valueOf方法 multiply add
     */
    @Test
    public void test12()
    {
        BigInteger a = BigInteger.valueOf(500); //BigInteger 任意整数整数
        BigInteger b = BigInteger.valueOf(78);
        System.out.println(a);

        BigInteger c = a.add(b);
        System.out.println(c);

        c = a.multiply(b);
        System.out.println(c);
    }

    /**
     * 测试数组
     */
    @Test
    public void test13()
    {
        int[] array1 = {23, 45, 454};
        System.out.println(array1.length);

        int[] array2 = new int[]{23, 45, 454};
        System.out.println(array1==array2?"true":"false");

        int[] ss = new int[0];
        System.out.println(ss.length);

        int[] array3 = Arrays.copyOf(array1, array1.length);
        System.out.println(array3.length);
    }

    /**
     * 测试Arrays sort\binarySearch
     */
    public void test14()
    {
        int[] array1 = {234, 45, 454};
        Arrays.sort(array1);

        for(int a:array1)
        {
            System.out.println(a);
        }

        int index = Arrays.binarySearch(array1, 234);
        System.out.println(index);
    }

    /**
     * 测试值传递
     * java只有值传递
     */
    @Test
    public void test15()
    {
        int num = 3;
        addnum(num);
        System.out.println(num); //num仍然是3
    }
    private void addnum(int num)
    {
        num++;
    }

    /**
     * 测试引用数组
     */
    @Test
    public void test16()
    {
        Teacher1[] array = new Teacher1[10]; //和C++不一样 无构造
    }
}


class Teacher1
{
    private String name;

    public Teacher1(String name)
    {
        this.name = name;
    }
}
