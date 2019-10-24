package cn.yangdali.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * 布隆过滤器线程安全测试
 *
 * @author：yangli	
 * @date:2019年8月19日 下午5:10:04
 * @version 1.0
 */
public class BloomFilterTest {

	static BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 10000);
	public static void main(String[] args) {
		List<String> param = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			param.add(i + "");
		}
		for (int i = 0; i < 1000; i++) {
			String iNumber = param.get(i);
			new Thread(new Runnable() {
				@Override
				public void run() {
					bloomFilter.put(iNumber + "");
				}
			}).start();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 1000; i++) {
			System.out.println(bloomFilter.mightContain(param.get(i)));
		}
		System.out.println(bloomFilter.mightContain("a"));
		System.out.println(bloomFilter.mightContain("b"));
		DefaultSingletonBeanRegistry beanRegistry = new DefaultSingletonBeanRegistry();
		new BeanFactoryPostProcessor() {

			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
				beanFactory.getBean("");
			}
		};
	}
}
