package com.lucky.platform.kafkaTest;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author: Loki
 * @data: 2021-10-29 19:34
 **/

public class KafkaDemo {

    private static final Logger log = LoggerFactory.getLogger(KafkaDemo.class);

    private String topic = "lucky";

    @Test
    public void producer() throws Exception{

        Map<String, Object> map = new HashMap<>(16);
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.10.11.53:9092");
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        //默认值是 1 只会到达leader 然后就返回offset, -1 会同步到所有的分区然后在返回 0 只确保消息发送了,不管服务端有没有收到
        map.put(ProducerConfig.ACKS_CONFIG,"1");
        // 16k batch的大小,生产者会尝试将记录合成一个批次,减少请求次数,减少内存碎片
        map.put(ProducerConfig.BATCH_SIZE_CONFIG,"16380");
        // 默认32M 客户端发送数据不是一条一条发送的,会经过缓冲区,把多个消息收集成一个一个batch,在发送到broker
        // 如果设置太小,缓冲区很快被写满,写满后会阻塞用户线程,不让继续往kafka写消息了
        map.put(ProducerConfig.BUFFER_MEMORY_CONFIG,"33554432");
        // 批次延迟上限,在一个批次未满的情况下设置的延迟时间
        map.put(ProducerConfig.LINGER_MS_CONFIG,"0");
        // 1M 请求的最大大小,限制单个请求中发送的批次数,避免发送大量请求
        map.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,"1048576");
        // 生产者空间不足时,send()被阻塞的时间,默认60s
        map.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,"60000");
        // 发送多少条消息后,接收服务端确认,比如设置为1,就是每发一条就要确认一条,发送5条消息等待一次确认
        map.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,"5");
        // 默认32k,若设置 -1 使用操作系统默认值 TCP发送缓冲区的大小  cat /proc/sys/net/core/rmem_max
        map.put(ProducerConfig.SEND_BUFFER_CONFIG,"32768");
        // 默认32k,若设置 -1 使用操作系统默认值 TCP接收缓冲区的大小
        map.put(ProducerConfig.RECEIVE_BUFFER_CONFIG,"32768");
        KafkaProducer<String, String> producer = new KafkaProducer<>(map);
        // 相同key会存储在同一个 分区中,一个分区只允许一个consumer消费,并自己维护消费进度
        for (int i = 0; i < 2; i++) {
            ProducerRecord<String, String> item = new ProducerRecord<>(topic,"item"+i, "word");
            Future<RecordMetadata> send = producer.send(item);
            final RecordMetadata metadata = send.get();
            final int partition = metadata.partition();
            final long offset = metadata.offset();
            log.info("key:{},val:{},partition:{},offset:{}",item.key(),item.value(),partition,offset);
        }
    }

    @Test
    public void consumer(){
        /*
         * 一个运行的consumer, 会自己维护自己的消费进度,一旦你自动提交 是异步的
         * 还没到时间系统挂了,没提交,重启一个consumer,参照之前的offset 会出现重复消费
         * 一个批次的数据还没写数据库成功,但是这个批次的offset异步提交了,服务挂了,重启一个consumer会丢失数据
         *
         */
        Map<String, Object> map = new HashMap<>(16);
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.10.11.53:9092");
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());

        map.put(ConsumerConfig.GROUP_ID_CONFIG,"mac");
        // 配置 项：earliest , latest , none
        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        // 是否自动提交offset,自动提交的时候是异步的,设置为false关闭自动提交 需要手动维护offset
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
        // 多长时间自动提交 默认是5秒
        map.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"15000");
        // 安需求 拉取多少数据
        //map.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,"");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(map);
        // 订阅 topic kafka的consumer会动态的负载均衡
        consumer.subscribe(Arrays.asList(topic), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                log.info("---onPartitionsRevoked---");
                final Iterator<TopicPartition> iter = collection.iterator();
                while (iter.hasNext()){
                    log.info("分区：{}",iter.next().partition());
                }
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                log.info("---onPartitionsAssigned---");
                final Iterator<TopicPartition> iter = collection.iterator();
                while (iter.hasNext()){
                    log.info("分区：{}",iter.next().partition());
                }
            }
        });

        while (true){
            //拉取数据 一次拉取多少数据？
            ConsumerRecords<String, String> poll = consumer.poll(Duration.ofMillis(0));

            if (!poll.isEmpty()){

                /*
                 * 每次poll的时候是获取多个分区的数据,且每个分区的数据是有序的
                 * 1、按消息进度同步提交
                 * 2、按分区粒度同步提交
                 *    通过多线程方式处理,每个分区一个线程
                 * 3、按当前poll的批次同步提交   consumer.commitSync();
                 */
                final Set<TopicPartition> partitions = poll.partitions();

                for (TopicPartition partition : partitions) {
                    // 设置固定分区
                    List<ConsumerRecord<String, String>> records = poll.records(partition);
                    Iterator<ConsumerRecord<String, String>> iter = records.iterator();
                    while (iter.hasNext()){
                        // 一个consumer可以消费多个分区,但是一个分区只能给一个consumer消费
                        ConsumerRecord<String, String> next = iter.next();
                        final int par = next.partition();
                        final long offset = next.offset();
                        log.info("key:{},val:{},partition:{},offset:{}",next.key(),next.value(),par,offset);
                        // 是最安全的,每条记录的更新。
                        /*Map<TopicPartition,OffsetAndMetadata> map = new HashMap<>(2);
                        TopicPartition tPar = new TopicPartition(topic, par);
                        OffsetAndMetadata offsetData = new OffsetAndMetadata(offset);
                        map.put(tPar, offsetData);
                        consumer.commitSync(map);*/
                    }
                    /*
                     * 按照分区提交offset
                     * 需要拿到分区最后一条数据offset的记录
                     */
                    /*Map<TopicPartition,OffsetAndMetadata> commitMap = new HashMap<>(2);
                    long offset = records.get(records.size() - 1).offset();
                    final OffsetAndMetadata offsetData = new OffsetAndMetadata(offset);
                    commitMap.put(partition,offsetData);
                    consumer.commitSync(commitMap);*/
                }
                /*
                 * 按照pool的批次提交
                 * consumer.commitSync();
                 * 一次性处理topic中所以分区的数据
                 */
                /*Iterator<ConsumerRecord<String, String>> iterator = poll.iterator();
                log.info("-------------{}-------------",poll.count());
                while (iterator.hasNext()){
                    // 一个consumer可以消费多个分区,但是一个分区只能给一个consumer消费
                    ConsumerRecord<String, String> next = iterator.next();
                    final int partition = next.partition();
                    final long offset = next.offset();
                    log.info("key:{},val:{},partition:{},offset:{}",next.key(),next.value(),partition,offset);
                }*/
            }
        }
    }
}
