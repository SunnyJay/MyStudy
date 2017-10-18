package guava;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/25.
 */
public class TestResource
{

    /**
     * Resources.getResource
     * url.openStream
     */
    @Test
    public void test1()
    {
        URL url = Resources.getResource("config.properties");
        System.out.println(url);
        Properties properties = new Properties();
        try
        {
            properties.load(url.openStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(properties.getProperty("name"));
    }


    /**
     * url.getPath
     */
    @Test
    public void test2()
    {
        URL url = Resources.getResource("config.properties");
        String path = url.getPath();
        System.out.println(url);
        System.out.println(path); //注意path与url的区别,url前面有file前缀
    }

}
