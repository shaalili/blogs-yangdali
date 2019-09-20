package cn.yangdali.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.yangdali.enums.ArticleCommentStatus;
import cn.yangdali.mapper.ArticleCategoryRefMapper;
import cn.yangdali.mapper.ArticleMapper;
import cn.yangdali.mapper.ArticleTagRefMapper;
import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.ArticleCategoryRef;
import cn.yangdali.pojo.ArticleTagRef;
import cn.yangdali.pojo.Category;
import cn.yangdali.pojo.Tag;
import cn.yangdali.service.ArticleService;

/**
 * 文章操作service层 本次更新，加入了RocketMQ 在新增文章的同时，将文章ID添加到布隆过滤器中 删除文章时，将布隆过滤器重新初始化
 * 
 * 历史版本 1.0 实现基本的文章的增删改查操作
 *
 * @author：yangli
 * @date:2019年9月17日 上午11:01:49
 * @version 1.1
 */
@Service
public class ArticleServiceImpl implements ArticleService {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

	@Autowired(required = false)
	private ArticleMapper articleMapper;

	@Autowired(required = false)
	private ArticleCategoryRefMapper articleCategoryRefMapper;

	@Autowired(required = false)
	private ArticleTagRefMapper articleTagRefMapper;

	@Autowired
	private DefaultMQProducer rocketmqProducer;

	@Override
	public Integer countArticle() {
		Integer count = 0;
		try {
			count = articleMapper.countArticle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public Integer countArticleComment() {
		Integer count = 0;
		try {
			count = articleMapper.countArticleComment();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public Integer countArticleView() {
		Integer count = 0;
		try {
			count = articleMapper.countArticleView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public Integer countArticleByCategoryId(Integer categoryId) {
		Integer count = 0;
		try {
			count = articleCategoryRefMapper.countArticleByCategoryId(categoryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public Integer countArticleByTagId(Integer tagId) {
		return articleTagRefMapper.countArticleByTagId(tagId);

	}

	@Override
	public List<Article> listArticle(HashMap<String, Object> criteria) {
		return articleMapper.findAll(criteria);
	}

	@Override
	public List<Article> listRecentArticle(Integer limit) {
		return articleMapper.listArticleByLimit(limit);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateArticleDetail(Article article) {
		article.setArticleUpdateTime(new Date());
		articleMapper.update(article);

		if (article.getTagList() != null) {
			// 删除标签和文章关联
			articleTagRefMapper.deleteByArticleId(article.getArticleId());
			// 添加标签和文章关联
			for (int i = 0; i < article.getTagList().size(); i++) {
				ArticleTagRef articleTagRef = new ArticleTagRef(article.getArticleId(),
						article.getTagList().get(i).getTagId());
				articleTagRefMapper.insert(articleTagRef);
			}
		}

		if (article.getCategoryList() != null) {
			// 添加分类和文章关联
			articleCategoryRefMapper.deleteByArticleId(article.getArticleId());
			// 删除分类和文章关联
			for (int i = 0; i < article.getCategoryList().size(); i++) {
				ArticleCategoryRef articleCategoryRef = new ArticleCategoryRef(article.getArticleId(),
						article.getCategoryList().get(i).getCategoryId());
				articleCategoryRefMapper.insert(articleCategoryRef);
			}
		}
	}

	@Override
	public void updateArticle(Article article) {
		articleMapper.update(article);
	}

	@Override
	public void deleteArticleBatch(List<Integer> ids) {
		articleMapper.deleteBatch(ids);
	}

	@Override
	public void deleteArticle(Integer id) {
		articleMapper.deleteById(id);
	}

	@Override
	public PageInfo<Article> pageArticle(Integer pageIndex, Integer pageSize, HashMap<String, Object> criteria) {
		PageHelper.startPage(pageIndex, pageSize);
		List<Article> articleList = articleMapper.findAll(criteria);
		for (int i = 0; i < articleList.size(); i++) {
			// 封装CategoryList
			List<Category> categoryList = articleCategoryRefMapper
					.listCategoryByArticleId(articleList.get(i).getArticleId());
			if (categoryList == null || categoryList.size() == 0) {
				categoryList = new ArrayList<>();
				categoryList.add(Category.Default());
			}
			articleList.get(i).setCategoryList(categoryList);
		}
		return new PageInfo<>(articleList);
	}

	@Override
	public Article getArticleByStatusAndId(Integer status, Integer id) {
		Article article = articleMapper.getArticleByStatusAndId(status, id);
		if (article != null) {
			List<Category> categoryList = articleCategoryRefMapper.listCategoryByArticleId(article.getArticleId());
			List<Tag> tagList = articleTagRefMapper.listTagByArticleId(article.getArticleId());
			article.setCategoryList(categoryList);
			article.setTagList(tagList);
		}
		return article;
	}

	@Override
	public List<Article> listArticleByViewCount(Integer limit) {
		return articleMapper.listArticleByViewCount(limit);
	}

	@Override
	public Article getAfterArticle(Integer id) {
		return articleMapper.getAfterArticle(id);
	}

	@Override
	public Article getPreArticle(Integer id) {
		return articleMapper.getPreArticle(id);
	}

	@Override
	public List<Article> listRandomArticle(Integer limit) {
		return articleMapper.listRandomArticle(limit);
	}

	@Override
	public List<Article> listArticleByCommentCount(Integer limit) {
		return articleMapper.listArticleByCommentCount(limit);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertArticle(Article article) {
		// 添加文章
		article.setArticleCreateTime(new Date());
		article.setArticleUpdateTime(new Date());
		article.setArticleIsComment(ArticleCommentStatus.ALLOW.getValue());
		article.setArticleViewCount(0);
		article.setArticleLikeCount(0);
		article.setArticleCommentCount(0);
		article.setArticleOrder(1);
		articleMapper.insert(article);
		// 获取新增文章id,并且将其置入消息队列中，用于存放如布隆过滤器
		Integer articleID = article.getArticleId();
		String articleIDToString = String.valueOf(articleID);
		try {
			Message message = new Message("Article_Topic", "Article_Bloom", articleIDToString.getBytes(RemotingHelper.DEFAULT_CHARSET));
			rocketmqProducer.send(message);
		} catch (Exception e) {
			logger.error("新增文章id至rocketmq消息队列中抛出异常,文章id:{},异常信息:{}", articleIDToString, e.getMessage());
		}

		// 添加分类和文章关联
		for (int i = 0; i < article.getCategoryList().size(); i++) {
			ArticleCategoryRef articleCategoryRef = new ArticleCategoryRef(article.getArticleId(),
					article.getCategoryList().get(i).getCategoryId());
			articleCategoryRefMapper.insert(articleCategoryRef);
		}
		// 添加标签和文章关联
		for (int i = 0; i < article.getTagList().size(); i++) {
			ArticleTagRef articleTagRef = new ArticleTagRef(article.getArticleId(),
					article.getTagList().get(i).getTagId());
			articleTagRefMapper.insert(articleTagRef);
		}
	}

	@Override
	public void updateCommentCount(Integer articleId) {
		articleMapper.updateCommentCount(articleId);
	}

	@Override
	public Article getLastUpdateArticle() {
		return articleMapper.getLastUpdateArticle();
	}

	@Override
	public List<Article> listArticleByCategoryId(Integer cateId, Integer limit) {
		return articleMapper.findArticleByCategoryId(cateId, limit);
	}

	@Override
	public List<Article> listArticleByCategoryIds(List<Integer> cateIds, Integer limit) {
		if (cateIds == null || cateIds.size() == 0) {
			return null;
		}
		return articleMapper.findArticleByCategoryIds(cateIds, limit);
	}

	@Override
	public List<Integer> listCategoryIdByArticleId(Integer articleId) {
		return articleCategoryRefMapper.selectCategoryIdByArticleId(articleId);
	}

	@Override
	public List<Article> listAllNotWithContent() {
		return articleMapper.listAllNotWithContent();
	}

}
