package cn.yangdali.redis;

import java.util.List;

public interface JedisClient {

	/**
	 * redis中添加缓存，并且设置过期时间
	 * 
	 * @param key 
	 * @param value
	 * @param time 过期时间（秒）
	 * @return
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年6月20日 下午9:34:27 
	 *
	 */
	String set(String key, String value, int time);
	String set(String key, String value);
	String get(String key);
	Boolean exists(String key);
	Long expire(String key, int seconds);
	Long ttl(String key);
	Long incr(String key);
	Long hset(String key, String field, String value);
	String hget(String key, String field);
	Long hdel(String key, String... field);
	/**
	 * 根据key在redis中取出所有key下存放的list的值，并且将其转换为需要的类型
	 * 
	 * @param key redis中存放list的key值
	 * @param requiredType 需要转换的泛型
	 * @return
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年8月15日 下午5:30:39 
	 *
	 */
	<T> List<T> getList(String key,Class<T> requiredType);
	/**
	 * 将缓存数据以list形式存入redis中
	 * 
	 * @param key 该value属于的list的key
	 * @param redisValue 存入的value
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年8月16日 上午10:29:14 
	 *
	 */
	void setList(String key, String redisValue);
	/**
	 * 根据redis中list存放的key以及bean自身，删除该bean在redis中的缓存
	 * 
	 * @param key
	 * @param redisValue
	 * @param requiredType
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年8月16日 上午10:43:28 
	 *
	 */
	<T> void deleteListValueByBean(String key, T beanValue);
	/**
	 * 将要缓存的bean实体类存放到键为key的list中
	 * 
	 * @param key 该value属于的list的key
	 * @param beanValue 存入的bean
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年8月16日 上午10:29:14 
	 *
	 */
	<T> void setListByBean(String key, T beanValue);
	
	/**
	 * 将list存入redis中
	 * 
	 * @param key
	 * @param redisValue
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年9月3日 下午6:32:42 
	 *
	 */
	<T> void setList(String key, List<T> redisValue);
}
