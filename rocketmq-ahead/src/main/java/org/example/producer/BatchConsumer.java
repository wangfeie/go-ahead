package org.example.producer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 批量消息消费者
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/26 14:59
 */
public class BatchConsumer {

    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerTest");
        consumer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("batchTopic", "*");
        // 指定每次可以消费10条消息，默认为1
        consumer.setConsumeMessageBatchMaxSize(10);
        // 指定每次可以从Broker拉取40条消息，默认为32
        consumer.setPullBatchSize(40);
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for (MessageExt messageExt : list) {
                System.out.println(messageExt);
            }
            // 消费成功的返回结果
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            // 消费异常时的返回结果
            // return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        });
        consumer.start();
        System.out.println("Consumer Started");
    }

}
