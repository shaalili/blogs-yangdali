package cn.yangdali.bloomfilter.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.PageInfo;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import cn.yangdali.bloomfilter.BloomFilterInterface;
import cn.yangdali.constant.ArticleConstant;
import cn.yangdali.enums.ArticleStatus;
import cn.yangdali.executor.ArticleThreadPoolExecutor;
import cn.yangdali.pojo.Article;
import cn.yangdali.service.ArticleService;

/**
 * 文章布隆过滤器操作类
 * 		在新增文章时，同时利用rocketmq将文章id刷入布隆过滤器中
 * 
 * 历史版本
 * 		v1.0.0:初始化布隆过滤器时，将数据库中文章id刷入
 *
 * @author：yangli	
 * @date:2019年9月20日 下午3:47:39
 * @version 1.1
 */
@Repository
public class ArticleBloomFilter implements BloomFilterInterface<Integer>{
	
	@Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleThreadPoolExecutor articleThreadPoolExecutor;
	/**
     * 布隆过滤器（测试过了，线程安全的）
     */
    private BloomFilter<Integer> bloomFilter;
    
    /**
     * 此处初始化布隆过滤器时，将数据库中文章id刷入
     * 
     * 待解决问题：
     * 1.此处如果文章增多，那么如何动态增加布隆过滤器长度？
     * 2.如果文章进行删除，那么如何删除布隆过滤器中的相关数值？
     * 
     * @version: v1.0.0
     * @author: yangli
     * @date: 2019年8月22日 下午3:07:55 
     *
     */
    @PostConstruct
    private void initBloomFilter() {
    	//获取文章总数，每100条起一个线程
    	Integer countArticle = articleService.countArticle();
    	//默认先设置为1000个槽，误判率为0.01
    	bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 1000, 0.01);
    	//记录当前页数
    	int i = 0;
    	while (i * ArticleConstant.ARTICLE_BOOLMFILTER_TO_PAGE_SIZE < countArticle) {
    		//记录当前页数
    		final Integer pageIndex = i;
    		articleThreadPoolExecutor.getExecutor().submit(()->{
    			//查询每个文章的id，并且将其放入过滤器中
    	    	HashMap<String, Object> criteria = new HashMap<>(1);
    			criteria.put("status", ArticleStatus.PUBLISH.getValue());
    			// 文章列表
    			PageInfo<Article> articleList = articleService.pageArticle(pageIndex, ArticleConstant.ARTICLE_BOOLMFILTER_TO_PAGE_SIZE, criteria);
    			List<Article> articles = articleList.getList();
    	    	//初始化布隆过滤器
    			articles.forEach(article->bloomFilter.put(article.getArticleId()));
    		});
    		//当前页数加1
    		i++;
		}
    }

	@Override
	public void put(Integer param) {
		bloomFilter.put(param);
	}

	@Override
	public boolean mightContain(Integer param) {
		return bloomFilter.mightContain(param);
	}
}
