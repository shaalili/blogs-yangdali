package cn.yangdali.rocketmq.test.listener;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * rocketmq生产者的一个监听器，生产者获得消息后，调用该回调函数执行相对应的操作
 *
 * @author：yangli	
 * @date:2019年9月20日 上午11:14:42
 * @version 1.0
 */
public class ComsumerSpringListener implements MessageListenerConcurrently{

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
		//用于记录消息队列中的结果
		String result = "";
		for (MessageExt messageExt : msgs) {
			result = new String(messageExt.getBody());
			System.out.println(result);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
