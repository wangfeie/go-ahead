package org.example.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 异步发送普通消息
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/23 15:12
 */
public class AsyncProducer {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("groupTest");
        producer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            try {
                final int index = i;
                // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
                Message msg = new Message("TopicTest-Async",
                        "TagA",
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%-10d OK %s %n", index,
                                sendResult.getMsgId());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        System.out.printf("%-10d Exception %s %n", index, throwable);
                        throwable.printStackTrace();
                        countDownLatch.countDown();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }
        }
        //异步发送，如果要求可靠传输，必须要等回调接口返回明确结果后才能结束逻辑，否则立即关闭Producer可能导致部分消息尚未传输成功
        countDownLatch.await(5, TimeUnit.SECONDS);
        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }
}
