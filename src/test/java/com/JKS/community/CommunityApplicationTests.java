//package com.JKS.community;
//
//import com.JKS.community.dto.*;
//import com.JKS.community.service.CategoryService;
//import com.JKS.community.service.CommentService;
//import com.JKS.community.service.MemberService;
//import com.JKS.community.service.PostService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@SpringBootTest
//@Transactional
//@Rollback(false)
//class CommunityApplicationTests {
//
//	@Autowired CategoryService categoryService;
//	@Autowired MemberService memberService;
//	@Autowired PostService postService;
//	@Autowired CommentService commentService;
//
//	@Test
//	void createDummyData() {
//		createCategoryDummyData();
//		createMemberDummyData();
//		createPostDummyData();
//		createCommentDummyData();
//	}
//
//	private void createCategoryDummyData() {
//		// Main Categories
//		Map<String, List<String>> categoriesMap = new HashMap<>();
//		categoriesMap.put("국가", Arrays.asList("중국", "일본", "한국"));
//		categoriesMap.put("음식", Arrays.asList("한식", "중식", "일식", "양식"));
//		categoriesMap.put("스포츠" , List.of("축구"));
//
//		List<String> tabs = Arrays.asList("잡담","정보","사진","공지");
//
//		for (String mainCategory : categoriesMap.keySet()) {
//			CategoryFormDto mainCategoryFormDto = CategoryFormDto.builder()
//					.name(mainCategory)
//					.enabled(true)
//					.parentId(null)
//					.build();
//			Long createdMainCategoryId = categoryService.create(mainCategoryFormDto).getId();
//
//			List<String> subCategories = categoriesMap.get(mainCategory);
//			for (String subCategory : subCategories) {
//				CategoryFormDto subCategoryFormDto = CategoryFormDto.builder()
//						.name(subCategory)
//						.enabled(true)
//						.parentId(createdMainCategoryId)
//						.build();
//				Long createdSubCategoryId = categoryService.create(subCategoryFormDto).getId();
//
//				for (String tab : tabs) {
//					CategoryFormDto tabAsSubcategoryFormDto = CategoryFormDto.builder()
//							.name(tab)
//							.enabled(true)
//							.parentId(createdSubCategoryId) // The parent is the subcategory.
//							.build();
//					categoryService.create(tabAsSubcategoryFormDto);
//				}
//			}
//		}
//	}
//
//	private void createMemberDummyData() {
//		// Dummy member data
//		List<MemberFormDto> members = new ArrayList<>();
//
//		for (int i = 1; i <= 30; i++) {
//			String email = "user" + i + "@example.com";
//			String password = "password" + i;
//			String confirm_password = "password" + i;
//			String name = "User " + i;
//
//			members.add(new MemberFormDto(email, password, confirm_password, name));
//		}
//
//		for (MemberFormDto member : members) {
//			memberService.register(member);
//		}
//	}
//
//	private void createPostDummyData() {
//		// Assume we have a list of members and categories
//		List<MemberDto> members = memberService.getList(Pageable.unpaged()).getContent();
//		List<CategoryDto> categories = categoryService.getListByDepth(0);
//		for (CategoryDto category : categories) {
//			for (CategoryDto subcategory : category.getChildren()) {
//				for (CategoryDto tab : subcategory.getChildren()) {
//					// 각 tab에 11개의 post를 생성한다. (작성자는 랜덤)
//					for (int i = 1; i <= 11; i++) {
//						MemberDto memberDto = members.get(new Random().nextInt(members.size()));
//						PostFormDto postFormDto = PostFormDto.builder()
//								.title(category.getName()+"/"+subcategory.getName()+"/"+tab.getName())
//								.content(memberDto.getName() + "의 게시글\n" +
//										"카테고리: " + category.getName() + "/" + subcategory.getName() + "/" + tab.getName()
//										+ "\n" + "게시글 제목: " + category.getName()+"/"+subcategory.getName()+"/"+tab.getName())
//								.categoryId(tab.getId())
//								.memberId(memberDto.getId())
//								.build();
//						postService.create(postFormDto);
//					}
//				}
//			}
//		}
//	}
//
//	private void createCommentDummyData() {
//		List<CategoryDto> categories = categoryService.getListByDepth(0);
//		List<MemberDto> members = memberService.getList(Pageable.unpaged()).getContent();
//
//		// 탭에 있는 포스트에 0개부터 11개까지 댓글을 작성한다 (작성자는 랜덤)
//		// 댓글에는 20% 확률로 3 개의 대댓글이 달려있고, 일부 대댓글에는 50% 확률로 1 개의 대대댓글이 달려있다.
//		for (CategoryDto category : categories) {
//			for (CategoryDto subCategory : category.getChildren()) {
//				for (CategoryDto tab : subCategory.getChildren()) {
//					Page<PostDto> postsByCategory = postService.getListByCategory(tab.getId(), Pageable.unpaged());
//					for (PostDto postDto : postsByCategory) {
//						// 11개의 댓글
//						for (int i = 1; i <= 11; i++) {
//							MemberDto memberDto = members.get(new Random().nextInt(members.size()));
//							CommentFormDto commentFormDto = CommentFormDto.builder()
//									.postId(postDto.getId())
//									.memberId(memberDto.getId())
//									.parentId(null)
//									.content(memberDto.getName() + "의 댓글\n")
//									.build();
//							CommentDto commentDto = commentService.create(commentFormDto);
//
//							if (new Random().nextInt(100) < 20) {
//								CommentFormDto replyFormDto = CommentFormDto.builder()
//										.postId(postDto.getId())
//										.memberId(members.get(new Random().nextInt(members.size())).getId())
//										.parentId(commentDto.getId())
//										.content(memberDto.getName() + "의 대댓글\n")
//										.build();
//								CommentDto replyDto = commentService.create(replyFormDto);
//
//								if (new Random().nextInt(100) < 50) {
//									CommentFormDto replyReplyFormDto = CommentFormDto.builder()
//											.postId(postDto.getId())
//											.memberId(members.get(new Random().nextInt(members.size())).getId())
//											.parentId(replyDto.getId())
//											.content(memberDto.getName() + "의 대대댓글\n")
//											.build();
//									commentService.create(replyReplyFormDto);
//								}
//							}
//
//
//						}
//
//
//					}
//				}
//			}
//		}
//
//	}
//}
