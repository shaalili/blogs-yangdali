package cn.yangdali.rocketmq.test;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

import cn.yangdali.rocketmq.test.listener.ComsumerSpringListener;

public class Consumer {

	public static void main(String[] args) throws MQClientException {
		//Instantiate with specified consumer group name
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
		//Specify name server address
		consumer.setNamesrvAddr("127.0.0.1:9876");
		//Subscribe one more more topics to consume(订阅一个topics用于消费)
		consumer.subscribe("TopicTest", "*");
		//Register callback to execute on arrival of messages fetched from brokers(注册回调函数，在接收到消息时便于执行)
		consumer.registerMessageListener(new ComsumerSpringListener());
		//Launch the consumer instance
		consumer.start();
		System.out.println("Consumer Started");
	}
}
