package com.heima.kafka.sample;

import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class ProducerQuickStart {

    @SneakyThrows
    public static void main(String[] args) {
        //1.kafka链接配置
        Properties prop = new Properties();
        //kafka链接地址
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");

        //key和value的序列化
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //ack配置，消息确认机制
        prop.put(ProducerConfig.ACKS_CONFIG, "all");

        //重试次数
        prop.put(ProducerConfig.RETRIES_CONFIG, 10);

        //数据压缩
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        //2.创建kafka生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(prop);

        //3.发送消息
        for (int i = 0; i < 5; i++) {
            ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>("itcast-topic-input", "hello kafka");
            producer.send(producerRecord);
        }

        //同步发送消息
/*        RecordMetadata recordMetadata = producer.send(producerRecord).get();
        System.out.println(recordMetadata.offset());*/

        //异步发送消息
/*        producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null){
                System.out.println("记录异常信息到日志表中");
            }
            System.out.println(metadata.offset());
        });*/

        //4.关闭消息通道
        producer.close();
    }
}
