package org.example.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 消息过滤发送
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/26 15:36
 */
public class FilterByTagProducer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("groupTest");
        producer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        producer.start();

        String[] tags = {"myTagA", "myTagB", "myTagC"};
        for (int i = 0; i < 10; i++) {
            byte[] body = ("Hi," + i).getBytes();
            String tag = tags[i%tags.length];
            Message msg = new Message("filterTopic",tag,body);
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult);
        }
        producer.shutdown();
    }

}
