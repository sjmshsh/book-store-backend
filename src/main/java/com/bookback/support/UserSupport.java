package com.bookback.support;


import com.bookback.exception.ConditionException;
import com.bookback.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {
    public static int getCurrentUserId(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 获取token
        String token = requestAttributes.getRequest().getHeader("token");
        int userId = TokenUtil.verifyToken(token);
        if (userId < 0){
            throw  new ConditionException("非法用户");
        }
        return userId;
    }
}
