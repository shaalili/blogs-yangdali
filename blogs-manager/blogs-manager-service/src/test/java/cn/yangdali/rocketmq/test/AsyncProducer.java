package cn.yangdali.rocketmq.test;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * RocketMQ消息生产者（异步发送）
 *
 * @author：yangli
 * @date:2019年9月17日 下午4:50:05
 * @version 1.0
 */
public class AsyncProducer {

	public static void main(String[] args) throws Exception {
		// Instantiate with a producer group name
		DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
		// Specify name server addresses
		producer.setNamesrvAddr("127.0.0.1:9876");
		// Launch the instance
		producer.start();
		// 设定在异步发送失败后，重试次数，
		// 这个可能导致消息多次发送
		// 如果不设置，则默认为两次
		producer.setRetryTimesWhenSendAsyncFailed(0);
		Message message = new Message("TopicTest", "TagA", "OrderID188", "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
		producer.send(message, new SendCallback() {
			
			@Override
			public void onSuccess(SendResult sendResult) {
				System.out.println(sendResult.getMsgId());
			}
			
			@Override
			public void onException(Throwable e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		});
		//Shut down once the producer instance is not longer in use
		producer.shutdown();
	}
}
