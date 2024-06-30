package org.example.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 定义SQL过滤Producer
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/26 15:40
 */
public class FilterBySQLProducer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("groupTest");
        producer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        producer.start();

        for (int i = 0; i < 10; i++) {
            try {
                byte[] body = ("Hi," + i).getBytes();
                Message msg = new Message("TopicTest-FilterBySQL", "myTag", body);
                msg.putUserProperty("age", i + "");
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }

}
