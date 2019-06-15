package com.socket.interceptor;

import com.socket.util.DataUtil;
import com.socket.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid=request.getParameter("uuid");
        Object object=null;
        if(DataUtil.isNotBlank(uuid)){
            object=request.getSession().getAttribute(uuid);
        }else{
            object=request.getSession().getAttribute("uuid");
        }
        if(object==null){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(Result.putValue(1, "请登录"));
        }
        return true;
    }
}
