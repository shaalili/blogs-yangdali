package cn.yangdali.manager.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.yangdali.constant.RedisConstant;
import cn.yangdali.pojo.Tag;
import cn.yangdali.redis.JedisClient;
import cn.yangdali.service.ArticleService;
import cn.yangdali.service.TagService;

/**
 * 对于标签操作controller层，
 * 历史版本:
 * 		v:1.00完成基本增删改查
 * 
 * 暂时将redis操作放入该层
 * 待完成：
 * 		1.将同步缓存操作放入MQ队列中
 *
 * @author：yangli	
 * @date:2019年8月16日 下午6:55:29
 * @version 1.01
 */
@Controller
@RequestMapping("/admin/tag")
public class BackTagController {

	@Resource(name = "articleService")
	private ArticleService articleService;

	@Resource(name = "tagService")
	private TagService tagService;
	
	@Resource(name = "jedisClientCluster")
	private JedisClient jedisClient;

	/**
	 * 后台标签列表显示
	 * 
	 * @return
	 */
	@RequestMapping(value = "")
	public ModelAndView index() {
		ModelAndView modelandview = new ModelAndView();
		List<Tag> tagList = tagService.listTagWithCount();
		modelandview.addObject("tagList", tagList);

		modelandview.setViewName("Admin/Tag/index");
		return modelandview;

	}

	/**
	 * 后台添加分类页面显示
	 *
	 * @param tag
	 * @return
	 */
	@RequestMapping(value = "/insertSubmit", method = RequestMethod.POST)
	public String insertTagSubmit(Tag tag) {
		//此处添加分类时，将该分类同时添加到redis缓存中
		tagService.insertTag(tag);
		//在添加数据库时，将其置入redis缓存中
		jedisClient.setListByBean(RedisConstant.TAG_TO_REDIS_THE_KEY, tag);
		return "redirect:/admin/tag";
	}

	/**
	 * 删除标签
	 *
	 * @param id
	 *            标签ID
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}")
	public String deleteTag(@PathVariable("id") Integer id) {
		Integer count = articleService.countArticleByTagId(id);
		if (count == 0) {
			tagService.deleteTag(id);
			//在后台删除标签时，将redis缓存数据一并进行删除
			Tag tag = tagService.getTagById(id);
			jedisClient.deleteListValueByBean(RedisConstant.TAG_TO_REDIS_THE_KEY, tag);
		}
		return "redirect:/admin/tag";
	}

	/**
	 * 编辑标签页面显示
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}")
	public ModelAndView editTagView(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView();

		Tag tag = tagService.getTagById(id);
		modelAndView.addObject("tag", tag);

		List<Tag> tagList = tagService.listTagWithCount();
		modelAndView.addObject("tagList", tagList);

		modelAndView.setViewName("Admin/Tag/edit");
		return modelAndView;
	}

	/**
	 * 编辑标签提交
	 *
	 * @param tag
	 * @return
	 */
	@RequestMapping(value = "/editSubmit", method = RequestMethod.POST)
	public String editTagSubmit(Tag tag) {
		tagService.updateTag(tag);
		//修改标签时，先根据id进行查询，然后再进行删除（此处考虑是否将该操作放入service层进行操作?）
		Tag tagByID = tagService.getTagById(tag.getTagId());
		jedisClient.deleteListValueByBean(RedisConstant.TAG_TO_REDIS_THE_KEY, tagByID);
		jedisClient.setListByBean(RedisConstant.TAG_TO_REDIS_THE_KEY, tag);
		return "redirect:/admin/tag";
	}
}
