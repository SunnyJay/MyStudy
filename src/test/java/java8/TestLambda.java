package java8;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jack
 * @date 2019/4/11 11:29
 */
public class TestLambda
{
    /**
     * 测试语法特性
     */
    @Test
    public void testSyntax()
    {
        // 标准单个语句
        Runnable runnable = () -> {
            System.out.println("vvvvv");
        };

        // 单个语句可以省略大括号和分号
        runnable = () -> System.out.println("vvvvv");

        // 标准多个语句
        runnable = () -> {
            System.out.println("vvvvv");
            System.out.println("bbbbb");
        };
    }

    /**
     * 测试Runable
     */
    @Test
    public void testRunable()
    {

        // 旧方式
        Runnable runnable1 = new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("zzzzzzzzzzz");
            }
        };


        // Lambda方式
        Runnable runnable2 = () -> System.out.println("vvvvv");
    }


    /**
     * 测试遍历List
     */
    @Test
    public void testList()
    {
        List<Integer> list = Lists.newArrayList(1, 3, 4);

        // 旧方式
        for (Integer integer : list)
        {
            System.out.println(integer);
        }

        // lambda方式
        // Consumer使用了@FunctionalInterface
        list.forEach(n -> System.out.println(n));
        Consumer consumer = n -> System.out.println(n);
        list.forEach(consumer);
    }

    /**
     * 测试compare
     */
    @Test
    public void testCompare()
    {
        String[] arrays = new String[]{"seas", "erb", "eeee"};
        // 旧方法
        Arrays.sort(arrays, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return Integer.compare(o1.length(), o2.length());
            }
        });

        // lambda
        // Comparator也使用了@FunctionalInterface
        Arrays.sort(arrays, (o1, o2) -> Integer.compare(o1.length(), o2.length()));
    }

    /**
     * 测试自定义接口
     */
    @Test
    public void testMyInterface()
    {
        // 旧方式
        this.doWork(new MyInterface()
        {
            @Override
            public void sayHi(String word)
            {
                System.out.println(word);
            }
        }, "你好");


        // 新方式
        this.doWork(word -> System.out.println(word), "你好");
    }

    private void doWork(MyInterface myInterface, String word)
    {
        myInterface.sayHi(word);
    }


    /**
     * 测试Stream
     * <p>
     * 在 Java 8 中, 集合接口有两个方法来生成流:<br>
     * 1.stream() − 为集合创建串行流<br>
     * 2.parallelStream() − 为集合创建并行流<br>
     */
    @Test
    public void testStream()
    {
        List<Integer> nums = Lists.newArrayList(1, null, 3, 4, null, 6);

        /*
        stream() 创建stream
        filter(num -> num != null)  将stream转化为另一个stream
        count() 聚合操作（这里是返回stream中的元素）
         */
        long count = nums.stream().filter(num -> num != null).count();
        System.out.println(count);

        /*
        collect(Collectors.toList())
         */
        List<Integer> list = nums.stream().filter(num -> num != null).collect(Collectors.toList());
        System.out.println(nums);
        System.out.println(list);

        /*
        foreach
         */
        nums.stream().forEach(n -> System.out.println(n));
        // 也可以直接list.foreach
        nums.forEach(n -> System.out.println(n));
    }


    /**
     * Collectors 类实现了很多归约操作
     */
    @Test
    public void testCollectors()
    {
        List<String> nums = Lists.newArrayList("1", null, "3", "5", null, "6");

        /*
        合并字符串
         */
        String mergedString = nums.stream().filter(num -> num != null).collect(Collectors.joining(","));
        System.out.println(mergedString);
    }

    /**
     * 对列表进行统计
     */
    @Test
    public void testSummaryStatistics()
    {

        List<Integer> integerList = Lists.newArrayList(3, 65, 23, 4);

        /*
        mapToInt 可以对元素进行修改
         */
        //IntSummaryStatistics statistics = integerList.stream().mapToInt(x -> x).summaryStatistics();
        IntSummaryStatistics statistics = integerList.stream().mapToInt(x -> x * 2).summaryStatistics();
        System.out.println(statistics.toString());
        System.out.println(statistics.getAverage());
        System.out.println(statistics.getMax());
        System.out.println(statistics.getMin());
    }

    /**
     * 并行（parallel）程序
     * <p>
     * parallelStream其实就是一个并行执行的流.它通过默认的ForkJoinPool,可能提高你的多线程任务的速度.<br>
     * parallelStream不是线程安全<br>
     * 使用parallelStream要注意不要使用ArrayList，ArrayList是线程不安全的，可以转化为同步的集合：<br>
     * 通过Collections.synchronizedList(new ArrayList<>())
     */
    @Test
    public void testParallel()
    {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        // 可能是任意的顺序, 每次都可能不一样
        numbers.parallelStream().forEach(n -> System.out.printf(n.toString()));

        System.out.println("-----------------------------------");

        // forEachOrdered可以保证顺序
        numbers.parallelStream().forEachOrdered(n -> System.out.printf(n.toString()));

        System.out.println("-----------------------------------");

        // 线程不安全
        List<Integer> list = new ArrayList<>();
        // 添加到list中
        numbers.parallelStream().map(e -> {
            list.add(e);
            return e;
        }).forEachOrdered(e -> System.out.println(e));

        // 经常会少元素
        System.out.println(list);

        System.out.println("-----------------------------------");

        //线程安全:使用synchronizedList
        List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
        // 添加到list中
        numbers.parallelStream().map(e -> {
            syncList.add(e);
            return e;
        }).forEachOrdered(e -> System.out.println(e));

        System.out.println(syncList);
    }
}

@FunctionalInterface
interface MyInterface
{
    void sayHi(String word);
}

