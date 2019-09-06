package cn.yangdali.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.yangdali.mapper.ArticleTagRefMapper;
import cn.yangdali.mapper.TagMapper;
import cn.yangdali.pojo.Tag;
import cn.yangdali.service.TagService;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

	private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

	@Autowired(required = false)
	private TagMapper tagMapper;

	@Autowired(required = false)
	private ArticleTagRefMapper articleTagRefMapper;

	@Override
	public Integer countTag() {
		return tagMapper.countTag();
	}

	@Override
	public List<Tag> listTag() {
		List<Tag> tagList = null;
		try {
			tagList = tagMapper.listTag();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获得所有标签失败, cause:{}", e);
		}
		return tagList;
	}

	@Override
	public List<Tag> listTagWithCount() {
		List<Tag> tagList = null;
		try {
			tagList = tagMapper.listTag();
			for (int i = 0; i < tagList.size(); i++) {
				Integer count = articleTagRefMapper.countArticleByTagId(tagList.get(i).getTagId());
				tagList.get(i).setArticleCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获得所有标签失败, cause:{}", e);
		}
		return tagList;
	}

	@Override
	@Cacheable(value = "default", key = "'tag:'+#id")
	public Tag getTagById(Integer id) {
		Tag tag = null;
		try {
			tag = tagMapper.getTagById(id);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("根据ID获得标签失败, id:{}, cause:{}", id, e);
		}
		return tag;
	}

	@Override
	@CachePut(value = "default", key = "'tag:'+#result.tagId")
	public Tag insertTag(Tag tag) {
		try {
			tagMapper.insert(tag);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("添加标签失败, tag:{}, cause:{}", tag, e);
		}
		return tag;
	}

	@Override
	@CacheEvict(value = "default", key = "'tag:'+#tag.tagId")
	public void updateTag(Tag tag) {
		try {
			tagMapper.update(tag);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("更新标签失败, tag:{}, cause:{}", tag, e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "default", key = "'tag:'+#id")
	public void deleteTag(Integer id) {
		try {
			tagMapper.deleteById(id);
			articleTagRefMapper.deleteByTagId(id);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("删除标签失败, id:{}, cause:{}", id, e);
		}

	}

	@Override
	public Tag getTagByName(String name) {
		Tag tag = null;
		try {
			tag = tagMapper.getTagByName(name);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("根据名称获得标签, name:{}, cause:{}", name, e);
		}
		return tag;
	}

	@Override
	public List<Tag> listTagByArticleId(Integer articleId) {
		List<Tag> tagList = null;
		try {
			tagList = articleTagRefMapper.listTagByArticleId(articleId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("根据文章ID获得标签失败，articleId:{}, cause:{}", articleId, e);
		}
		return tagList;
	}

}
