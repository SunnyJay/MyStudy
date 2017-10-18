package guava;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.junit.AfterClass;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/21.
 */
public class TestOrdering
{
    private static List<String> stringList;
    @AfterClass
    public static void setUp()
    {
        stringList = Lists.newArrayList();
        stringList.add("luluyang");
        stringList.add("songyangjia");
        stringList.add("chagnhaha");
        stringList.add("buzhidaohaofanaaaaaaaa");
        stringList.add("wozhedebuzhidaoa");
    }

    /**
     * 自然排序
     * Ordering静态方法
     */
    @Test
    public void test1()
    {
        //对传入的参数进行自然排序，例如：String排字典序，Int排大小
        Ordering<String> natural = Ordering.natural();
        stringList = natural.sortedCopy(stringList);
        System.out.println(stringList);
    }

    /**
     * 无序
     */
    @Test
    public void test2()
    {
        Ordering<Object> arbitrary = Ordering.arbitrary();//无序。每次的排序结果都不同
        stringList = arbitrary.sortedCopy(stringList);
        System.out.println(stringList);
    }

    /**
     * 排字典序
     */
    @Test
    public void test3()
    {
        //排字典序
        Ordering<Object> usingToString = Ordering.usingToString();
        stringList = usingToString.sortedCopy(stringList);
        System.out.println(stringList);
    }

    /**
     * 自定义排序  new Ordering
     */
    @Test
    public void test4()
    {
        List<Student> studentList = Lists.newArrayList();
        studentList.add(new Student("a",34));
        studentList.add(new Student("b",24));
        studentList.add(new Student("c",33));
        studentList.add(new Student("d",55));

        Ordering<Student> orderingStudent = new Ordering<Student>()
        {
            @Override
            public int compare(@Nullable Student left, @Nullable Student right)
            {
                return left.getAge() - right.getAge();
            }
        };

        studentList = orderingStudent.sortedCopy(studentList);
        System.out.println(studentList);
    }
}



//**************************测试对象*****************************
class Student
{
    private String name;
    private int age;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public Student(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString()
    {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}