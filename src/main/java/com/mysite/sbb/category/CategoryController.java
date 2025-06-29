package com.mysite.sbb.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 목록 출력 (예: 관리용, 단독 카테고리 관리 뷰 만들 때 사용)
    @GetMapping("/list")
    public String getCategoryList(Model model) {
        List<Category> categoryList = categoryService.getCategoryList();
        model.addAttribute("categoryList", categoryList);
        return "category_list"; // templates/category_list.html (필요하면 만들어서 사용)
    }

    // 개별 카테고리 단일 조회 (관리/테스트/확장용)
    @GetMapping("/{id}")
    public String getCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategory(id);
        model.addAttribute("category", category);
        return "category_detail"; // templates/category_detail.html (필요하면 만들어서 사용)
    }

    // 추가: 카테고리 등록/수정/삭제 기능은 관리자가 필요할 때 확장!
}
