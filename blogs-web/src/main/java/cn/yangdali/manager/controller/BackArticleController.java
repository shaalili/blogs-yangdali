package cn.yangdali.manager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;

import cn.yangdali.dto.ArticleParam;
import cn.yangdali.pojo.Article;
import cn.yangdali.pojo.Category;
import cn.yangdali.pojo.Tag;
import cn.yangdali.pojo.User;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.CategoryService;
import cn.yangdali.service.TagService;

@Controller
@RequestMapping("/admin/article")
public class BackArticleController {
	@Resource(name = "articleService")
	private ArticleService articleService;

	@Resource(name = "tagService")
	private TagService tagService;

	@Resource(name = "categoryService")
	private CategoryService categoryService;

	/**
	 * 后台文章列表显示
	 *
	 * @return modelAndView
	 */
	@RequestMapping(value = "")
	public String index(@RequestParam(required = false, defaultValue = "1") Integer pageIndex,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false) String status, Model model) {
		HashMap<String, Object> criteria = new HashMap<>(1);
		if (status == null) {
			model.addAttribute("pageUrlPrefix", "/admin/article?pageIndex");
		} else {
			criteria.put("status", status);
			model.addAttribute("pageUrlPrefix", "/admin/article?status=" + status + "&pageIndex");
		}
		PageInfo<Article> articlePageInfo = articleService.pageArticle(pageIndex, pageSize, criteria);
		// 查询出文章的所属分类

		model.addAttribute("pageInfo", articlePageInfo);
		return "Admin/Article/index";
	}

	/**
	 * 后台添加文章页面显示
	 *
	 * @return
	 */
	@RequestMapping(value = "/insert")
	public String insertArticleView(Model model) {
		List<Category> categoryList = categoryService.listCategory();
		List<Tag> tagList = tagService.listTag();
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("tagList", tagList);
		return "Admin/Article/insert";
	}

	/**
	 * 后台添加文章提交操作
	 *
	 * @param articleParam
	 * @return
	 */
	@RequestMapping(value = "/insertSubmit", method = RequestMethod.POST)
	public String insertArticleSubmit(HttpSession session, ArticleParam articleParam) {
		Article article = new Article();
		// 用户ID
		User user = (User) session.getAttribute("user");
		if (user != null) {
			article.setArticleUserId(user.getUserId());
		}
		article.setArticleTitle(articleParam.getArticleTitle());
		article.setArticleContent(articleParam.getArticleContent());
		article.setArticleStatus(articleParam.getArticleStatus());
		// 填充分类
		List<Category> categoryList = new ArrayList<>();
		if (articleParam.getArticleChildCategoryId() != null) {
			categoryList.add(new Category(articleParam.getArticleParentCategoryId()));
		}
		if (articleParam.getArticleChildCategoryId() != null) {
			categoryList.add(new Category(articleParam.getArticleChildCategoryId()));
		}
		article.setCategoryList(categoryList);
		// 填充标签
		List<Tag> tagList = new ArrayList<>();
		if (articleParam.getArticleTagIds() != null) {
			for (int i = 0; i < articleParam.getArticleTagIds().size(); i++) {
				Tag tag = new Tag(articleParam.getArticleTagIds().get(i));
				tagList.add(tag);
			}
		}
		article.setTagList(tagList);

		articleService.insertArticle(article);
		return "redirect:/admin/article";
	}

	/**
	 * 删除文章
	 *
	 * @param id
	 *            文章ID
	 */
	@RequestMapping(value = "/delete/{id}")
	public void deleteArticle(@PathVariable("id") Integer id) {
		articleService.deleteArticle(id);
	}

	/**
	 * 编辑文章页面显示
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}")
	public ModelAndView editArticleView(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView();

		Article article = articleService.getArticleByStatusAndId(null, id);
		modelAndView.addObject("article", article);

		List<Category> categoryList = categoryService.listCategory();
		modelAndView.addObject("categoryList", categoryList);

		List<Tag> tagList = tagService.listTag();
		modelAndView.addObject("tagList", tagList);

		modelAndView.setViewName("Admin/Article/edit");
		return modelAndView;
	}

	/**
	 * 编辑文章提交
	 *
	 * @param articleParam
	 * @return
	 */
	@RequestMapping(value = "/editSubmit", method = RequestMethod.POST)
	public String editArticleSubmit(ArticleParam articleParam) {
		Article article = new Article();
		article.setArticleId(articleParam.getArticleId());
		article.setArticleTitle(articleParam.getArticleTitle());
		article.setArticleContent(articleParam.getArticleContent());
		article.setArticleStatus(articleParam.getArticleStatus());
		// 填充分类
		List<Category> categoryList = new ArrayList<>();
		if (articleParam.getArticleChildCategoryId() != null) {
			categoryList.add(new Category(articleParam.getArticleParentCategoryId()));
		}
		if (articleParam.getArticleChildCategoryId() != null) {
			categoryList.add(new Category(articleParam.getArticleChildCategoryId()));
		}
		article.setCategoryList(categoryList);
		// 填充标签
		List<Tag> tagList = new ArrayList<>();
		if (articleParam.getArticleTagIds() != null) {
			for (int i = 0; i < articleParam.getArticleTagIds().size(); i++) {
				Tag tag = new Tag(articleParam.getArticleTagIds().get(i));
				tagList.add(tag);
			}
		}
		article.setTagList(tagList);
		articleService.updateArticleDetail(article);
		return "redirect:/admin/article";
	}

}
