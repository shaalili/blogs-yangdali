package cn.yangdali.portal.controller;


import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import cn.yangdali.constant.ArticleConstant;
import cn.yangdali.enums.ArticleStatus;
import cn.yangdali.executor.ArticleThreadPoolExecutor;
import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.Comment;
import cn.yangdali.pojo.Tag;
import cn.yangdali.pojo.User;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CommentService;
import cn.yangdali.service.TagService;
import cn.yangdali.service.UserService;

/**
 * 文章
 *
 * @author：yangli	
 * @date:2019年5月6日 上午11:22:31
 * @version 1.0
 */
@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;
    
    @Autowired
    private ArticleThreadPoolExecutor articleThreadPoolExecutor;
    
    /**
     * 布隆过滤器（测试过了，线程安全的）
     */
    private BloomFilter<Integer> bloomFilter;
    
    /**
     * 此处初始化布隆过滤器时，将数据库中文章id刷入
     * 待解决问题：
     * 1.此处如果文章增多，那么如何动态增加布隆过滤器长度？
     * 2.如果文章进行删除，那么如何删除布隆过滤器中的相关数值？
     * 
     * 下个版本应增加文章时，将添加任务放入消息队列中。更新布隆过滤器的值
     * 
     * @version: v1.0.0
     * @author: yangli
     * @date: 2019年8月22日 下午3:07:55 
     *
     */
    @PostConstruct
    private void initArticle() {
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
    	    	//初始化布隆过滤器
    	    	for (Article article : articleList.getList()) {
    	    		bloomFilter.put(article.getArticleId());
				}
    		});
    		//当前页数加1
    		i++;
		}
    }

    /**
     * 文章详情页显示
     *
     * @param articleId 文章ID
     * @return modelAndView
     */
    @RequestMapping(value = "/article/{articleId}")
    public String getArticleDetailPage(@PathVariable("articleId") Integer articleId, Model model) {

    	//此处利用布隆过滤器进行判断文章是否存在,如果不存在，则直接返回404页面
    	if (!bloomFilter.mightContain(articleId)) {
			return "Home/Error/404";
		}
        //文章信息，分类，标签，作者，评论
        Article article = articleService.getArticleByStatusAndId(ArticleStatus.PUBLISH.getValue(), articleId);
        if (article == null) {
            return "Home/Error/404";
        }

        //用户信息
        User user = userService.getUserById(article.getArticleUserId());
        //将关键信息置空
        user.setUserPass(null);
        user.setUserLastLoginIp(null);
        article.setUser(user);

        //文章信息
        model.addAttribute("article", article);

        //评论列表
        List<Comment> commentList = commentService.listCommentByArticleId(articleId);
        model.addAttribute("commentList", commentList);

        //相关文章
        List<Integer> categoryIds = articleService.listCategoryIdByArticleId(articleId);
        List<Article> similarArticleList = articleService.listArticleByCategoryIds(categoryIds, 5);
        model.addAttribute("similarArticleList", similarArticleList);

        //猜你喜欢
        List<Article> mostViewArticleList = articleService.listArticleByViewCount(5);
        model.addAttribute("mostViewArticleList", mostViewArticleList);

        //获取下一篇文章
        Article afterArticle = articleService.getAfterArticle(articleId);
        model.addAttribute("afterArticle", afterArticle);

        //获取上一篇文章
        Article preArticle = articleService.getPreArticle(articleId);
        model.addAttribute("preArticle", preArticle);

        //侧边栏
        //标签列表显示
        List<Tag> allTagList = tagService.listTag();
        model.addAttribute("allTagList", allTagList);
        //获得随机文章
        List<Article> randomArticleList = articleService.listRandomArticle(8);
        model.addAttribute("randomArticleList", randomArticleList);
        //获得热评文章
        List<Article> mostCommentArticleList = articleService.listArticleByCommentCount(8);
        model.addAttribute("mostCommentArticleList", mostCommentArticleList);

        return "Home/Page/articleDetail";
    }

    /**
     * 点赞增加
     *
     * @param id 文章ID
     * @return 点赞量数量
     */
    @RequestMapping(value = "/article/like/{id}", method = {RequestMethod.POST})
    @ResponseBody
    public Integer increaseLikeCount(@PathVariable("id") Integer id) {
        Article article = articleService.getArticleByStatusAndId(ArticleStatus.PUBLISH.getValue(), id);
        Integer articleCount = article.getArticleLikeCount() + 1;
        article.setArticleLikeCount(articleCount);
        articleService.updateArticle(article);
        return articleCount;
    }

    /**
     * 文章访问量数增加
     *
     * @param id 文章ID
     * @return 访问量数量
     */
    @RequestMapping(value = "/article/view/{id}", method = {RequestMethod.POST})
    @ResponseBody
    public Integer increaseViewCount(@PathVariable("id") Integer id) {
        Article article = articleService.getArticleByStatusAndId(ArticleStatus.PUBLISH.getValue(), id);
        Integer articleCount = article.getArticleViewCount() + 1;
        article.setArticleViewCount(articleCount);
        articleService.updateArticle(article);
        return articleCount;
    }
}
