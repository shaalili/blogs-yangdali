package cn.yangdali.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.JedisCluster;

/**
 * redis操作类
 *
 * @author：yangli
 * @date:2019年6月20日 下午9:24:17
 * @version 1.0
 */
@Repository("jedisClientCluster")
public class JedisClientCluster implements JedisClient {
	
//	private static final Logger logger = LoggerFactory.getLogger(JedisClientCluster.class);

	@Autowired
	private JedisCluster jedisCluster;

	@Override
	public String set(String key, String value, int time) {
		return jedisCluster.setex(key, time, value);
	}
	
	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return jedisCluster.expire(key, seconds);
	}

	@Override
	public Long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return jedisCluster.hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	@Override
	public Long hdel(String key, String... field) {
		return jedisCluster.hdel(key, field);
	}

	@Override
	public <T> List<T> getList(String key, Class<T> requiredType) {
		List<String> redisValue = jedisCluster.lrange(key, 0, -1);
		List<T> resultValue = new ArrayList<>();
		for (String string : redisValue) {
			T parseObject = JSON.parseObject(string, requiredType);
			resultValue.add(parseObject);
		}
		return resultValue;
	}

	@Override
	public void setList(String key, String redisValue) {
		jedisCluster.lpush(key, redisValue);
	}

	@Override
	public <T> void deleteListValueByBean(String key, T beanValue) {
		String redisValue = JSON.toJSONString(beanValue);
		jedisCluster.lrem(key, 1, redisValue);
	}

	@Override
	public <T> void setListByBean(String key, T beanValue) {
		String redisValue = JSON.toJSONString(beanValue);
		setList(key, redisValue);
	}

	@Override
	public <T> void setList(String key, List<T> redisValue) {
		//jedisCluster.lpush(key, redisValue);方法的第二个参数为可变参数，但是我还没找到如何将list集合转换为可变参数的方法
		//如果后期变动，应将集合直接传入第二个参数，这样就可以不使用循环
		//此处使用循环不知是否会有隐患
		for (T t : redisValue) {
			String jsonString = JSON.toJSONString(t);
			setList(key, jsonString);
		}
	}
}
