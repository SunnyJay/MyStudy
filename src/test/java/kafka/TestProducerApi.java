package kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/10/12.
 */
public class TestProducerApi
{
    private static Properties producerProps = new Properties();

    @BeforeClass
    public static void setUp()
    {
        //不用指定全部的broker，它将自动发现集群中的其余的borker（最好指定多个，万一有服务器故障）。
        producerProps.put("bootstrap.servers", "10.1.5.196:9092,10.1.5.196:9093,10.1.5.196:9094");

        //ack是判别请求是否为完整的条件（就是是判断是不是成功发送了）。
        //我们指定了“all”将会阻塞消息，这种设置性能最低，但是是最可靠的。
        producerProps.put("acks", "all");

        //retries，如果请求失败，生产者会自动重试，我们指定是0次，如果启用重试，则会有重复消息的可能性。
        producerProps.put("retries", 0);

        //生产者的缓冲空间池保留尚未发送到服务器的消息
        //缓存的大小是通过 batch.size 配置指定的。值较大的话将会产生更大的批。并需要更多的内存（因为每个“活跃”的分区都有1个缓冲区）。
        producerProps.put("batch.size", 16384);

        //在发送当前batch时等多久额外的信息 默认是0
        //producer会在batch满了或者这个时间到了发送(无论batch满不满) 默认是0就意味着即时发送
        //大于0会导致延迟 但是增加吞吐量
        producerProps.put("linger.ms", 1);

        producerProps.put("buffer.memory", 33554432);

        producerProps.put("session.timeout.ms", "30000"); //消费者停止心跳的时间超过session.timeout.ms,那么就会认为是故障的

        //这个deserializer设置如何把byte转成object类型，
        // 通过指定string解析器，我们告诉获取到的消息的key和value只是简单个string类型。
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


    }


    /**
     * 测试最简单的send
     */
    @Test
    public void test1()
    {
        Producer<String, String> producer = new KafkaProducer<>(producerProps);
        for(int i = 0; i < 100; i++)
        {
            //异步发送一条消息到topic，并调用callback（当发送已确认）。
            ProducerRecord record = new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i));
            producer.send(record);
        }

        producer.close();
    }

    /**
     *  测试send和callback
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test2() throws ExecutionException, InterruptedException
    {
        Producer<String, String> producer = new KafkaProducer<>(producerProps);

        for(int i = 0; i < 100; i++)
        {
            //异步发送一条消息到topic，并调用callback（当发送已确认）。
            ProducerRecord record = new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i));

            //当发送成功即发送确认后，才会调用Callback
            //发送到同一个分区的消息回调保证按一定的顺序执行
            producer.send(record,
                    new Callback()
                    {
                        @Override
                        public void onCompletion(RecordMetadata metadata, Exception exception)
                        {
                            if (exception != null)
                            {
                                exception.printStackTrace();
                            }
                            System.out.println("The offset of the record we just sent is:" + metadata.offset());
                        }
                    });
        }

        producer.close();
    }

    /**
     * 测试send和get
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test3() throws ExecutionException, InterruptedException
    {
        Producer<byte[], byte[]> producer = new KafkaProducer<>(producerProps);
        byte[] key = "key".getBytes();
        byte[] value = "value".getBytes();
        ProducerRecord<byte[],byte[]> record = new ProducerRecord<>("my-topic", key, value);

        //如果future调用get()，则将阻塞，直到相关请求完成并返回该消息的metadata，或抛出发送异常。
        producer.send(record).get(); //get返回RecordMetadata
    }

    /**
     * 封装自定义消息 使用文本序列化
     * keyedmessage
     */
    @Test
    public void test4()
    {
        /*可以使用两种方式序列化自定义消息对象：
        1.二进制
        2.文本 json  */

        //方式一:json
        Student student1 = new Student(1,"jack",23);
        Student student2 = new Student(2,"li",25);
        Gson gson = new Gson();
        String str1 = gson.toJson(student1);
        String str2 = gson.toJson(student2);
        Producer<String, String> producer = new KafkaProducer<>(producerProps);
        ProducerRecord record1 = new ProducerRecord<>("topic_obj", student1.getId(), str1);
        ProducerRecord record2 = new ProducerRecord<>("topic_obj", student2.getId(), str2);

        producer.send(record1);
        producer.send(record2);

        producer.close();
    }

    /**
     * 封装自定义消息 使用二进制序列化
     * keyedmessage
     */
    @Test
    public void test5()
    {
        //方式二:二进制
        producerProps.put("value.serializer", "kafka.MyMessageEncoder"); //value的序列化类
        Student student1 = new Student(1,"jack",23);
        Student student2 = new Student(2,"li",25);

        Producer<String, Student> producer = new KafkaProducer<>(producerProps);
        ProducerRecord record1 = new ProducerRecord<>("topic_obj", String.valueOf(student1.getId()), student1);
        ProducerRecord record2 = new ProducerRecord<>("topic_obj", String.valueOf(student2.getId()), student2);

        producer.send(record1);
        producer.send(record2);
    }
}
