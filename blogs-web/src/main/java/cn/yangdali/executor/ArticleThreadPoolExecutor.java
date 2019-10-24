package cn.yangdali.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import cn.yangdali.executor.factory.ArticleThreadFactory;

/**
 * 文章类线程池
 *
 * @author：yangli
 * @date:2019年8月19日 下午5:49:34
 * @version 1.0
 */
@Repository
public class ArticleThreadPoolExecutor {
	/**
	 * 线程池实例
	 */
	private ThreadPoolExecutor executor;
	/**
	 * 线程池内默认线程数量
	 */
	private static final Integer CORE_POOL_SIZE = 5;
	/**
	 * 线程池内最大线程数量
	 */
	private static final Integer MAXIMUM_POOL_SIZE = 10;
	/**
	 * 线程存活时间
	 */
	private static final Long KEEP_ALIVE_TIME = 5L;
	/**
	 * 线程存活时间单位--此处定义为分钟
	 */
	private static final TimeUnit UNIT = TimeUnit.MINUTES;
	/**
	 * 用于存放待执行线程队列
	 */
	private static final BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(20);
	/**
	 * 当线程过多时的拒绝策略
	 * 1.AbortPolicy:直接抛出异常
	 * 2.CallerRunsPolic:只要线程池未关闭，该策略直接在调用者线程中，运行当前被丢弃的任务
	 * 					这样做不会真正的丢弃任务，但是任务提交线程的性能可能会急剧下降。
	 * 3.DisCardOledestPolicy:丢弃无法处理的任务，不予任务处理，如果允许任务丢失，这是最好的办法了。
	 * 
	 * RejectedExecutionHandler:该结构定义了策略类的规则，如果想要自定义策略，则仅需实现该接口即可
	 */
	private static final RejectedExecutionHandler DEFAULT_HANDLER = new ThreadPoolExecutor.AbortPolicy();
	/**
	 * 文章线程池线程创建工厂
	 */
	private static final ThreadFactory THREAD_FACTORY = new ArticleThreadFactory();
	/**
	 * 初始化线程池
	 * 
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年8月19日 下午5:47:28
	 *
	 */
	@PostConstruct
	private void initThreadPoolExecutor() {
		executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, UNIT, WORK_QUEUE, THREAD_FACTORY, DEFAULT_HANDLER)
	}
	public ThreadPoolExecutor getExecutor() {
		return executor;
	}
	public void setExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
	}
}