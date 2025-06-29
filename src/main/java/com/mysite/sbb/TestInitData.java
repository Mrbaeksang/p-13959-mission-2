package com.mysite.sbb;

import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class TestInitData {

    @Autowired
    @Lazy
    private TestInitData self;

    private final CategoryService categoryService;
    private final QuestionService questionService;

    @Bean
    ApplicationRunner testInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    void work1() {
        if (questionService.getList().size() > 0) return;

        // 카테고리 없으면 하나 추가 (예시)
        if (categoryService.getCategoryList().isEmpty()) {
            // 예시: "자유게시판" 카테고리 하나 생성
            Category category = new Category();
            category.setName("자유게시판");
            categoryService.save(category); // save 메소드 필요
        }
        Category category = categoryService.getCategoryList().get(0); // 첫 번째 카테고리 사용

        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 질문입니다 : [%03d]", i);
            String content = "테스트 내용입니다.";
            questionService.create(subject, content, null, category);
        }
    }
}
