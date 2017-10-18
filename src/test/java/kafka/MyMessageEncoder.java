package kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/17.
 */
public class MyMessageEncoder implements Serializer
{
    @Override
    public void configure(Map configs, boolean isKey)
    {

    }

    @Override
    public byte[] serialize(String topic, Object data)
    {
        return JSON.toJSONBytes(data); //使用fastjson进行二进制序列化
    }

    @Override
    public void close()
    {

    }
}