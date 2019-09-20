package cn.yangdali.rocketmq.test;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * rocketmq消息生产者测试(同步消息发送)
 *
 * @author：yangli
 * @date:2019年9月17日 下午3:37:54
 * @version 1.0
 */
public class SyncProducer {

	public static void main(String[] args) throws Exception {
		// Instantiate with a producer group name(设置生产者的组名称，并且实例化)
		DefaultMQProducer producer = new DefaultMQProducer("please_remname_unique_group_name");
		//指定发送失败重试次数
		producer.setRetryTimesWhenSendFailed(3);
		//设置最大消息数
		producer.setMaxMessageSize(999999);
		// Specify name server addresses(指定服务器地址)
		producer.setNamesrvAddr("127.0.0.1:9876");
		producer.start();
		// Create a message instance, specifying topic,tag and message body
		Message message = new Message("TopicTest", "TagA", "Hello RocketMQ".getBytes(RemotingHelper.DEFAULT_CHARSET));
		// Call send message to deliver message to one of brokers
		SendResult sendResult = producer.send(message);
		System.out.printf("%s%n", sendResult);
		producer.shutdown();
	}
}
