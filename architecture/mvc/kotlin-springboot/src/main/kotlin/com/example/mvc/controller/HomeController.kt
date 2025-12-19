package com.example.mvc.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * 홈 컨트롤러
 */
@Controller
class HomeController {
    /**
     * 홈 페이지 - 게시글 목록으로 리다이렉트
     */
    @GetMapping("/")
    fun home(): String {
        return "redirect:/posts"
    }
}
