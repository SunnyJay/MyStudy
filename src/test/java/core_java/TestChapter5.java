package core_java;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/20.
 * 继承
 */
public class TestChapter5
{
    /*
    this和super的区别：
    super不是一个对象的引用，不能赋值，只是一个指示编译器调用超类方法的特殊关键词
    */

    /*
    Java的动态绑定又称为运行时绑定。意思就是说，程序会在运行的时候自动选择调用哪儿个方法。
    1.虚拟机预先为每个类创建一个方法表；
    2.运行的时候虚拟机提取对象实际类型的方法表，然后在这些方法表里有找指定签名的类
    3.找到后调用方法
    */


    /**
     * 在转换超类为子类前，要先instanceof检查
     */
    @Test
    public void test1()
    {
        Father1 obj = new ChildClass1();
        if(obj instanceof  ChildClass1) //obj为null返回false
        {
            ChildClass1 obj2 = (ChildClass1)obj;
            obj2.fun1();
        }
    }


    /**
     * ArrayList
     */
    @Test
    public void test2()
    {
        /**
         * 设置预设容量initialCapacity 默认10
         * arraylist的扩容方法在1.7中变为 old*2+old
         */
        ArrayList<Integer> list = new ArrayList<>(100);
        list.trimToSize();//调整容量到size,收回多余空间

        //int a = list[1]; //list不提供下标访问,只能get
    }

    /**
     * toArray方法
     * 两种方式：toArray()后强制转换、toArray(T)
     * 第一种避免使用，容易出现cast错误
     */
    @Test
    public void test3()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        String[] array1 = (String[])list.toArray();  //这种方式避免使用,很容易出现ClassCastException异常
        System.out.println(array1);

        String[] array2 = new String[list.size()]; //注意是size方法
        list.toArray(array2); //存到了array2里
    }

    /**
     * 测试hashCode equals
     * ==不能重写
     */
    @Test
    public void test4()
    {
        //测试hashCode和equals
        Student a = new Student("A", 22);
        Student b = new Student("A", 22);
        Student c = new Student("A", 23);
        System.out.println(a.equals(b)?"true":"false");
        System.out.println(a == b ?"true":"false"); //==是无法重写的！
        System.out.println(a.equals(c)?"true":"false");
    }
}

//测试hashCode equals
class Student
{
    private String name;
    private int age;
    public Student(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return age % 100;
    }

    @Override
    public boolean equals(Object obj) {
        //先判断是否是本对象
        if(obj == this)
            return true;
        //再判断是否同类
        if(obj instanceof Student)
        {
            //然后比较域
            Student temp = (Student) obj;
            if((temp.age == this.age) && (temp.name.equals(this.name)) )
                return true;
        }
        return false;
    }
}

//*********************************一、覆盖（重写）特性*********************************

class Father1
{
    int a = 3;
    public void fun1()
    {
        System.out.println("father");
    }
}

class ChildClass1 extends Father1
{
    int a = 5; //a会隐藏掉父类的a

    /**
     * 覆盖时，子类的方法不能低于父类方法的可见性
     * 也就是说，若此处fun1为private或default，则会编译出错
     */
    public void fun1()
    {
        System.out.println("child");
        System.out.println(a);
        System.out.println(super.a);
    }

    /*//会出错
    private void fun1()
    {

    }*/
}


//*********************************二、final特性*********************************
/**
 * final类不允许继承 目的：不允许子类修改语义
 */
final class Father2
{
    /**
     * final类中默认都是final
     */
    public void fun1()
    {

    }
}
/*
class Child1 extends core_java.Father2 //不允许继承
{

}*/

/**
 *  final方法可以继承，不允许覆盖
 */
class Father3
{
    private final int i = 0; //必须显示初始化
    public final void fun1()
    {
        System.out.println("fun1");
    }
}
class Child2 extends Father3
{
    //public void fun1() {} //不允许覆盖(或叫重写)
}

//*********************************三、抽象类特性*********************************
abstract class Fater1
{
    public abstract void fun1();
}