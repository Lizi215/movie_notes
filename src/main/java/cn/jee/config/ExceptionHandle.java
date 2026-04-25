package cn.jee.config;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(IOException.class)
    public String exception(IOException e) {
        return e.getMessage();
    }
}
