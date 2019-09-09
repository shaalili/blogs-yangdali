package cn.yangdali.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cn.yangdali.mapper.MenuMapper;
import cn.yangdali.pojo.Menu;
import cn.yangdali.service.MenuService;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {


    @Autowired(required = false)
    private MenuMapper menuMapper;

    @Override
    public List<Menu> listMenu() {
        List<Menu> menuList = menuMapper.listMenu();
        return menuList;
    }

    @Override
    @CachePut(value = "default", key = "'menu:'+#menu.menuId")
    public Menu insertMenu(Menu menu) {
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    @CacheEvict(value = "default", key = "'menu:'+#id")
    public void deleteMenu(Integer id) {
        menuMapper.deleteById(id);
    }

    @Override
    @CacheEvict(value = "default", key = "'menu:'+#menu.menuId")
    public void updateMenu(Menu menu) {
        menuMapper.update(menu);
    }

    @Override
    @Cacheable(value = "default", key = "'menu:'+#id")
    public Menu getMenuById(Integer id) {
        return menuMapper.getMenuById(id);
    }
}
