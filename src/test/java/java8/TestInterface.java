package java8;

import org.junit.Test;

/**
 * @author jack
 * @date 2019/4/11 16:45
 */
public class TestInterface
{
    /**
     * 测试默认方法
     */
    @Test
    public void testDefaultMethod()
    {
        /*
        想象这样一中情况，当多个类实现一个接口的某个方法时，方法的具体实现代码相同，
        这样就会造成代码重复问题。接口增强就相当于把公共的代码抽离出来，放入接口定义中，
        这样实现类对于该方法就不用重新定义，直接调用即可，这很好的解决了实现该接口的子类代码重复的问题。
         */
        Car benz = new Benz();
        benz.run();

        Car bmw = new Bmw();
        bmw.run();

    }

    /**
     * 测试静态方法
     */
    @Test
    public void testStaticMethod()
    {
        Car.staticMethod();
    }

    /**
     * 测试同名继承
     */
    @Test
    public void testMultiImplements()
    {
       // 编译都过不了
        // 提示
        // Error:(56, 1) java: 类 java8.Audi从类型 java8.Car 和 java8.Car2 中继承了run() 的不相关默认值
    }
}


class Benz implements Car
{

}


/*
class Audi implements Car, Car2
{
   //编译报错
}
*/

class Bmw implements Car
{
    @Override
    public void run()
    {
        System.out.println("BWM running------------");
    }
}

interface Car
{
    /**
     * Java 8接口支持默认方法 <br>
     * 默认方法和抽象方法之间的区别在于抽象方法需要实现，而默认方法不需要。 <br>
     * 接口提供的默认方法会被接口的实现类继承或者覆写 <br>
     *
     * @return
     */
    default void run()
    {
        System.out.println("running------------");
    }

    /**
     * static方法不能继承
     */
    static void staticMethod() {
        System.out.println("static method invoked! ");
    }
}

interface Car2
{
    default void run()
    {
        System.out.println("running2------------");
    }
}