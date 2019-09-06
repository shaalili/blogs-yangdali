package cn.yangdali.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * 拦截用户非法请求。（暂定解决方案，后期改用shiro）
 *
 * @author：yangli	
 * @date:2019年5月23日 下午2:43:04
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
	/**
	 * 在DispatcherServlet完全处理完请求后被调用
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception)
			throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 在业务处理器处理请求执行完成后,生成视图之前执行
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {
		
	}

	/**
	 * 在业务处理器处理请求之前被调用
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		HttpSession httpSession = request.getSession();
		//判断用户登录信息
		Object attribute = httpSession.getAttribute("loginUser");
		if (attribute == null) {
			response.sendRedirect("/login");
			return false;
		}
		return true;
	}
	
}
