package com.JKS.community.entity;

import com.JKS.community.repository.CategoryRepository;
import com.JKS.community.repository.CommentRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
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


    Member member1, member2;
    Category rootCategory, chinaCategory, koreaCategory;
    @BeforeEach
    public void setUp() {
        this.member1 = createMember(0);
        this.member2 = createMember(1);

        this.rootCategory = createCategory(null, "country");
        this.chinaCategory = createCategory(rootCategory, "china");
        this.koreaCategory = createCategory(rootCategory, "korea");
    }

    @Test
    public void test_Category() throws Exception {
        Assertions.assertThat(chinaCategory.getName()).isEqualTo("china");
        Assertions.assertThat(chinaCategory.getParent().getName()).isEqualTo("country");
        Assertions.assertThat(chinaCategory.getParent().getParent()).isNull();
        Assertions.assertThat(chinaCategory.getParent().getId()).isEqualTo(rootCategory.getId());
    }

    @Test
    public void test_post() throws Exception {
        Post post = Post.of("음식", "중식", member1, chinaCategory);

        postRepository.save(post);
    }

    @Test
    public void test_comment() throws Exception {
        Post post = Post.of("중식", "중식이 노래 좋음", member1, chinaCategory);
        postRepository.save(post);

        for (int i=0; i<3; i++) {
            Comment comment2 = Comment.of(null, i ,post , member2 ,"한식 두식 세식 ㅋㅋㅋ 웃기네" + i);
            commentRepository.save(comment2);
        }

        Post findPost=postRepository.findByTitle(post.getTitle())
                .orElseThrow(() -> new IllegalArgumentException ("Invalid post title:" + "한식"));
        Assertions.assertThat(findPost.getComments().size()).isEqualTo(3);
    }

    private Member createMember(int num) {
        Member member = Member.of("test" + num + "@test.com", "user" + num, "testPassword" + num);

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