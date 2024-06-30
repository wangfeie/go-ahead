package org.example.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * 发送顺序消息
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/23 20:08
 */
public class OrderedProducer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("groupTest");
        producer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        producer.start();

        for (int i = 0; i < 100; i++) {
            Integer orderId = i;
            byte[] body = ("Hi," + i).getBytes();
            Message msg = new Message("Topic-Ordered", "TagA", body);
            SendResult sendResult = producer.send(msg, (list, message, o) -> {
                int index = orderId % list.size();
                return list.get(index);
            }, orderId);
            System.out.println(sendResult);
        }
        producer.shutdown();
    }

}
