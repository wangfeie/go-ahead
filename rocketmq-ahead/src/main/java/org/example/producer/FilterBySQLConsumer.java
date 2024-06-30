package org.example.producer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 定义SQL过滤Consumer
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/26 15:42
 */
public class FilterBySQLConsumer {

    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerTest");
        consumer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("TopicTest-FilterBySQL", MessageSelector.bySql("age between 0 and 6"));
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for (MessageExt me : list) {
                System.out.println(me);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.out.println("Consumer Started");
    }

}
