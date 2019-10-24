package cn.yangdali.portal.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.yangdali.enums.LinkStatus;
import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.Link;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.LinkService;

@Controller
public class LinkController {
	@Autowired
	private LinkService linkService;

	@Autowired
	private ArticleService articleService;

	@RequestMapping("/applyLink")
	public String applyLinkView(Model model) {
		// 侧边栏显示
		// 获得热评文章
		List<Article> mostCommentArticleList = articleService.listArticleByCommentCount(8);
		model.addAttribute("mostCommentArticleList", mostCommentArticleList);
		return "Home/Page/applyLink";
	}

	@RequestMapping(value = "/applyLinkSubmit", method = { RequestMethod.POST })
	@ResponseBody
	public void applyLinkSubmit(Link link) {
		link.setLinkStatus(LinkStatus.HIDDEN.getValue());
		link.setLinkCreateTime(new Date());
		link.setLinkUpdateTime(new Date());
		linkService.insertLink(link);
	}
}
