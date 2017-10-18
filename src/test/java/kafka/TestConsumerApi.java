package kafka;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/10/12.
 */
public class TestConsumerApi
{
    private static Properties consumerProps = new Properties();

    @BeforeClass
    public static void setUp()
    {
        consumerProps.put("bootstrap.servers", "10.1.5.196:9092,10.1.5.196:9093,10.1.5.196:9094");
        consumerProps.put("group.id", "Chinese"); //消费者组叫Chinese
        consumerProps.put("enable.auto.commit", "true");
        consumerProps.put("auto.commit.interval.ms", "1000"); //控制自动提交的频率
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    /**
     * 自动提交偏移量
     */
    @Test
    public void test1()
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);

        //客户端订阅了两个topic:  foo和bar
        consumer.subscribe(Arrays.asList("test", "test1"));

        while (true)
        {

            /*
            订阅一组topic后，当调用poll(long）时，消费者将自动加入到消费者组中。
            只要持续的调用poll，消费者将一直保持可用，并继续从分配的分区中接收消息。
            此外，消费者向服务器定时发送心跳。 如果消费者崩溃或无法在session.timeout.ms配置的时间内发送心跳，
            则消费者将被视为死亡，并且其分区将被重新分配。
            */
            ConsumerRecords<String, String> records = consumer.poll(100);

            for (ConsumerRecord<String, String> record : records)
            {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }

    /**
     * 手动提交偏移量
     */
    @Test
    public void test2()
    {
        consumerProps.put("enable.auto.commit", "false");
        consumerProps.put("session.timeout.ms", "30000");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList("foo", "bar"));

        final int minBatchSize = 200;
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();

        while (true)
        {
            ConsumerRecords<String, String> records = consumer.poll(100);

            for (ConsumerRecord<String, String> record : records)
            {
                buffer.add(record);
            }


            /*
            当我们积累足够多的消息后，我们再将它们批量插入到数据库中。
            如果我们设置offset自动提交（之前说的例子），消费将被认为是已消费的。
            这样会出现问题，如果进程在消息被插入到数据库之前失败了，此时消息已经被自动提交，但实际上没有消费成功。
            因此，要在数据库提交成功后，再手动提交位置（偏移）量
            */
            if (buffer.size() >= minBatchSize) //凑够两百条才处理，否则等待
            {
                insertIntoDb(buffer); //插入数据库后再提交位置量，这样我们可以准确控制消息是成功消费的。
                consumer.commitSync(); //提交位置量，提交后才能获取新的消息
                buffer.clear();
            }
        }
    }

    /**
     * 更精细的手动控制提交偏移量
     * 指定某个具体的偏移量为“已提交”
     */
    @Test
    public void test3()
    {
        consumerProps.put("enable.auto.commit", "false");
        consumerProps.put("session.timeout.ms", "30000");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList("foo", "bar")); //动态分区分配

        try
        {
            while(true)
            {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);

                //处理每个分区
                for (TopicPartition partition : records.partitions())
                {
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);

                    //处理分区中的每个消息，此处打印一下
                    for (ConsumerRecord<String, String> record : partitionRecords)
                    {
                        System.out.println(record.offset() + ": " + record.value());
                    }

                    //得到本分区最后消息的偏移量
                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();

                    //提交本分区所有的偏移量
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
                }
            }
        }
        finally
        {
            consumer.close();
        }
    }

    /**
     * 手动分配分区
     * 用途：当消费者进程本身具有高可用性，并且如果它失败，会自动重新启动，
     *      在这种情况下，不需要Kafka检测故障，重新分配分区，因为消费者进程将在另一台机器上重新启动。
     */
    @Test
    public void test4()
    {
        consumerProps.put("enable.auto.commit", "false");
        consumerProps.put("session.timeout.ms", "30000");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        String topic = "my-topic";
        TopicPartition partition0 = new TopicPartition(topic, 0);
        TopicPartition partition1 = new TopicPartition(topic, 1);

        /*
        注意:
        手动分配分区（即，assgin）和动态分区分配的订阅topic模式（即，subcribe）不能混合使用。
        手动分配不会进行分组协调，因此消费者故障不会引发分区重新平衡
         */
        consumer.assign(Arrays.asList(partition0, partition1));
    }


    /**
     * offset存储在其他地方
     */
    @Test
    public void test5()
    {

    }

    /**
     * 控制消费的位置
     * seekToBeginning方法
     */
    @Test
    public void test6()
    {
        /*
        kafka支持消费者去手动的控制消费的位置，可以消费之前的消息也可以跳过最近的消息。
        kafka使用seek(TopicPartition, long)指定新的消费位置。
        用于查找服务器保留的最早和最新的offset的特殊的方法也可用（seekToBeginning(Collection) 和 seekToEnd(Collection)）。
         */

        consumerProps.put("enable.auto.commit", "false");
        consumerProps.put("session.timeout.ms", "30000");

        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        String topic = "my-topic";

        consumer.subscribe(Arrays.asList(topic),
                new ConsumerRebalanceListener()
                {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions)
                    {

                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions)
                    {
                        consumer.seekToBeginning(Lists.newArrayList(partitions));
                    }
                });

        //由于设置了seekToBeginning，则会从0重新开始消费消息
        while(true)
        {
            ConsumerRecords<String, String> records = consumer.poll(100);

            for (ConsumerRecord<String, String> record : records)
            {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }

    /**
     * 消费者流量控制
     * 消息优先级
     */
    @Test
    public void test7()
    {
        /*
        如果消费者分配了多个分区，并同时消费所有的分区，这些分区具有相同的优先级。
        在一些情况下，消费者需要首先消费一些指定的分区，当指定的分区有少量或者已经没有可消费的数据时，则开始消费其他分区。
         */
    }


    /**
     * 多线程处理
     */
    @Test
    public void test8()
    {
        /*
         * Kafka生产者是线程安全的,而Kafka消费者不是线程安全的。
         * KafkaConsumer和KafkaProducer不同，后者是线程安全的，因此我们鼓励用户在多个线程中“共享”一个KafkaProducer实例，
         * 这样通常都要比每个线程维护一个KafkaProducer实例效率要高。
         * 但对于KafkaConsumer而言，它不是线程安全的，所以实现多线程时通常由两种实现方法：
         * 1 每个线程维护一个KafkaConsumer
         * 2 维护“一个”或多个KafkaConsumer，同时维护多个事件处理线程(worker thread)，即线程池
         *
         * 推荐第2种，可以避免将很重的处理逻辑放入消费者的代码中，让工人去干活！有几个消息就找几个工人。
         */

        /*
        第2种的利弊：
        PRO: 可扩展消费者和处理进程的数量。这样单个消费者的数据可分给多个处理器线程来执行，避免对分区的任何限制。
        CON: 跨多个处理器的顺序保证需要特别注意，因为线程是独立的执行，后来的消息可能比遭到的消息先处理，
             这仅仅是因为线程执行的运气。如果对排序没有问题，这就不是个问题。
        CON: 手动提交变得更困难，因为它需要协调所有的线程以确保处理对该分区的处理完成。
         */
    }

    /**
     * 多线程处理，worker thread
     * 一个（或多个）消费者雇佣了很多工人去干活
     */
    @Test
    public void test9()
    {
        ExecutorService service = Executors.newCachedThreadPool();

        //仅一个消费者，也可以多个消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList("test1"));

        while(true)
        {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for(ConsumerRecord<String, String> record: records) //一次拉回多少条消息，就提交多少个工人线程去工作
            {
                /*
                 * 注意，由于多线程，消息处理的顺序可能会乱，因为后一个worker可能比前一个worker更早的处理玩消息
                 */
                service.submit(new Worker(record)); //将处理逻辑解耦，交给worker干活
            }

        }

    }

    class Worker implements Runnable
    {
        ConsumerRecord<String, String> record;
        public Worker(ConsumerRecord<String, String> record)
        {
            this.record = record;
        }


        @Override
        public void run()
        {
            System.out.println("线程"+Thread.currentThread().getName() +
                    "处理了一条消息" + record.offset()+ record.partition());
        }
    }


    /**
     * 接受自定义对象消息 JSON文本
     */
    @Test
    public void test10()
    {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);

        consumer.subscribe(Arrays.asList("topic_obj"));

        while (true)
        {

            ConsumerRecords<String, String> records = consumer.poll(100);

            for (ConsumerRecord<String, String> record : records)
            {
                //System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                Gson gson = new Gson();
                Student student = gson.fromJson(record.value(), Student.class);
                System.out.println(student);
            }
        }
    }

    /**
     * 接受自定义对象消息 二进制
     */
    @Test
    public void test11()
    {
        consumerProps.put("value.deserializer", "kafka.MyMessageDecoder"); //value的反序列化类

        KafkaConsumer<String, Student> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList("topic_obj"));
        while (true)
        {

            ConsumerRecords<String, Student> records = consumer.poll(100);

            for (ConsumerRecord<String, Student> record : records)
            {
                Student student = record.value();
                System.out.println(student.getName());
                System.out.println(student.getId());
            }
        }
    }

    private void insertIntoDb(List<ConsumerRecord<String, String>> buffer)
    {
        System.out.println("插入到了数据库");
    }
}
