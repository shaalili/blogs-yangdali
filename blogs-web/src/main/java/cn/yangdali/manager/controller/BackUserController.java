package cn.yangdali.manager.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.yangdali.pojo.User;
import cn.yangdali.service.UserService;

/**
 * 后台用户controller层
 *
 * @author：yangli	
 * @date:2019年10月22日 上午11:24:45
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/user")
public class BackUserController {

	@Resource(name = "userService")
	private UserService userService;

	/**
	 * 后台用户列表显示
	 *
	 * @return
	 */
	@RequestMapping(value = "")
	public ModelAndView userList() {
		ModelAndView modelandview = new ModelAndView();
		
		List<User> userList = userService.listUser();
		modelandview.addObject("userList", userList);

		modelandview.setViewName("Admin/User/index");
		return modelandview;

	}

	/**
	 * 后台添加用户页面显示
	 *
	 * @return
	 */
	@RequestMapping(value = "/insert")
	public ModelAndView insertUserView() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("Admin/User/insert");
		return modelAndView;
	}

	/**
	 * 检查用户名是否存在
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/checkUserName", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkUserName(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>(4);
		String username = request.getParameter("username");
		User user = userService.getUserByName(username);
		int id = Integer.valueOf(request.getParameter("id"));
		// 用户名已存在,但不是当前用户(编辑用户的时候，不提示)
		if (user != null) {
			if (user.getUserId() != id) {
				map.put("code", 1);
				map.put("msg", "用户名已存在！");
			}
		} else {
			map.put("code", 0);
			map.put("msg", "");
		}
		return map;
	}

	/**
	 * 检查Email是否存在
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/checkUserEmail", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkUserEmail(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>(4);
		String email = request.getParameter("email");
		User user = userService.getUserByEmail(email);
		int id = Integer.valueOf(request.getParameter("id"));
		// 用户名已存在,但不是当前用户(编辑用户的时候，不提示)
		if (user != null) {
			if (user.getUserId() != id) {
				map.put("code", 1);
				map.put("msg", "电子邮箱已存在！");
			}
		} else {
			map.put("code", 0);
			map.put("msg", "");
		}
		return map;
	}

	/**
	 * 后台添加用户页面提交
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/insertSubmit", method = RequestMethod.POST)
	public String insertUserSubmit(User user) {
		User user2 = userService.getUserByName(user.getUserName());
		User user3 = userService.getUserByEmail(user.getUserEmail());
		if (user2 == null && user3 == null) {
			user.setUserRegisterTime(new Date());
			user.setUserStatus(1);
			userService.insertUser(user);
		}
		return "redirect:/admin/user";
	}

	/**
	 * 删除用户
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id) {
		userService.deleteUser(id);
		return "redirect:/admin/user";
	}

	/**
	 * 编辑用户页面显示
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}")
	public ModelAndView editUserView(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView();

		User user = userService.getUserById(id);
		modelAndView.addObject("user", user);

		modelAndView.setViewName("Admin/User/edit");
		return modelAndView;
	}

	/**
	 * 编辑用户提交
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/editSubmit", method = RequestMethod.POST)
	public String editUserSubmit(User user) {
		userService.updateUser(user);
		return "redirect:/admin/user";
	}

	/**
	 * 基本信息页面显示
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/profile/{id}")
	public ModelAndView userProfileView(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView();

		User user = userService.getUserById(id);
		modelAndView.addObject("user", user);

		modelAndView.setViewName("Admin/User/profile");
		return modelAndView;
	}
}
