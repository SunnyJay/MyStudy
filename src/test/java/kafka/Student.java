package kafka;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/17.
 */
public class Student implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int id;

    public Student(int id, String name, int age)
    {
        this.id = id;
        this.name = name;
        this.age = age;
    }

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

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAge()
    {
        return age;

    }

    public void setAge(int age)
    {
        this.age = age;
    }

    @Override
    public String toString()
    {
        return "Student{" +
                "姓名='" + name + '\'' +
                ", 年龄=" + age +
                '}';
    }
}