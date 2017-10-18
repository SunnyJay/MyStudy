package core_java;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/20.
 * 接口与内部类
 */
public class TestChapter6
{
    /**
     * 测试克隆 尽量避免克隆 甚至杜绝
     * 要重写cloneable方法
     * 默认是浅拷贝 会共享子对象
     * clone是protected，因此子类重写时必须高于protected
     */
    @Test
    public void test1()
    {
        try
        {
            Student6 s1 = new Student6("sun", 22);
            Student6 s2 = (Student6) s1.clone();
            System.out.println(s2.name + " " + s2.age);
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }



    /**
     * 测试内部类 重要 老忘记
     *
     * 内部类的对象总有个隐藏的成员outer，表示外围类对象的引用,利用他可以访问外部对象的所有状态（包括私有）
     * 当然并不叫outer，可用OuterClass.this得到outer。
     *
     * 内部类会默认生成一个构造器，参数是外围类对象，然后把对象赋值给outer引用
     *
     */
    @Test
    public void test2()
    {
        //外部类
        A6 a6 = new A6();
        a6.anum = 7;

        //内部类    外.new 内()
        A6.B6 b6 = a6.new B6(); //注意写法OuterObject.new  InnerClass()  先new出外部类的实例

        System.out.println(b6.bnum);

        String name = b6.getClass().getName(); //查看b6的类名字，你可以发现它是A6$B拼接起来的
        System.out.println(name);

    }


    /**
     * 测试局部内部类
     * 非常鸡肋，知道即可
     */
    @Test
    public void test3()
    {

    }

    /**
     * 测试匿名内部类
     * 使用最为广泛 如线程
     * 匿名类没有类名，所以不能有构造函数
     * 两种形式： 接口、父类
     */
    @Test
    public void test4()
    {
        //接口形式
        new Animal()
        {
            @Override
            public void eat()
            {
                System.out.println("eat!");
            }
        }.eat();

        //父类形式 父类当然可用有构造器的
        new Anony("sun")
        {
            //匿名内不能有构造器是指这里不能有构造器

            @Override
            public void say()
            {
                System.out.println("say!");
            }
        }.say();

        //线程
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("zzzz");
            }
        });
        t.start();

    }


    /**
     * 测试匿名内部类
     * 双括号初始化 重要
     */
    public void test5()
    {
        System.out.println(new ArrayList<String>(){{add("sun");add("nan");}}.size());
    }

    /**
     * 测试静态内部类
     * 静态内部类只是为了隐藏，不想让外人访问
     * 外部类不能加static，否则矛盾
     *
     * 静态内部类的对象除了没有对生成他的外围类对象的引用特权外（即那个outer），与其他所有内部类都一样
     *
     */
    public void test6()
    {
        AStatic as6 = new AStatic();
        as6.anum = 7;

        //静态内部类不通过外部实例就可以创建对象；与类变量可以通过类名访问相似
        AStatic.B6 bs6 = new AStatic.B6(33); //因为是静态的，所以可以直接类.内部类  就和普通的静态成员调用一样  如果不是静态的就不行
        bs6.say();


        A6 a6 = new A6();
        a6.anum = 7;

        //core_java.A6.B6 b6 = new core_java.A6.B6(); //常规内部类需要通过外部类的实例才能创建对象，与实例变量需要通过对象来访问相似
        A6.B6 b6 = a6.new B6(); //这样是可以的
    }
}


//****************************************测试对象***************************************


interface Animal
{
    //默认是public
    void eat();
}


//用于测试克隆
class Student6 implements Cloneable
{
    public String name;
    public int age;

    public Student6(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException //protected、或public
    {
        //默认克隆是浅拷贝 直接调用super.clone();
        return super.clone();
    }

    public Student6()
    {

    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setAge(int age)
    {
        this.age = age;
    }
}

//测试内部类
class A6
{
    int anum;
    private int age;

    public class B6
    {
        int bnum;
        int age;

        //有个隐藏的成员outer，表示外围类引用,利用他可以访问外部对象的所有状态
        //会默认生成一个构造器，参数是外围类对象，然后把对象赋值给outer引用
        //当然并不叫outer，可用OuterClass.this得到outer。

        public B6()
        {
            //通过A6.this 可以得到隐藏的外围类引用。core_java.A6.this是由编译器添加的默认构造函数初始化的
            bnum = A6.this.anum;
            age = A6.this.age; //私有也可以
        }
    }
}

//测试匿名内部类
class Anony
{
    public String name;
    public Anony(String name)
    {
        this.name = name;
    }

    public void say()
    {
        System.out.println("My name is " + name);
    }
}

//测试静态内部类
class AStatic
{
    public int anum;
    public static String name;

    //B6属于AStatic类本身，而不属于AStatic的对象
    //所以B6不能访问AStatic的实例对象的成员，但可以访问静态成员
    public static class B6
    {
        int bnum;


        public B6(int bnum)
        {
            this.bnum = bnum;
        }

        public void say()
        {
            System.out.println(AStatic.name);//可以访问外部类的静态成员 不能访问实例成员
            System.out.println("I am  b6 " + bnum);
        }

    }
}