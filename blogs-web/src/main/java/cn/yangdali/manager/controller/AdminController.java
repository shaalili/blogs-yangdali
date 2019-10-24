package cn.yangdali.manager.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.Comment;
import cn.yangdali.pojo.User;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CommentService;
import cn.yangdali.service.UserService;
import cn.yangdali.util.Functions;

@Controller
public class AdminController {

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "articleService")
	private ArticleService articleService;

	@Resource(name = "commentService")
	private CommentService commentService;

	/**
	 * 后台首页
	 *
	 * @return
	 */
	@RequestMapping("/admin")
	public String index(Model model) {
		// 文章列表
		List<Article> articleList = articleService.listRecentArticle(5);
		model.addAttribute("articleList", articleList);
		// 评论列表
		List<Comment> commentList = commentService.listRecentComment(5);
		model.addAttribute("commentList", commentList);
		return "Admin/index";
	}

	/**
	 * 登录页面显示
	 *
	 * @return
	 */
	@RequestMapping("/login")
	public String loginPage() {
		return "Admin/login";
	}

	/**
	 * 登录验证
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/loginVerify", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> loginVerify(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>(4);
		var username = request.getParameter("username");
		var password = request.getParameter("password");
		User user = userService.getUserByNameOrEmail(username);
		if (user == null || !user.getUserPass().equals(password)) {
			map.put("code", 0);
			map.put("msg", "用户名或密码错误！");
		} else {
			// 登录成功
			map.put("code", 1);
			map.put("msg", "");
			// 添加session
			user.setUserLastLoginTime(new Date());
			user.setUserLastLoginIp(Functions.getIpAddr(request));
			request.getSession().setAttribute("loginUser", user);
			request.getSession().setAttribute("user", user);
			userService.updateUser(user);
		}
		return map;
	}

	/**
	 * 退出登录
	 *
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/admin/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.removeAttribute("loginUser");
		session.invalidate();
		return "redirect:/";
	}

}
