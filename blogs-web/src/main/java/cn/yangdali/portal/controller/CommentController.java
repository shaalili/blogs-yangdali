package cn.yangdali.portal.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yangdali.enums.ArticleStatus;
import cn.yangdali.enums.Role;
import cn.yangdali.pojo.Comment;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CommentService;
import cn.yangdali.util.Functions;

@Controller
public class CommentController {
	@Autowired
	private CommentService commentService;

	@Autowired
	private ArticleService articleService;

	/**
	 * '添加评论
	 *
	 * @param request
	 * @param comment
	 */
	@RequestMapping(value = "/comment", method = { RequestMethod.POST })
	@ResponseBody
	public void insertComment(HttpServletRequest request, Comment comment) {
		// 添加评论
		comment.setCommentCreateTime(new Date());
		comment.setCommentIp(Functions.getIpAddr(request));
		if (request.getSession().getAttribute("user") != null) {
			comment.setCommentRole(Role.ADMIN.getValue());
		} else {
			comment.setCommentRole(Role.VISITOR.getValue());
		}
		comment.setCommentAuthorAvatar(Functions.getGravatar(comment.getCommentAuthorEmail()));
		commentService.insertComment(comment);

		// 更新文章的评论数
		var article = articleService.getArticleByStatusAndId(ArticleStatus.PUBLISH.getValue(), comment.getCommentArticleId());
		articleService.updateCommentCount(article.getArticleId());
	}

}
