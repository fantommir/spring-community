package com.JKS.community.service;

import com.JKS.community.dto.CommentCreateDto;
import com.JKS.community.entity.Comment;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.repository.CommentRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import com.JKS.community.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private MemberRepository memberRepository;
    private ReactionRepository reactionRepository;

    @Override
    public Comment create(CommentCreateDto commentCreateDto) {
        Post post = postRepository.findById(commentCreateDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + commentCreateDto.getPostId()));
        Member member = memberRepository.findById(commentCreateDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + commentCreateDto.getMemberId()));
        if (commentCreateDto.getParentId() == null) {
            // This is a top-level comment.
            return Comment.of(UUID.randomUUID().toString(), 0, post, member, commentCreateDto.getContent());
        } else {
            // This is a reply to another comment.
            Comment parentComment = commentRepository.findById(commentCreateDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment Id:" + commentCreateDto.getParentId()));

            String newCommentRef = parentComment.getRef();
            int newCommentLevel = parentComment.getLevel() + 1;

            return Comment.of(newCommentRef, newCommentLevel, post, member, commentCreateDto.getContent());
        }
    }

    @Override
    public Comment update(Long commentId, String content) {
        return null;
    }

    @Override
    public void delete(Long commentId) {

    }

    @Override
    public void getList(Long postId) {

    }

    @Override
    public void react(Long commentId, Long memberId, boolean isLike) {

    }

    @Override
    public void getListByMember(Long memberId) {

    }

    @Override
    public void getTop5(Long postId) {

    }
}
