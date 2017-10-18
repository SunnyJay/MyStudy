package core_java;

import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/9/20.
 */
public class TestChapter5_2
{

    enum Color
    {
        RED(3),BLUE(4);

        int id;

        //枚举构造必须是private 可以不写 多余
        Color(int id)
        {
            this.id = id;
        }

        //提供get方法
        public int getId()
        {
            return id;
        }
    }



    /**
     * 测试枚举
     */
    @Test
    public void test1()
    {
        Color color = Color.BLUE;
        System.out.println(color); //toString返回字符串实例

        Color[] colors = Color.values(); //返回所有实例的数组

        //根据字符串生成枚举实例
        //两种方法
        Color cc1 = Color.valueOf("BLUE"); //方法一
        cc1 = Enum.valueOf(Color.class,"BLUE"); //class
    }


    /**
     * 测试包装类
     */
    @Test
    public void test2()
    {
        //Character Void Byte Boolean
        Character c = new Character('c');
        Integer a = new Integer(3);
        System.out.println(Integer.valueOf(3));
        System.out.println(Integer.valueOf("3445"));  //返回Integer对象

        int aa = Integer.parseInt("345");
        System.out.println(aa);

    }



//********************************反射*******************************

    /*
    Class cls = Class.forName("core_java.Teacher");
    cls是个对象，叫类对象，是Class类的实例
    虚拟机为每个“类型”管理一个Class对象，如Teacher有一个自己的Class对象，通过以下两种方式获取
     */

    /**
     * 测试反射
     * 获取class 两种方式
     */
    public void test3()
    {
        Class cls = Teacher.class;//或者 对象.getClass();
        //另一种获取方式
        Teacher teacher = new Teacher("sun", 24);
        teacher.getClass();

        System.out.println(cls.getName());
    }

    /**
     * 测试反射
     * 生成实例  forname、newInstance
     */
    @Test
    public void test4() throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class cls = Class.forName("core_java.Teacher");
        Teacher teacher = (Teacher) cls.newInstance(); //还要强制转换
        teacher.setAge(33);
        System.out.println(teacher);
    }

    /**
     * 测试反射
     * 返回属性名数组  getFields、getDeclaredFields
     */
    @Test
    public void test5() throws ClassNotFoundException
    {
        Class cls = Class.forName("core_java.Teacher");
        Field[] fieldsPublic = cls.getFields(); //返回public方法(the accessible public fields)，Teacher没有，因此数组为空
        for(Field f:fieldsPublic)
        {
            System.out.println(f.getName());
        }

        Field[] fileds = cls.getDeclaredFields(); //all the fields declared by the class
        for(Field f:fileds)
        {
            System.out.println(f.getName());
        }
    }

    /**
     * 测试反射
     * 返回属性  Field  set(obj,value) get(obj)
     */
    @Test
    public void test6() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
    {
        Teacher teacher = new Teacher("nanjun", 55);
        Class tcl = teacher.getClass();

        Field f = tcl.getDeclaredField("age"); //注意和getDeclaredFileds()的区别
        f.setAccessible(true); //这样才可以访问私有属性

        Integer age = (Integer)f.get(teacher);//获得属性的句柄
        System.out.println(age);//打印属性值

        f.set(teacher, 23);//更改属性值
        age = (Integer)f.get(teacher);
        System.out.println(age);
    }

    /**
     * 测试反射
     * 返回方法  getMethod(方法名,参数类型...)  invoke(obj)调用方法
     */
    @Test
    public void test7() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Teacher teacher = new Teacher("nanjun", 55);
        Class tcl = teacher.getClass();

        //利用反射获得方法
        Method m = tcl.getMethod("getAge");
        System.out.println("利用反射获取" +m.invoke(teacher)); //用Invoke调用方法

        //Array类
        Object obj = Array.newInstance(Teacher.class, 3);
    }

    /**
     * 测试反射
     * 利用反射实现函数指针的功能 不建议使用 而使用接口实现函数指针
     */
    @Test
    public void test8() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Teacher teacher = new Teacher("nanjun", 55);
        Class tcl = teacher.getClass();

        Method m = tcl.getMethod("getAge");
        System.out.println("利用反射获取" +m.invoke(teacher));
    }


}

//用于测试反射
class Teacher
{
    private String name;
    private int age;

    public Teacher()
    {
    }

    public Teacher(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    public int getAge()
    {
        return this.age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return "core_java.Teacher{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

