package org.example.producer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 消费者消费
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/23 15:51
 */
public class SomeConsumer {

    public static void main(String[] args) throws Exception {
        // 定义一个pull消费者
        // DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("cg");
        // 定义一个push消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerTest");
        // 指定nameServer
        consumer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        // 指定从第一条消息开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 指定消费topic与tag
        consumer.subscribe("TopicA", "*");
        // 指定采用“广播模式”进行消费，默认为“集群模式”
        // consumer.setMessageModel(MessageModel.BROADCASTING);
        // 注册消息监听器
        // 一旦broker中有了其订阅的消息就会触发该方法的执行，
        // 其返回值为当前consumer消费的状态
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            // 逐条消费消息
            for (MessageExt msg : msgs) {
                System.out.println(msg);
            }
            // 返回消费状态：消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        // 开启消费者消费
        consumer.start();
        System.out.println("Consumer Started");
    }

}
