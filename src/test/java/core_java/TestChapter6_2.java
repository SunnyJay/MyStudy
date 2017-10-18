package core_java;

import com.google.common.reflect.Reflection;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2017/9/21.
 * 测试动态代理和AOP
 */
public class TestChapter6_2
{
    /*
    测试动态代理  java的方法
    常用于编写底层框架
     */
    @Test
    public void test1()
    {
        //1.先生成一个handler MyInvocationHandler里面的invoke是主要逻辑方法
        InvocationHandler handler = new MyInvocationHandler();

        //2.生成一个代理对象p   传入handler
        Person p = (Person) Proxy.newProxyInstance(Person.class.getClassLoader(), new Class[]{Person.class}, handler);

        //3.调用方法
        //调用代理对象p的方法都会被替换为调用invoke方法
        p.say();
        p.sleep();
    }

    /*
   测试动态代理  guava的方法 更简洁
    */
    @Test
    public void test2()
    {
        InvocationHandler handler = new MyInvocationHandler();
        Person person = Reflection.newProxy(Person.class, handler);
        person.say();
        person.sleep();
    }

    /**
     * 测试AOP
     * 关键在于把对象拿到Handler中
     */
    @Test
    public void test3()
    {
        //AOP可以在不改变原对象的基础上为其添加自定义方法
        //AOP就是动态代理，只不过在invoke方法中调用了原对象的方法 原对象通过set方法传进来

        MyAopInvocationHandler handler = new MyAopInvocationHandler();
        Person man = new Man();
        handler.setTarget(man);

        Person person = Reflection.newProxy(Person.class, handler);
        person.say();
    }



}



//*********************************测试对象********************************

//接口
interface Person
{
    void say();
    void sleep();
}

//实现类
class Man implements Person
{

    @Override
    public void say() {

        System.out.println("hi");
    }

    @Override
    public void sleep() {
        System.out.println("zzzz");

    }
}


/*
InvocationHandler的invoke才是是接口方法的真正实现。
 */
class MyInvocationHandler implements InvocationHandler
{

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("正在执行的方法： " + method);
        if(args != null)
        {
            for(Object val:args)
            {
                System.out.println(val);
            }
        }
        else
        {
            System.out.println("该方法没有实参");
        }
        return null;
    }
}


//测试Aop
class MyAopInvocationHandler implements InvocationHandler
{
    //被代理的对象要传进来，因为method的invoke方法要用
    private Object target;

    public void setTarget(Object target)
    {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //第一个方法
        System.out.println("模拟插入方法1");

        //执行自带方法
        method.invoke(target, args);

        //第二个方法
        System.out.println("模拟插入方法2");

        return null;
    }
}