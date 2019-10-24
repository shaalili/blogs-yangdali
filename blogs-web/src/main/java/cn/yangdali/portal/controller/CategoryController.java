package cn.yangdali.portal.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.yangdali.enums.ArticleStatus;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CategoryService;
import cn.yangdali.service.TagService;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private TagService tagService;

	/**
	 * 根据分类查询文章
	 *
	 * @param cateId 分类ID
	 * @return 模板
	 */
	@RequestMapping("/category/{cateId}")
	public String getArticleListByCategory(@PathVariable("cateId") Integer cateId,
			@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, Model model) {

		// 该分类信息
		var category = categoryService.getCategoryById(cateId);
		if (category == null) {
			return "redirect:/404";
		}
		model.addAttribute("category", category);

		// 文章列表
		HashMap<String, Object> criteria = new HashMap<>(2);
		criteria.put("categoryId", cateId);
		criteria.put("status", ArticleStatus.PUBLISH.getValue());
		var articlePageInfo = articleService.pageArticle(pageIndex, pageSize, criteria);
		model.addAttribute("pageInfo", articlePageInfo);

		// 侧边栏
		// 标签列表显示
		var allTagList = tagService.listTag();
		model.addAttribute("allTagList", allTagList);
		// 获得随机文章
		var randomArticleList = articleService.listRandomArticle(8);
		model.addAttribute("randomArticleList", randomArticleList);
		// 获得热评文章
		var mostCommentArticleList = articleService.listArticleByCommentCount(8);
		model.addAttribute("mostCommentArticleList", mostCommentArticleList);
		model.addAttribute("pageUrlPrefix", "/category/" + pageIndex + "?pageIndex");
		return "Home/Page/articleListByCategory";
	}

}
