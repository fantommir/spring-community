package com.JKS.community.entity;

import com.JKS.community.repository.CategoryRepository;
import com.JKS.community.repository.CommentRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Collections;

@SpringBootTest
@Transactional
@Rollback(false)
class EntityTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    EntityManager em;

    @Test
    public void test_user() throws Exception {
        // save member
        Member member = createMember(0);
    }


    @Test
    public void test_Category() throws Exception {
        Category rootCategory = createCategory(null, "food");
        Category childCategory = createCategory(rootCategory, "korean");

        Category findChildCategory = categoryRepository.findById(childCategory.getId()).get();
        Assertions.assertThat(findChildCategory.getName()).isEqualTo("korean");
        Assertions.assertThat(findChildCategory.getParent().getName()).isEqualTo("food");
        Assertions.assertThat(findChildCategory.getParent().getParent()).isNull();
        Assertions.assertThat(findChildCategory.getParent().getId()).isEqualTo(rootCategory.getId());
    }

    @Test
    public void test_post() throws Exception {
        Member member = createMember(0);

        Category rootCategory = createCategory(null, "country");
        Category childCategory = createCategory(rootCategory, "china");


        Member findMember = memberRepository.findById(member.getId()).get();
        Category findChinaCategory = categoryRepository.findById(childCategory.getId()).get();


        Post post = Post.builder()
                .title("음식")
                .content("중식")
                .member(findMember)
                .category(findChinaCategory)
                .build();

        postRepository.save(post);
    }

    @Test
    public void test_comment() throws Exception {
        Category rootCategory = createCategory(null, "country");
        Category childCategory = createCategory(rootCategory, "china");
        Category childCategory2 = createCategory(rootCategory, "korea");

        Member member = createMember(0);
        Member member2 = createMember(1);

        Post post = Post.builder()
                .title("중식")
                .content("중식이 노래 좋음")
                .member(member)
                .category(childCategory)
                .build();

        Post post2 = Post.builder()
                .title("한식")
                .content("한식 두식 세식")
                .member(member2)
                .category(childCategory2)
                .build();

        postRepository.save(post);
        postRepository.save(post2);

        Comment comment = Comment.builder()
                .content("몰락써요 따아악")
                .member(member)
                .post(post)
                .build();


        commentRepository.save(comment);
        for (int i=0; i<3; i++) {
            Comment comment2 = Comment.builder()
                    .content("한식 두식 세식 ㅋㅋㅋ 웃기네" + i)
                    .member(member2)
                    .post(post2)
                    .build();
            commentRepository.save(comment2);
        }


        // 포스트에 달린 댓글들 아이디 확인
        Post findPost = postRepository.findByTitle("한식").get();
        Assertions.assertThat(findPost.getComments().size()).isEqualTo(3);
    }


    private Member createMember(int num) {
        Member member = Member.builder()
                .loginId("testloginid" + num)
                .password("testpassword" + num)
                .name("testname" + num)
                .build();

        memberRepository.save(member);
        return member;
    }

    private Category createCategory(Category rootCategory, String name) {
        Category category = Category.builder()
                .name(name)
                .parent(rootCategory)
                .build();

        categoryRepository.save(category);
        return category;
    }
}