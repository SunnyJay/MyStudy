package kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/18.
 */
public class MyMessageDecoder implements Deserializer
{

    @Override
    public void configure(Map configs, boolean isKey)
    {

    }

    @Override
    public Object deserialize(String topic, byte[] data)
    {
        return JSON.parseObject(data, Student.class);
    }

    @Override
    public void close()
    {

    }
}
