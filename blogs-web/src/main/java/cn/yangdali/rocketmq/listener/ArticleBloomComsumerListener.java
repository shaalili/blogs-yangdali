package cn.yangdali.rocketmq.listener;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import cn.yangdali.bloomfilter.BloomFilterInterface;

/**
 * 文章消息队列消费者回调函数
 * 
 * 存在问题：将文章id（Integer）转换为消息队列中存储的byte[]数组时，暂时将其通过中间String类型转换
 * 此处建议优化，直接将Integer转换为byte[]数组
 *
 * @author：yangli	
 * @date:2019年9月20日 下午4:13:00
 * @version 1.0
 */
public class ArticleBloomComsumerListener implements MessageListenerConcurrently{

	@Autowired
	private BloomFilterInterface<Integer> articleBloomFilter;
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExts, ConsumeConcurrentlyContext concurrentlyContext) {
		//从消息队列中获取文章id
		messageExts.forEach((messageExt)->{
			var articleID = new String(messageExt.getBody());
			articleBloomFilter.put(Integer.valueOf(articleID));
		});
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
}
