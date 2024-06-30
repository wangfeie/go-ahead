package org.example.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 同步发送普通消息
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/23 10:29
 */
public class SyncProducer {

    public static void main(String[] args) throws Exception {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("groupTest");
        // 设置NameServer地址
        producer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        // 启动producer
        producer.start();
        producer.setRetryTimesWhenSendFailed(0);

        for (int i = 0; i < 100; i++) {
            // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
            Message message = new Message("TopicTest-Sync", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 利用producer进行发送，并同步等待发送结果
            SendResult sendResult = producer.send(message);
            System.out.printf("%s%n", sendResult);
        }
        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }


}
