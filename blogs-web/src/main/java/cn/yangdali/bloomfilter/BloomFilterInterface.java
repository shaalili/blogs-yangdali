package cn.yangdali.bloomfilter;

/**
 * 定义布隆过滤器操作方法
 *
 * @author：yangli	
 * @date:2019年9月20日 下午3:46:40
 * @version 1.0
 */
public interface BloomFilterInterface<T> {

	/**
	 * 将数据存入布隆过滤器中
	 * 
	 * @param param
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年9月20日 下午4:08:35 
	 *
	 */
	void put(T param);
	
	/**
	 * 查看该参数是否存在于过滤器中
	 * 
	 * @param param
	 * @return
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年9月20日 下午4:28:00 
	 *
	 */
	boolean mightContain(T param);
}
