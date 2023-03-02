package com.wr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : WangRui
 * @date : 2023/3/2
 */

@Controller
public class HelloController {

    @RequestMapping("/index")
    public String sayHello(){
        return "index";
    }
}
