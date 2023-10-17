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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


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
                .orElseThrow(() -> new MemberNotFoundException("Invalid member Id:" + commentFormDto.getMemberId()));
        if (commentFormDto.getParentId() == null) {
            // This is a top-level comment.
            Comment comment = Comment.of(null, 0, post, member, commentFormDto.getContent());
            commentRepository.save(comment);
            return new CommentDto(comment);
        } else {
            // This is a reply to another comment.
            Comment parentComment = commentRepository.findById(commentFormDto.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Invalid parent comment Id:" + commentFormDto.getParentId()));

            int newCommentLevel = parentComment.getLevel() + 1;

            Comment createdComment = Comment.of(parentComment, newCommentLevel, post, member, commentFormDto.getContent());
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

        commentRepository.delete(findComment);
    }

    @Override
    public CommentDto get(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Invalid comment Id:" + commentId));
        return new CommentDto(findComment);
    }

    @Override
    public Page<CommentDto> getListByPost(Long postId, Pageable pageable) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post Id:" + postId));

        return commentRepository.findAllByPostId(postId, pageable).map(CommentDto::new);
    }

    @Override
    public Page<CommentDto> getListByMember(Long memberId, Pageable pageable) {
        return commentRepository.findAllByMemberId(memberId, pageable).map(CommentDto::new);
    }

    @Override
    public Page<CommentDto> getRootCommentsByPost(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Invalid post ID: " + postId);
        }
        return commentRepository.findAllByPostIdAndLevel(postId, 0, pageable).map(CommentDto::new);
    }


    @Override
    public Page<CommentDto> getCommentByParent(Long parentId, Pageable pageable) {
        commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentNotFoundException("Invalid comment ID: " + parentId));

        return commentRepository.findAllByParentId(parentId, pageable).map(CommentDto::new);
    }

    @Override
    public CommentDto react(Long memberId, Long commentId, Boolean isLike) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Invalid member ID: " + memberId));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Invalid comment ID: " + commentId));

        Optional<Reaction> optionalReaction = reactionRepository.findByMemberIdAndCommentId(memberId, commentId);
        if (optionalReaction.isPresent()) {
            Reaction existingReaction = optionalReaction.get();
            if (existingReaction.isLike() == isLike) {
                // Cancel reaction
                reactionRepository.delete(existingReaction);
                comment.setLikeCount(comment.getLikeCount() - (isLike ? 1 : 0));
                comment.setDislikeCount(comment.getDislikeCount() - (isLike ? 0 : 1));
            } else {
                // If the user changes their reaction, update it and adjust the counts.
                existingReaction.update(isLike);
                int increment = isLike ? 1 : -1;
                comment.setLikeCount(comment.getLikeCount() + increment);
                comment.setDislikeCount(comment.getDislikeCount() - increment);
            }
        } else {
            // If there's no existing reaction, create a new one.
            Reaction newReaction = Reaction.of(member,comment, isLike);
            reactionRepository.save(newReaction);
            comment.setLikeCount(comment.getLikeCount()+(isLike ? 1 : 0));
            comment.setDislikeCount(comment.getDislikeCount()+(isLike ? 0 : 1));
        }

        return new CommentDto(comment);
    }

    @Override
    public long countCommentsByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        return memberRepository.countCommentsByMember(member);
    }
}
