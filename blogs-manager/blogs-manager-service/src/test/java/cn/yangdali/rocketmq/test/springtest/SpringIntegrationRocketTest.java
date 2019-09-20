package cn.yangdali.rocketmq.test.springtest;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring集成Rocket测试类
 *
 * @author：yangli	
 * @date:2019年9月20日 上午9:43:47
 * @version 1.0
 */
public class SpringIntegrationRocketTest {

	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-rocketmq.xml");
		DefaultMQProducer producer = (DefaultMQProducer)applicationContext.getBean("rocketmqProducer");
		Message message = new Message("TopicTest", "TopicA", "Hello RocketMQ".getBytes(RemotingHelper.DEFAULT_CHARSET));
		producer.send(message);
		Thread.sleep(5000);
		applicationContext.getClass().getMethod("close").invoke(applicationContext);
	}
}
