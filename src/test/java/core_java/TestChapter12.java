package core_java;

import org.junit.Test;

import javax.script.Compilable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/21.
 * 泛型程序设计
 */
public class TestChapter12
{
    /**
     * 测试简单的泛型类
     */
    @Test
    public void test1()
    {
        Teacher12<String> teacher12 = new Teacher12<>();
        teacher12.setAge(12);
        teacher12.setName("sun");

        Teacher12_2<String, Integer> teacher12_2 = new Teacher12_2<>();
        teacher12_2.setName("sun");
        teacher12_2.setAge(21);
    }

    /**
     * 测试泛型方法
     *
     * 注意强制转换
     */
    @Test
    public void test2()
    {
        this.<String>fun1("jack"); //调用时加上泛型，但是泛型是可以省略的，如下所示
        String name = this.fun1("sun"); //大多数情况下可以省略泛型，但是传入多个不同类型的参数时注意不要产生歧义，
        System.out.println(name);

        // 调用泛型方法时，编译器会自动对返回的Object强制转换为你的类型
        //即fun1("sun")返回为Object,然后再(String)强制转换


    }

    /**
     * 测试类型擦除 泛型类
     */
    @Test
    public void test3()
    {
        //无论何时定义一个泛型类，都将自动提供一个原始类型(raw type)
        //原始类型就是删除类型形参，并替换为第一个限定类型（无限定类型替换为object）
        //注意类型擦出的概念


        //如Teacher12就会被Object替换
    }

    /**
     * 测试类型擦除 泛型方法
     *
     */
    @Test
    public void test4()
    {
        //泛型方法擦除可能会与多态发生冲突，此时编译器会自动生成一个桥方法
        //注意桥方法的概念


        //如fun3擦除后为
        //public core_java.Teacher12_3 core_java.Teacher12_3 fun3(core_java.Teacher12_3 arg)
    }


    /**
     * 测试泛型通配符 重要
     */
    @Test
    public void test5()
    {
        //概念一
        //List<core_java.Cat>与List<core_java.Teacher>没有任何关系

        //概念二
        //RedCat是Cat的子类
        //List<core_java.RedCat>不是List<core_java.Cat>，他们没有任何关系

        //概念三
        //参数化类型永远可以转换为一个原始类型   List<core_java.Cat> ——》 List
        // 即List<core_java.Cat>是原始类型List的一个子类型！

        //概念四
        //泛型类可以扩展或实现其他的泛型类
        //如ArryList<T>是List<T>的子类
        //所以，ArryList<core_java.Cat>是List<core_java.Cat>的子类
        //但是ArryList<core_java.Cat>是ArryList<core_java.RedCat>没有关系(虽然recat是cat的子类)

        //概念五
        //? extend core_java.Cat 限制必须为Cat的超类
        //即 Pair<RedCate>是Pair<?extends core_java.Cat>的子类

        //? super core_java.RedCat 限制必须是RedCat的父类
        //即 Pair<core_java.Cat>是Pair<? super core_java.RedCat>的父类


        // ArrayList<? extends core_java.Cat> 是 ArrayList<core_java.Cat>的父类  因为Cat是Cat的本身
        ArrayList<? extends Cat> a1 = new ArrayList<Cat>(); //多态

        // ArrayList<? extends core_java.Cat> 是 ArrayList<core_java.RedCat>的父类  因为Cat是RedCat的父类
        ArrayList<? extends Cat> a2 = new ArrayList<RedCat>(); //多态

        //ArrayList<? super core_java.RedCat>是ArrayList<core_java.Cat>的父类  因为<? super core_java.RedCat>包含了cat
        ArrayList<? super RedCat> a3 = new ArrayList<Cat>(); //多态

        //ArrayList<? super core_java.RedCat>是ArrayList<Object>的父类  因为<? super core_java.RedCat>包含了Obejct
        ArrayList<? super RedCat> a4 = new ArrayList<Object>(); //多态

        //ArrayList<? super core_java.RedCat>是ArrayList<core_java.RedCat>的父类  因为<? super core_java.RedCat>包含了Redcat
        ArrayList<? super RedCat> a5 = new ArrayList<RedCat>(); //多态
    }

    /**
     * 测试泛型数据 java不支持
     */
    @Test
    public void test6()
    {
        //不支持
        //core_java.Teacher12<String>[] teacher_array = new core_java.Teacher12<String>[10];

        Teacher12<String> teacher = new Teacher12<String>();
    }


    /*
      泛型方法1 简单
      注意一定要在返回类型前加<T>
     */
    public <T> T fun1(T arg)
    {
        System.out.println(arg);
        return arg;
    }


    /*
    限定类1（接口或类）
    限定T必须是Comparable的子类，即实现Comparable接口
    注意是extends而不是implements
     */
    public <T extends Compilable> T fun2(T arg)
    {
        System.out.println(arg);
        return arg;
    }

    /*
    限定类2 多个
    限定类用&分割，逗号用来分隔类型变量
    限定中可以有多个接口，但是最多一个类！而且放在第一个
    */
    public <T extends Teacher12_3 & Compilable & Serializable> void fun3(T arg)
    {
        System.out.println(arg);
    }

    /*
    限定类3 多个 List<T>
    */
    public <T extends Teacher12_3 & Compilable & Serializable> List<T> fun4(final T arg)
    {
        ArrayList<T> list = new ArrayList<T>(){{add(arg);}};
        return list;
    }

}



//*************************************测试对象********************************
//一个简单的泛型类
class Teacher12<T>
{
    private T name;
    private int age;

    public void setName(T name)
    {
        this.name = name;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public T getName()
    {
        return name;
    }

    public int getAge()
    {
        return age;
    }
}

/*
带两个参数的泛型类
T U S   KV 名字不要乱起 用这几个就行
 */
class Teacher12_2<T, U>
{
    private T name;
    private U age;

    public T getName()
    {
        return name;
    }

    public void setName(T name)
    {
        this.name = name;
    }

    public U getAge()
    {
        return age;
    }

    public void setAge(U age)
    {
        this.age = age;
    }
}

class Teacher12_3
{

}


//用于测试继承在泛型中的应用
class Cat
{

}
class RedCat extends Cat
{

}
