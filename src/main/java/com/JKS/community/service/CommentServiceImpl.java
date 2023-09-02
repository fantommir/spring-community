package com.JKS.community.service;

import com.JKS.community.dto.CommentDto;
import com.JKS.community.dto.CommentFormDto;
import com.JKS.community.entity.Comment;
import com.JKS.community.entity.Member;
import com.JKS.community.entity.Post;
import com.JKS.community.entity.Reaction;
import com.JKS.community.exception.CommentNotFoundException;
import com.JKS.community.exception.MemberNotFoundException;
import com.JKS.community.exception.PostNotFoundException;
import com.JKS.community.repository.CommentRepository;
import com.JKS.community.repository.MemberRepository;
import com.JKS.community.repository.PostRepository;
import com.JKS.community.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReactionRepository reactionRepository;

    @Override
    public CommentDto create(CommentFormDto commentFormDto) {
        Post post = postRepository.findById(commentFormDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + commentFormDto.getPostId()));
        Member member = memberRepository.findById(commentFormDto.getMemberId())
                .orElseThrow(() -> new CommentNotFoundException("Invalid member Id:" + commentFormDto.getMemberId()));
        if (commentFormDto.getParentId() == null) {
            // This is a top-level comment.
            Comment comment = Comment.of(null, 0, post, member, commentFormDto.getContent());
            commentRepository.save(comment);
            return new CommentDto(comment);
        } else {
            // This is a reply to another comment.
            Comment parentComment = commentRepository.findById(commentFormDto.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Invalid parent comment Id:" + commentFormDto.getParentId()));

            Long parentId = parentComment.getParentId();
            int newCommentLevel = parentComment.getLevel() + 1;

            Comment createdComment = Comment.of(parentId, newCommentLevel, post, member, commentFormDto.getContent());
            commentRepository.save(createdComment);
            return new CommentDto(createdComment);
        }
    }

    @Override
    public CommentDto update(Long commentId, String content) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Invalid comment Id:" + commentId));

        findComment.update(content);
        return new CommentDto(findComment);
    }

    @Override
    public void delete(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Invalid comment Id:" + commentId));

        findComment.delete();
    }

    @Override
    public Page<CommentDto> getList(Long postId, Pageable pageable) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + postId));

        Page<Comment> commentPage = commentRepository.findByPostIdAndEnabledTrue(postId, pageable);
        List<CommentDto> commentDtoList = commentPage.getContent().stream()
                .map(CommentDto::new)
                .toList();
        return new PageImpl<>(commentDtoList, pageable, commentPage.getTotalElements());
    }

    @Override
    public CommentDto react(Long memberId, Long commentId, Boolean isLike) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Invalid member ID: " + memberId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Invalid comment ID: " + commentId));

        // Check if the user has already reacted to the post.
        Optional<Reaction> optionalReaction = reactionRepository.findByMemberIdAndCommentId(memberId, commentId);

        if (optionalReaction.isEmpty()) {
            // If there's no existing reaction, create a new one.
            Reaction newReaction = Reaction.of(member, comment, isLike);
            reactionRepository.save(newReaction);

            if (isLike) {
                comment.setLikeCount(comment.getLikeCount() + 1);
            } else {
                comment.setDislikeCount(comment.getDislikeCount() + 1);
            }
        } else {
            // If there's an existing reaction and the type is different,
            Reaction existingReaction = optionalReaction.get();

            if (existingReaction.isLike() != isLike) {
                existingReaction.update(isLike);

                if (isLike) {
                    comment.setLikeCount(comment.getLikeCount() + 1);
                    comment.setDislikeCount(comment.getDislikeCount() - 1);
                } else {
                    comment.setDislikeCount(comment.getDislikeCount() + 1);
                    comment.setLikeCount(comment.getLikeCount() - 1);
                }
            }
        }

        return new CommentDto(comment);
    }

    @Override
    public Page<CommentDto> getListByMember(Long memberId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findAllByMemberIdAndEnabledTrue(memberId, pageable);
        List<CommentDto> commentDtoList = commentPage.getContent().stream()
                .map(CommentDto::new)
                .toList();

        return new PageImpl<>(commentDtoList, commentPage.getPageable(), commentPage.getTotalElements());
    }
}
