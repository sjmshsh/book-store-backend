package com.bookback.handler;

import com.bookback.exception.ConditionException;
import com.bookback.utils.JsonResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e) {
        String errMessage = e.getMessage();
        if (e instanceof ConditionException) {
            String errCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errCode, errMessage);
        } else {
            return new JsonResponse<>("500", errMessage);
        }
    }

}
