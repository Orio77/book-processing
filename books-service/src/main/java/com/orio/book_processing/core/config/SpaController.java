package com.orio.book_processing.core.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/",
            "/{path:^(?!api$|ws$|assets$)[^.]+}",
            "/{path:^(?!api|ws|assets)[^.]+}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
