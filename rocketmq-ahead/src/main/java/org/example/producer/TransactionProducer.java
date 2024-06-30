package org.example.producer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事务消息发送
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/2/26 10:42
 */
public class TransactionProducer {

    public static void main(String[] args) throws Exception {
        // 初始化一个producer并设置Producer group name
        TransactionMQProducer producer = new TransactionMQProducer("groupTest");
        // 设置NameServer地址
        producer.setNamesrvAddr("192.168.77.3:9876;192.168.77.4:9876");
        // 定义一个线程池
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("client-transaction-msg-check-thread");
                    return thread;
                });
        // 为生产者指定一个线程池
        producer.setExecutorService(executorService);
        // 为生产者添加事务监听器
        producer.setTransactionListener(new ICBCTransactionListener());
        // 启动producer
        producer.start();

        String[] tags = {"TAGA", "TAGB", "TAGC"};
        for (int i = 0; i < 3; i++) {
            byte[] body = ("Hi," + i).getBytes();
            Message msg = new Message("TopicTest-Transaction", tags[i], body);
            // 发送事务消息
            // 第二个参数用于指定在执行本地事务时要使用的业务参数
            SendResult sendResult = producer.sendMessageInTransaction(msg, null);
            System.out.println("发送结果为：" + sendResult.getSendStatus());
        }
    }


}
