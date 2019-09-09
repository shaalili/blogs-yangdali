package cn.yangdali.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cn.yangdali.mapper.LinkMapper;
import cn.yangdali.pojo.Link;
import cn.yangdali.service.LinkService;

import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {
	
	@Autowired(required = false)
	private LinkMapper linkMapper;
	
	@Override
	public Integer countLink()  {
		return linkMapper.countLink();
	}
	
	@Override
	public List<Link> listLink(Integer status)  {
		return linkMapper.listLink(status);
	}

	@Override
	@CachePut(value = "default", key = "'link:'+#link.linkId")
	public void insertLink(Link link)  {
		linkMapper.insert(link);
	}

	@Override
	@CacheEvict(value = "default", key = "'link:'+#id")
	public void deleteLink(Integer id)  {
		linkMapper.deleteById(id);
	}

	@Override
	@CacheEvict(value = "default", key = "'link:'+#link.linkId")
	public void updateLink(Link link)  {
		linkMapper.update(link);
	}

	@Override
	@Cacheable(value = "default", key = "'link:'+#id")
	public Link getLinkById(Integer id)  {
		return linkMapper.getLinkById(id);
	}

}
