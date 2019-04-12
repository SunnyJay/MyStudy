package java8;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author jack
 * @date 2019/4/11 17:05
 */
public class TestMethodReference
{

    /**
     * 方法引用的标准形式:    类名::方法名。（注意：只需要写方法名，不需要写括号）<br>
     * <p>
     * 引用静态方法	ContainingClass::staticMethodName<br>
     * 引用某个对象的实例方法	containingObject::instanceMethodName<br>
     * 引用某个类型的任意对象的实例方法	ContainingType::methodName<br>
     * 引用构造方法	ClassName::new<br>
     */
    @Test
    public void test1()
    {
        String[] stringsArray = new String[]{"sd", "sf"};
        Arrays.sort(stringsArray, (s1, s2) -> s1.compareToIgnoreCase(s2));

        // 在Java8中，我们可以直接通过方法引用来简写"lambda表达式中已经存在的方法"。
        Arrays.sort(stringsArray, String::compareToIgnoreCase);
    }

    @Test
    public void test2()
    {
        Person a = new Person(50);
        Person b = new Person(40);

        List<Person> arrays = Lists.newArrayList(a, b);

        arrays.forEach(e -> System.out.println(e.getAge() + ""));
        arrays.forEach(System.out::println);
    }
}


class Person
{
    public Person(Integer age)
    {
        this.age = age;
    }

    private Integer age;

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public static int compareByAge(Person a, Person b)
    {
        return a.age.compareTo(b.age);
    }

    @Override
    public String toString()
    {
        return "Person{" +
                "age=" + age +
                '}';
    }
}
