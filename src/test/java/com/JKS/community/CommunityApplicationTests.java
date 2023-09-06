package com.JKS.community;

import com.JKS.community.dto.CategoryFormDto;
import com.JKS.community.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class CommunityApplicationTests {

	@Autowired
	CategoryService categoryService;

	@Test
	void 더미데이터() {
		// 나라 카테고리 생성
		CategoryFormDto countryCategory = CategoryFormDto.builder()
				.name("국가")
				.enabled(true)
				.parentId(null)
				.build();
		Long countryId = categoryService.create(countryCategory).getId();

		// 각 나라별 카테고리 생성
		List<String> countries = Arrays.asList("중국", "미국", "일본", "한국", "영국");
		for (String country : countries) {
			CategoryFormDto countryFormDto = CategoryFormDto.builder()
					.name(country)
					.enabled(true)
					.parentId(countryId)
					.build();
			Long createdCountryId = categoryService.create(countryFormDto).getId();

			// 한국의 도시들 추가
			if (country.equals("한국")) {
				List<String> citiesInKorea = Arrays.asList("서울", "부천", "부산", "인천");
				for (String city : citiesInKorea) {
					CategoryFormDto cityCategory = CategoryFormDto.builder()
							.name(city)
							.enabled(true)
							.parentId(createdCountryId)
							.build();
					categoryService.create(cityCategory);
				}
			}

			// 중국의 도시들 추가
			else if (country.equals("중국")) {
				String cityInChina = "베이징";
				CategoryFormDto cityCategory = CategoryFormDto.builder()
						.name(cityInChina)
						.enabled(true)
						.parentId(createdCountryId)
						.build();
				categoryService.create(cityCategory);
			}

			// 일본의 도시들 추가
			else if (country.equals("일본")) {
				List<String> citiesInJapan= Arrays.asList("도쿄","아키하바라");
				for(String city: citiesInJapan){
					CategoryFormDto cityCategory=  CategoryFormDto.builder()
							.name(city).enabled(true).parentId(createdCountryId).build();
					categoryService.create(cityCategory);
				}
			}
		}
	}

}
