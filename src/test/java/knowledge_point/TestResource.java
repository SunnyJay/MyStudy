package knowledge_point;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/25.
 */
public class TestResource
{
    /**
     * 测试Class.getResource()
     */
    @Test
    public void testClassGetResource()
    {
         /*
        输出当前类所在package位置（即类加载路径）
         */
        String path = TestResource.class.getResource("").getPath();
        System.out.println(path);

        /*
        输出classpath路径，即bin(eclipse),idea(main or test)
         */
        path = TestResource.class.getResource("/").getPath();
        System.out.println(path);

        System.out.println("1___________________");
    }

    /**
     * 测试Class.getClassLoader().getResource()
     */
    @Test
    public void testClassLoaderGetResource()
    {
         /*
        返回boot classloader的加载范围，此处返回null
         */
        System.out.println(TestResource.class.getClassLoader().getResource("/"));

        /*
        同于Class.getResource("/"),返回classpath的位置 等价于TestResource.class.getResource("/").getPath()
         */
        String path = TestResource.class.getClassLoader().getResource("").getPath();
        System.out.println(path);
        System.out.println("2___________________");
    }

    /**
     * 测试ClassLoader.getSystemResource
     */
    @Test
    public void testGetSystemResource()
    {
        String path = ClassLoader.getSystemResource("").getPath();
        System.out.println(path);
        System.out.println("3___________________");
    }

    /**
     * 测试Class.getResourceAsStream()
     * 从当前路径查找资源资源
     */
    @Test
    public void testGetResourceAsStream()
    {
        String streamName = TestResource.class.getResourceAsStream("/").toString();
        System.out.println(streamName);
        System.out.println("4___________________");
    }

    /**
     * 测试加载资源文件
     * (1)getResourceAsStream或getClassLoader.getResource里填写资源会自动定位到resources目录下的对应资源
     * (2)而getResource无法找到
     */
    @Test
    public void testGetProperties() throws IOException
    {
        /*
        方法一：getResourceAsStream
         */
        InputStream input = TestResource.class.getClassLoader().getResourceAsStream("config.properties");

        Properties properties = new Properties();
        properties.load(input);
        System.out.println(properties.getProperty("name"));

        /*
        方法二：getResource
        重要：
        （1）括号里写资源，则定位到resources目录下
        （2）括号里不写资源，只写/或“”,则定位classes下
         */
        System.out.println(TestResource.class.getResource("/").getPath());
        //System.out.println(TestResource.class.getResource("config.properties").getPath()); //无法找到

        System.out.println(TestResource.class.getClassLoader().getResource("").getPath());
        System.out.println(TestResource.class.getClassLoader().getResource("config.properties").getPath());

        File file = new File(TestResource.class.getClassLoader().getResource("config.properties").getFile());
        input = new FileInputStream(file);
        properties.load(input);
        System.out.println(properties.getProperty("name"));

        System.out.println("5___________________");
    }
}
