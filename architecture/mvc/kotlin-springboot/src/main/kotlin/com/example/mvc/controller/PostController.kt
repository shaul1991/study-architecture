package com.example.mvc.controller

import com.example.mvc.dto.CreatePostDto
import com.example.mvc.dto.UpdatePostDto
import com.example.mvc.service.CommentService
import com.example.mvc.service.PostService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * 게시글 웹 MVC 컨트롤러
 *
 * Thymeleaf 템플릿을 사용하여 HTML 뷰를 반환합니다.
 */
@Controller
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
    private val commentService: CommentService
) {
    /**
     * 게시글 목록 페이지
     * GET /posts
     */
    @GetMapping
    fun listPosts(model: Model): String {
        val posts = postService.getAllPosts()
        model.addAttribute("posts", posts)
        return "posts/list"
    }

    /**
     * 게시글 상세 페이지
     * GET /posts/{id}
     */
    @GetMapping("/{id}")
    fun showPost(@PathVariable id: Long, model: Model): String {
        val post = postService.getPostById(id)
        val comments = commentService.getCommentsByPost(id)

        // 조회수 증가
        postService.incrementViewCount(id)

        model.addAttribute("post", post)
        model.addAttribute("comments", comments)
        return "posts/detail"
    }

    /**
     * 게시글 작성 폼 페이지
     * GET /posts/new
     */
    @GetMapping("/new")
    fun newPostForm(model: Model): String {
        model.addAttribute("post", CreatePostDto())
        model.addAttribute("isEdit", false)
        return "posts/form"
    }

    /**
     * 게시글 작성 처리
     * POST /posts
     *
     * 실제 환경에서는 로그인한 사용자 ID를 세션에서 가져와야 하지만
     * 데모를 위해 임시로 authorId=1을 사용합니다.
     */
    @PostMapping
    fun createPost(
        @Valid @ModelAttribute("post") dto: CreatePostDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "posts/form"
        }

        // TODO: 실제로는 로그인한 사용자의 ID를 가져와야 함
        val authorId = 1L
        val post = postService.createPost(authorId, dto.title, dto.content, dto.published)

        redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.")
        return "redirect:/posts/${post.id}"
    }

    /**
     * 게시글 수정 폼 페이지
     * GET /posts/{id}/edit
     */
    @GetMapping("/{id}/edit")
    fun editPostForm(@PathVariable id: Long, model: Model): String {
        val post = postService.getPostById(id)
        val dto = UpdatePostDto(
            title = post.title,
            content = post.content
        )
        model.addAttribute("postId", id)
        model.addAttribute("post", dto)
        model.addAttribute("isEdit", true)
        return "posts/form"
    }

    /**
     * 게시글 수정 처리
     * POST /posts/{id}
     */
    @PostMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @Valid @ModelAttribute("post") dto: UpdatePostDto,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id)
            model.addAttribute("isEdit", true)
            return "posts/form"
        }

        postService.updatePost(id, dto.title, dto.content)
        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.")
        return "redirect:/posts/$id"
    }

    /**
     * 게시글 삭제 처리
     * POST /posts/{id}/delete
     */
    @PostMapping("/{id}/delete")
    fun deletePost(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        postService.deletePost(id)
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.")
        return "redirect:/posts"
    }
}
