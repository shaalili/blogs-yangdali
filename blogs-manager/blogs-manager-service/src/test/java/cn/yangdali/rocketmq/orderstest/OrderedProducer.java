package cn.yangdali.rocketmq.orderstest;

import org.apache.rocketmq.client.producer.DefaultMQProducer;


/**
 * 订单发送端
 * 		保证相同订单id的各种消息发往同一个MessageQueue()
 * 		//MessageQueueSelector保证同一个orderId的消息都存储在同一个MessageQueue。
 *
 * @author：yangli	
 * @date:2019年9月18日 下午4:43:13
 * @version 1.0
 */
public class OrderedProducer {

	public static void main(String[] args) throws Exception{
		//Instantiate with a producer group name
		DefaultMQProducer producer = new DefaultMQProducer("ecamplee_group_name");
		//Specify name server address
		producer.setNamesrvAddr("127.0.0.1:9876");
		//Launch the instance
		producer.start();
		String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
		for (int i = 0; i < 100; i++) {
			int orderId = i % 10;
			//Create a message instance, specifying topic,tag and message body
		}
	}
}
