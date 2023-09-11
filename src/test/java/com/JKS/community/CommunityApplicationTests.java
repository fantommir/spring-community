package com.JKS.community;

import com.JKS.community.dto.*;
import com.JKS.community.service.CategoryService;
import com.JKS.community.service.CommentService;
import com.JKS.community.service.MemberService;
import com.JKS.community.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest
@Transactional
@Rollback(false)
class CommunityApplicationTests {

	@Autowired CategoryService categoryService;
	@Autowired MemberService memberService;
	@Autowired PostService postService;
	@Autowired CommentService commentService;

	@Test
	void createDummyData() {
		createCategoryDummyData();
		createMemberDummyData();
		createPostDummyData();
	}

	private void createCategoryDummyData() {
		// Main Categories
		Map<String, List<String>> categoriesMap = new HashMap<>();
		categoriesMap.put("국가", Arrays.asList("중국", "미국", "일본", "한국"));
		categoriesMap.put("음식", Arrays.asList("한식", "중식", "일식", "양식"));
		categoriesMap.put("게임", Arrays.asList("FPS","RPG","MMO"));
		categoriesMap.put("스포츠" ,Arrays.asList("축구","야구","농구"));

		List<String> tabs = Arrays.asList("잡담","정보","사진","공지");

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
				Long createdSubCategoryId = categoryService.create(subCategoryFormDto).getId();

				for (String tab : tabs) {
					CategoryFormDto tabAsSubcategoryFormDto = CategoryFormDto.builder()
							.name(tab)
							.enabled(true)
							.parentId(createdSubCategoryId) // The parent is the subcategory.
							.build();
					categoryService.create(tabAsSubcategoryFormDto);
				}
			}
		}
	}

	private void createMemberDummyData() {
		// Dummy member data
		List<MemberFormDto> members = new ArrayList<>();

		for (int i = 1; i <= 30; i++) {
			String loginId = "user" + i;
			String password = "password" + i;
			String name = "User " + i;

			members.add(new MemberFormDto(loginId, password, name));
		}

		for (MemberFormDto member : members) {
			memberService.register(member);
		}
	}

	private void createPostDummyData() {
		// Assume we have a list of members and categories
		List<MemberDto> members = memberService.getList(Pageable.unpaged()).getContent();
		List<CategoryDto> categories = categoryService.getList();

		for (CategoryDto category : categories) {
			for (CategoryDto childCategory : category.getChildren()) {
				for (CategoryDto tab : childCategory.getChildren()) {
					for (MemberDto memberDto : members) {
						// 0개에서 3개 사이의 랜덤한 게시글을 생성
						int randomPostCount = (int) (Math.random() * 3);
						for (int i = 0; i < randomPostCount; i++) {
							PostFormDto postFormDto = PostFormDto.builder()
									.title(category.getName()+"/"+childCategory.getName()+"/"+tab.getName())
									.content("This is a dummy post.")
									.categoryId(tab.getId())
									.memberId(memberDto.getId())
									.build();
							postService.create(postFormDto);
						}
					}
				}
			}
		}
	}


}
