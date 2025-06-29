package com.mysite.sbb.category;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 전체 카테고리 리스트 반환
    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    // id로 카테고리 하나만 반환
    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("category not found"));
    }

    public void save(Category category) {
        categoryRepository.save(category);
    }
}
