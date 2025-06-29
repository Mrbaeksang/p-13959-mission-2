import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Autowired
private CategoryService categoryService;
@Autowired
private QuestionService questionService;

@Test
void t1() {
    // 카테고리 1개라도 없다면 추가
    if (categoryService.getCategoryList().isEmpty()) {
        // 카테고리 직접 추가 메서드 만들거나, SQL로 사전 삽입
    }
    Category category = categoryService.getCategoryList().get(0);

    for (int i = 1; i <= 300; i++) {
        String subject = String.format("테스트 데이터입니다:[%03d]", i);
        String content = "내용무";
        this.questionService.create(subject, content, null, category);
    }
}
