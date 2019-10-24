package cn.yangdali.service;


import java.util.List;

import cn.yangdali.pojo.Article;

/**
 * 文章全文检索接口
 *
 * @author：yangli	
 * @date:2019年10月22日 下午4:36:48
 * @version 1.0
 */
public interface ArticleSearchService {

	/**
	 * 根据文章名称在ElasticSearch中进行模糊检索
	 * 
	 * @param title
	 * @return
	 * @version: v1.0.0
	 * @author: yangli
	 * @date: 2019年10月22日 下午4:37:34 
	 *
	 */
	List<Article> getArticleByTitle(String title);
}
