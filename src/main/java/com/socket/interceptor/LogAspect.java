package com.socket.interceptor;

import net.sf.json.JSONObject;
import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.*;

@Component
@Aspect
public class LogAspect {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Before(value =("within(com.socket.controller.ImController)"))
    public void before(JoinPoint joinPoint){
        log.info("接口："+LogAspect.queryString(joinPoint));
    }
    @AfterReturning(value =("within(com.socket.controller.ImController)"),returning = "rvt")
    public void after(JoinPoint joinPoint,Object rvt){
        MethodSignature signature=(MethodSignature)joinPoint.getSignature();
        Method method=signature.getMethod();
        if(method.isAnnotationPresent(ResponseBody.class)){
            log.info("{}.{}:返回数据:{}",method.getDeclaringClass().getName(),method.getName(), JSONObject.fromObject(rvt));
        }
    }

    private static String queryString(JoinPoint joinPoint){
        Optional<Object> ol= Arrays.asList(joinPoint.getArgs()).stream().filter(e->e instanceof RequestFacade).findFirst();
        String queryString="";
        if(ol.isPresent()){
            RequestFacade requestFacade= (RequestFacade) ol.get();
            Enumeration<?> pNames = requestFacade.getParameterNames();
            Map<String, String> params = new HashMap<String, String>();
            while (pNames.hasMoreElements()) {
                String pName = (String) pNames.nextElement();
                params.put(pName, requestFacade.getParameter(pName));
                queryString+=queryString.length()>0?"&"+pName+"="+requestFacade.getParameter(pName):pName+"="+requestFacade.getParameter(pName);
            }
            queryString=requestFacade.getRequestURI()+(queryString.length()>0?"?"+queryString:"");
        }
        return queryString;
    }
}
