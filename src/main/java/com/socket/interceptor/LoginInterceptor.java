package com.socket.interceptor;

import com.socket.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = request.getParameter("uuid");
        if(!DataUtil.isNotBlank(uuid)){
            response.sendRedirect("login");
            return false;
        }
        Object user= request.getSession().getAttribute(uuid);
        if(user==null){
            response.sendRedirect("login");
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.info(" --- Request End --- ");
        super.afterCompletion(request, response, handler, ex);
    }
}
