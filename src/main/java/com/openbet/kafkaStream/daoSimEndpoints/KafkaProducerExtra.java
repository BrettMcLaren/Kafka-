package com.openbet.kafkaStream.daoSimEndpoints;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaProducerExtra {

    private final static String TOPIC = "customers";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
    private final static String CLIENT_ID_CONFIG = "Producer";
    private final static Logger log = LoggerFactory.getLogger(KafkaProducerExtra.class);
    private static Producer<Long, String> producer;


    public KafkaProducerExtra() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new org.apache.kafka.clients.producer.KafkaProducer(props);
    }

    public static void main(String... args) throws Exception {
        KafkaProducerExtra producer = new KafkaProducerExtra();
        producer.run(Integer.MAX_VALUE);
    }

    public void run(final int sendMessageCount) throws InterruptedException {


        long time = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(sendMessageCount);

        try {
            for (long index = time; index < time + sendMessageCount; index++) {
                final ProducerRecord<Long, String> record =
                        new ProducerRecord<>(TOPIC, index, "{ \"Customer.id\": \"" + index + "\", \"Customer.username\": \"123\", \"Customer.status\": \"123\", \"Customer.langRef\": \"123\", \"Customer.PersonalDetails.firstName\": \"123\", \"Customer.PersonalDetails.lastName\": \"123\", \"Customer.Regisration.source\": \"123\", \"Customer.Registration.countryRef\": \"CAN\", \"Customer.CustomerAddress.Address.stateRef\": \"123\", \"Customer.CustomerFlags\": \"123\", \"Customer.CustomerGroups\": \"123\", \"Customer.Account.currencyRef\": \"123\" }");
                producer.send(record, (metadata, exception) -> {
                    long elapsedTime = System.currentTimeMillis() - time;
                    if (metadata != null) {
                        System.out.printf("sent record(key=%s value=%s) " +
                                        "meta(partition=%d, offset=%d) time=%d\n",
                                record.key(), record.value(), metadata.partition(),
                                metadata.offset(), elapsedTime);
                        log.info("sent record(key=%s value=%s) " +
                                        "meta(partition=%d, offset=%d) time=%d\n",
                                record.key(), record.value(), metadata.partition(),
                                metadata.offset(), elapsedTime);
                    } else {
                        exception.printStackTrace();
                    }
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await(25, TimeUnit.SECONDS);
        } finally {
            producer.flush();
        }
    }
}
