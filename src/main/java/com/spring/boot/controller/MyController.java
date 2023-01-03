package com.spring.boot.controller;

import com.spring.boot.service.MyService;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



/**
 * Controller层
 *
 * @author 代码的路
 * @date 2023/1/3
 */


@Controller
@ResponseBody
public class MyController {

    @Autowired
    MyService myService;

    @Autowired
    private PrometheusMeterRegistry prometheusMeterRegistry;

    // 提供 prometheus 接口
    @RequestMapping(value = "/metric/custom", method = RequestMethod.GET,
            produces = "text/plain; charset=utf-8")
    public Object metric() {
        return prometheusMeterRegistry.scrape();
    }
}
