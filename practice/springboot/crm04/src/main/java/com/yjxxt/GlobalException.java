package com.yjxxt;


import com.alibaba.fastjson.JSON;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.exception.NoLoginException;
import com.yjxxt.exception.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalException implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest req , HttpServletResponse resp, Object handler, Exception e) {
        //未登录异常
        if(e instanceof NoLoginException){
            //返回登录页面
            ModelAndView mav = new ModelAndView("redirect:/index");
            //mav.setViewName("redirect:/index");
            return mav;
        }
        //附默认值
        ModelAndView mav = new ModelAndView();
        //返回默认视图
        mav.setViewName("");
        mav.addObject("code",400);
        mav.addObject("msg","系统异常,请稍后访问。。。");
        e.printStackTrace();
        //判断
        if(handler instanceof HandlerMethod){
            HandlerMethod hm= (HandlerMethod) handler;
            ResponseBody responseBody=hm.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if(responseBody==null){
                if(e instanceof ParamsException){
                    ParamsException pe = (ParamsException) e;
                    mav.addObject("code",pe.getCode());
                    mav.addObject("msg",pe.getMsg());
                    return mav;
                }
            }else {
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(400);
                resultInfo.setMsg("系统异常111");
                e.printStackTrace();
                if(e instanceof ParamsException){
                    ParamsException pe = (ParamsException) e;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                resp.reset();
                resp.setContentType("application/json;charset=utf-8");
                //输出
                PrintWriter out=null;
                try {
                    out=resp.getWriter();
                    //json--->string
                    out.write(JSON.toJSONString(resultInfo));

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }finally {
                    if(out!=null){
                        out.flush();
                        out.close();
                    }
                }
            }
        }
        return mav;
    }
}
