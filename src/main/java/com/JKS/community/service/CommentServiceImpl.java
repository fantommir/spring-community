package com.JKS.community.service;

import com.JKS.community.dto.CommentCreateDto;
import com.JKS.community.dto.CommentDto;
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
    public CommentDto create(CommentCreateDto commentCreateDto) {
        Post post = postRepository.findById(commentCreateDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + commentCreateDto.getPostId()));
        Member member = memberRepository.findById(commentCreateDto.getMemberId())
                .orElseThrow(() -> new CommentNotFoundException("Invalid member Id:" + commentCreateDto.getMemberId()));
        if (commentCreateDto.getParentId() == null) {
            // This is a top-level comment.
            Comment comment = Comment.of(UUID.randomUUID().toString(), 0, post, member, commentCreateDto.getContent());
            commentRepository.save(comment);
            return new CommentDto(comment);
        } else {
            // This is a reply to another comment.
            Comment parentComment = commentRepository.findById(commentCreateDto.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Invalid parent comment Id:" + commentCreateDto.getParentId()));

            String newCommentRef = parentComment.getRef();
            int newCommentLevel = parentComment.getLevel() + 1;

            Comment createdComment = Comment.of(newCommentRef, newCommentLevel, post, member, commentCreateDto.getContent());
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
    public Page<CommentDto> getListByMember(Long memberId) {
        Page<Comment> commentPage = commentRepository.findAllByMemberIdAndEnabledTrue(memberId);
        List<CommentDto> commentDtoList = commentPage.getContent().stream()
                .map(CommentDto::new)
                .toList();

        return new PageImpl<>(commentDtoList, commentPage.getPageable(), commentPage.getTotalElements());
    }
}
