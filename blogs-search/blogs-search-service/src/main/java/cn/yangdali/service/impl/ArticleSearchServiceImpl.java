package cn.yangdali.service.impl;

import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;

import cn.yangdali.pojo.Article;
import cn.yangdali.service.ArticleSearchService;

/**
 * 文章查询接口实现层
 *
 * @author：yangli	
 * @date:2019年10月22日 下午4:45:33
 * @version 1.0
 */
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService{

	@Override
	public List<Article> getArticleByTitle(String title) {
		//根据文章名称，从搜索引擎中查询文章列表
		return null;
	}

}
