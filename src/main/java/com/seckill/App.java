package com.seckill;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"com.seckill"})
@MapperScan("com.seckill.dao")
public class App
{
    public static void main( String[] args ) {
        SpringApplication.run(App.class,args);
    }
}
