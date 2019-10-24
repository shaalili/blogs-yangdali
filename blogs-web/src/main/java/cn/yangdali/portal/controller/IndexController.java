package cn.yangdali.portal.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.github.pagehelper.PageInfo;

import cn.yangdali.constant.RedisConstant;
import cn.yangdali.enums.ArticleStatus;
import cn.yangdali.enums.LinkStatus;
import cn.yangdali.enums.NoticeStatus;
import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.Comment;
import cn.yangdali.pojo.Link;
import cn.yangdali.pojo.Notice;
import cn.yangdali.pojo.Tag;
import cn.yangdali.redis.JedisClient;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CategoryService;
import cn.yangdali.service.CommentService;
import cn.yangdali.service.LinkService;
import cn.yangdali.service.NoticeService;
import cn.yangdali.service.TagService;

/**
 * 主页相关信息查询
 *
 * @author：yangli
 * @date:2019年9月4日 下午8:21:50
 * @version 1.0
 */
@Controller
public class IndexController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private LinkService linkService;

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private TagService tagService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private JedisClient jedisClient;

	@RequestMapping(value = { "/", "/article" })
	public String index(@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, Model model) {
		HashMap<String, Object> criteria = new HashMap<>(1);
		criteria.put("status", ArticleStatus.PUBLISH.getValue());
		// 文章列表
		PageInfo<Article> articleList = articleService.pageArticle(pageIndex, pageSize, criteria);
		model.addAttribute("pageInfo", articleList);

		// 公告
		List<Notice> noticeList = noticeService.listNotice(NoticeStatus.NORMAL.getValue());
		model.addAttribute("noticeList", noticeList);
		// 友情链接
		List<Link> linkList = linkService.listLink(LinkStatus.NORMAL.getValue());
		model.addAttribute("linkList", linkList);

		// 侧边栏显示
		// 标签列表显示
		// 此处修改为标签存放入缓存中，不直接到数据库中查询，redis中存放标签的list
		// 如果有更新，则同时更新redis中的标签
		List<Tag> allTagList = jedisClient.getList(RedisConstant.TAG_TO_REDIS_THE_KEY, Tag.class);
		// 如果缓存查询为空，则从数据库中查询
		if (CollectionUtils.isEmpty(allTagList)) {
			// 如何防止穿透和雪崩?
			allTagList = tagService.listTag();
			if (CollectionUtils.isEmpty(allTagList)) {
				// 此处将查询信息放入缓存中。如果为空，则将空信息刷入缓存，并且设置过期时间
				jedisClient.setList(RedisConstant.TAG_TO_REDIS_THE_KEY, allTagList);
				jedisClient.expire(RedisConstant.TAG_TO_REDIS_THE_KEY, 500);
			} else {
				// 将结果放入缓存中
				jedisClient.setList(RedisConstant.TAG_TO_REDIS_THE_KEY, allTagList);
			}
		}
		model.addAttribute("allTagList", allTagList);
		// 最新评论
		List<Comment> recentCommentList = commentService.listRecentComment(10);
		// 查询文章，留言，文章总数等数量
		List<String> siteBasicStatistics = new LinkedList<>();
		// 将查询数量统一放入redis中进行缓存
		// 文章总数
		Integer countArticle = articleService.countArticle();
		siteBasicStatistics.add(countArticle.toString());
		// 留言总数
		Integer countArticleComment = articleService.countArticleComment();
		siteBasicStatistics.add(countArticleComment.toString());
		// 分类数量
		Integer countCategory = categoryService.countCategory();
		siteBasicStatistics.add(countCategory.toString());
		// 标签数量
		Integer countTag = tagService.countTag();
		siteBasicStatistics.add(countTag.toString());
		// 链接数量
		Integer countLink = linkService.countLink();
		siteBasicStatistics.add(countLink.toString());
		// 浏览总量
		Integer countArticleView = articleService.countArticleView();
		siteBasicStatistics.add(countArticleView.toString());
		model.addAttribute("siteBasicStatistics", siteBasicStatistics);
		// 最后更新
		Article lastUpdateArticle = articleService.getLastUpdateArticle();
		model.addAttribute("lastUpdateArticle", lastUpdateArticle);
		model.addAttribute("recentCommentList", recentCommentList);
		model.addAttribute("pageUrlPrefix", "/article?pageIndex");
		return "Home/index";
	}

	@RequestMapping(value = "/search")
	public String search(@RequestParam("keywords") String keywords,
			@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, Model model) {
		// 文章列表
		HashMap<String, Object> criteria = new HashMap<>(2);
		criteria.put("status", ArticleStatus.PUBLISH.getValue());
		criteria.put("keywords", keywords);
		PageInfo<Article> articlePageInfo = articleService.pageArticle(pageIndex, pageSize, criteria);
		model.addAttribute("pageInfo", articlePageInfo);

		// 侧边栏显示
		// 标签列表显示
		List<Tag> allTagList = tagService.listTag();
		model.addAttribute("allTagList", allTagList);
		// 获得随机文章
		List<Article> randomArticleList = articleService.listRandomArticle(8);
		model.addAttribute("randomArticleList", randomArticleList);
		// 获得热评文章
		List<Article> mostCommentArticleList = articleService.listArticleByCommentCount(8);
		model.addAttribute("mostCommentArticleList", mostCommentArticleList);
		// 最新评论
		List<Comment> recentCommentList = commentService.listRecentComment(10);
		model.addAttribute("recentCommentList", recentCommentList);
		model.addAttribute("pageUrlPrefix", "/search?pageIndex");
		return "Home/Page/search";
	}

	@RequestMapping("/404")
	public String NotFound(@RequestParam(required = false) String message, Model model) {
		model.addAttribute("message", message);
		return "Home/Error/404";
	}

	@RequestMapping("/500")
	public String ServerError(@RequestParam(required = false) String message, Model model) {
		model.addAttribute("message", message);
		return "Home/Error/500";
	}
}
