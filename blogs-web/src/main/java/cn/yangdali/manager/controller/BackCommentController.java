package cn.yangdali.manager.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;

import cn.yangdali.enums.ArticleStatus;
import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.Comment;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CommentService;
import cn.yangdali.util.Functions;

@Controller
@RequestMapping("/admin/comment")
public class BackCommentController {

	@Resource(name = "commentService")
	private CommentService commentService;

	@Resource(name = "articleService")
	private ArticleService articleService;

	/**
	 * 评论页面
	 *
	 * @param pageIndex
	 *            页码
	 * @param pageSize
	 *            页大小
	 * @return modelAndView
	 */
	@RequestMapping(value = "")
	public String commentListView(@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, Model model) {
		PageInfo<Comment> commentPageInfo = commentService.listCommentByPage(pageIndex, pageSize);
		model.addAttribute("pageInfo", commentPageInfo);
		model.addAttribute("pageUrlPrefix", "/admin/comment?pageIndex");
		return "Admin/Comment/index";
	}

	/**
	 * 添加评论
	 *
	 * @param request
	 * @param comment
	 */
	@RequestMapping(value = "/insert", method = { RequestMethod.POST })
	@ResponseBody
	public void insertComment(HttpServletRequest request, Comment comment) {
		// 添加评论
		comment.setCommentIp(Functions.getIpAddr(request));
		comment.setCommentCreateTime(new Date());
		commentService.insertComment(comment);
		// 更新文章的评论数
		Article article = articleService.getArticleByStatusAndId(null, comment.getCommentArticleId());
		articleService.updateCommentCount(article.getArticleId());
	}

	/**
	 * 删除评论
	 *
	 * @param id
	 *            批量ID
	 */
	@RequestMapping(value = "/delete/{id}")
	public void deleteComment(@PathVariable("id") Integer id) {
		Comment comment = commentService.getCommentById(id);
		// 删除评论
		commentService.deleteComment(id);
		// 删除其子评论
		List<Comment> childCommentList = commentService.listChildComment(id);
		for (int i = 0; i < childCommentList.size(); i++) {
			commentService.deleteComment(childCommentList.get(i).getCommentId());
		}
		// 更新文章的评论数
		Article article = articleService.getArticleByStatusAndId(null, comment.getCommentArticleId());
		articleService.updateCommentCount(article.getArticleId());
	}

	/**
	 * 编辑评论页面显示
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}")
	public String editCommentView(@PathVariable("id") Integer id, Model model) {
		Comment comment = commentService.getCommentById(id);
		model.addAttribute("comment", comment);
		return "Admin/Comment/edit";
	}

	/**
	 * 编辑评论提交
	 *
	 * @param comment
	 * @return
	 */
	@RequestMapping(value = "/editSubmit", method = RequestMethod.POST)
	public String editCommentSubmit(Comment comment) {
		commentService.updateComment(comment);
		return "redirect:/admin/comment";
	}

	/**
	 * 回复评论页面显示
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/reply/{id}")
	public String replyCommentView(@PathVariable("id") Integer id, Model model) {
		Comment comment = commentService.getCommentById(id);
		model.addAttribute("comment", comment);
		return "Admin/Comment/reply";
	}

	/**
	 * 回复评论提交
	 *
	 * @param request
	 * @param comment
	 * @return
	 */
	@RequestMapping(value = "/replySubmit", method = RequestMethod.POST)
	public String replyCommentSubmit(HttpServletRequest request, Comment comment) {
		// 文章评论数+1
		Article article = articleService.getArticleByStatusAndId(ArticleStatus.PUBLISH.getValue(),
				comment.getCommentArticleId());
		article.setArticleCommentCount(article.getArticleCommentCount() + 1);
		articleService.updateArticle(article);
		// 添加评论
		comment.setCommentCreateTime(new Date());
		comment.setCommentIp(Functions.getIpAddr(request));
		commentService.insertComment(comment);
		return "redirect:/admin/comment";
	}

}
