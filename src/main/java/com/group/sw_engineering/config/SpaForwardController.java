package com.group.sw_engineering.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards direct navigations/refreshes on client-side routes (e.g. /items/5)
 * to index.html so React Router can take over, instead of a 404 Whitelabel
 * page. /api/** and static assets are resolved before this ever applies.
 */
@Controller
public class SpaForwardController {

    @GetMapping({"/items", "/items/**", "/about"})
    public String forward() {
        return "forward:/index.html";
    }
}
