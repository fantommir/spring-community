package com.JKS.community;

import com.JKS.community.dto.CategoryFormDto;
import com.JKS.community.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
@Rollback(false)
class CommunityApplicationTests {

	@Autowired
	CategoryService categoryService;

	@Test
	void createDummyData() {
		// Main Categories
		Map<String, List<String>> categoriesMap = new HashMap<>();
		categoriesMap.put("국가", Arrays.asList("중국", "미국", "일본", "한국", "영국"));
		categoriesMap.put("음식", Arrays.asList("한식", "중식", "일식", "양식"));
		categoriesMap.put("게임", Arrays.asList("FPS", "RPG","MMO"));
		categoriesMap.put("스포츠" ,Arrays.asList("축구","야구","농구"));

		for (String mainCategory : categoriesMap.keySet()) {
			CategoryFormDto mainCategoryFormDto = CategoryFormDto.builder()
					.name(mainCategory)
					.enabled(true)
					.parentId(null)
					.build();
			Long createdMainCategoryId = categoryService.create(mainCategoryFormDto).getId();

			List<String> subCategories = categoriesMap.get(mainCategory);
			for (String subCategory : subCategories) {
				CategoryFormDto subCategoryFormDto = CategoryFormDto.builder()
						.name(subCategory)
						.enabled(true)
						.parentId(createdMainCategoryId)
						.build();
				categoryService.create(subCategoryFormDto);
			}
		}
	}


}
