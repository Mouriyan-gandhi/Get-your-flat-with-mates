package com.roommateai.service;

import com.roommateai.dto.PostRequest;
import com.roommateai.dto.PostResponse;
import com.roommateai.dto.CommentRequest;
import com.roommateai.dto.CommentResponse;
import com.roommateai.model.Post;
import com.roommateai.model.PostComment;
import com.roommateai.model.PostLike;
import com.roommateai.model.User;
import com.roommateai.repository.PostRepository;
import com.roommateai.repository.PostCommentRepository;
import com.roommateai.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Community Service
 * Handles community posts, likes, and comments functionality
 */
@Service
public class CommunityService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    /**
     * Create a new post
     */
    public PostResponse createPost(PostRequest request, User user) {
        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(Post.PostCategory.valueOf(request.getCategory()));
        post.setImagesJson(request.getImagesJson());
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setIsPinned(false);
        post.setIsActive(true);

        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost, false);
    }

    /**
     * Get all active posts
     */
    public List<PostResponse> getAllPosts(Long currentUserId) {
        List<Post> posts = postRepository.findRecentPosts();
        return posts.stream()
                .map(post -> convertToResponse(post, isPostLikedByUser(post.getId(), currentUserId)))
                .collect(Collectors.toList());
    }

    /**
     * Get posts by category
     */
    public List<PostResponse> getPostsByCategory(String category, Long currentUserId) {
        Post.PostCategory postCategory = Post.PostCategory.valueOf(category);
        List<Post> posts = postRepository.findRecentPostsByCategory(postCategory);
        return posts.stream()
                .map(post -> convertToResponse(post, isPostLikedByUser(post.getId(), currentUserId)))
                .collect(Collectors.toList());
    }

    /**
     * Get posts by user
     */
    public List<PostResponse> getPostsByUser(Long userId, Long currentUserId) {
        List<Post> posts = postRepository.findPostsByUserOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(post -> convertToResponse(post, isPostLikedByUser(post.getId(), currentUserId)))
                .collect(Collectors.toList());
    }

    /**
     * Get post by ID
     */
    public Optional<PostResponse> getPostById(Long postId, Long currentUserId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.map(post -> convertToResponse(post, isPostLikedByUser(postId, currentUserId)));
    }

    /**
     * Search posts
     */
    public List<PostResponse> searchPosts(String searchTerm, Long currentUserId) {
        List<Post> posts = postRepository.searchPosts(searchTerm);
        return posts.stream()
                .map(post -> convertToResponse(post, isPostLikedByUser(post.getId(), currentUserId)))
                .collect(Collectors.toList());
    }

    /**
     * Like or unlike a post
     */
    public boolean togglePostLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }

        PostLike existingLike = postLikeRepository.findByPostAndUser(postId, userId);
        
        if (existingLike != null) {
            // Unlike the post
            postLikeRepository.delete(existingLike);
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            postRepository.save(post);
            return false; // Post is now unliked
        } else {
            // Like the post
            PostLike newLike = new PostLike(post, post.getUser());
            postLikeRepository.save(newLike);
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
            return true; // Post is now liked
        }
    }

    /**
     * Add a comment to a post
     */
    public CommentResponse addComment(Long postId, CommentRequest request, User user) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new RuntimeException("Post not found");
        }

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent());

        if (request.getParentCommentId() != null) {
            PostComment parentComment = postCommentRepository.findById(request.getParentCommentId()).orElse(null);
            if (parentComment != null) {
                comment.setParentComment(parentComment);
            }
        }

        PostComment savedComment = postCommentRepository.save(comment);

        // Update post comment count
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return convertCommentToResponse(savedComment);
    }

    /**
     * Get comments for a post
     */
    public List<CommentResponse> getCommentsForPost(Long postId) {
        List<PostComment> topLevelComments = postCommentRepository.findTopLevelCommentsByPost(postId);
        return topLevelComments.stream()
                .map(this::convertCommentToResponseWithReplies)
                .collect(Collectors.toList());
    }

    /**
     * Update a post
     */
    public Optional<PostResponse> updatePost(Long postId, PostRequest request, User user) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            
            // Check if user can update this post
            if (!post.getUser().getId().equals(user.getId()) && 
                !user.getRole().equals(User.UserRole.ADMIN)) {
                return Optional.empty();
            }

            // Update fields
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setCategory(Post.PostCategory.valueOf(request.getCategory()));
            post.setImagesJson(request.getImagesJson());

            Post updatedPost = postRepository.save(post);
            return Optional.of(convertToResponse(updatedPost, isPostLikedByUser(postId, user.getId())));
        }
        
        return Optional.empty();
    }

    /**
     * Delete a post
     */
    public boolean deletePost(Long postId, User user) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            
            // Check if user can delete this post
            if (post.getUser().getId().equals(user.getId()) || 
                user.getRole().equals(User.UserRole.ADMIN)) {
                
                // Delete all likes and comments first
                postLikeRepository.deleteAll(postLikeRepository.findByPostId(postId));
                postCommentRepository.deleteAll(postCommentRepository.findByPostId(postId));
                
                postRepository.delete(post);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Pin or unpin a post (Admin only)
     */
    public boolean togglePostPin(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setIsPinned(!post.getIsPinned());
            postRepository.save(post);
            return post.getIsPinned();
        }
        
        return false;
    }

    /**
     * Get community statistics
     */
    public Map<String, Object> getCommunityStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPosts", postRepository.countByIsActive(true));
        stats.put("totalLikes", postLikeRepository.count());
        stats.put("totalComments", postCommentRepository.count());
        
        // Posts by category
        Map<String, Long> postsByCategory = new HashMap<>();
        for (Post.PostCategory category : Post.PostCategory.values()) {
            postsByCategory.put(category.name(), postRepository.countByCategory(category));
        }
        stats.put("postsByCategory", postsByCategory);
        
        return stats;
    }

    /**
     * Get trending posts (high engagement)
     */
    public List<PostResponse> getTrendingPosts(Long currentUserId) {
        List<Post> posts = postRepository.findPostsByEngagement();
        return posts.stream()
                .limit(10) // Top 10 trending posts
                .map(post -> convertToResponse(post, isPostLikedByUser(post.getId(), currentUserId)))
                .collect(Collectors.toList());
    }

    /**
     * Check if post is liked by user
     */
    private boolean isPostLikedByUser(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        return postLikeRepository.hasUserLikedPost(postId, userId);
    }

    /**
     * Convert Post entity to PostResponse DTO
     */
    private PostResponse convertToResponse(Post post, boolean isLikedByCurrentUser) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setUserId(post.getUser().getId());
        response.setUserName(post.getUser().getName());
        response.setUserEmail(post.getUser().getEmail());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCategory(post.getCategory().name());
        response.setImagesJson(post.getImagesJson());
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount());
        response.setIsPinned(post.getIsPinned());
        response.setIsActive(post.getIsActive());
        response.setIsLikedByCurrentUser(isLikedByCurrentUser);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        response.setCreatedAt(post.getCreatedAt().format(formatter));
        response.setUpdatedAt(post.getUpdatedAt().format(formatter));
        
        return response;
    }

    /**
     * Convert PostComment entity to CommentResponse DTO
     */
    private CommentResponse convertCommentToResponse(PostComment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPost().getId());
        response.setUserId(comment.getUser().getId());
        response.setUserName(comment.getUser().getName());
        response.setContent(comment.getContent());
        response.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        response.setCreatedAt(comment.getCreatedAt().format(formatter));
        response.setUpdatedAt(comment.getUpdatedAt().format(formatter));
        
        return response;
    }

    /**
     * Convert PostComment entity to CommentResponse DTO with replies
     */
    private CommentResponse convertCommentToResponseWithReplies(PostComment comment) {
        CommentResponse response = convertCommentToResponse(comment);
        
        // Get replies
        List<PostComment> replies = postCommentRepository.findRepliesByParentComment(comment.getId());
        List<CommentResponse> replyResponses = replies.stream()
                .map(this::convertCommentToResponse)
                .collect(Collectors.toList());
        
        response.setReplies(replyResponses);
        
        return response;
    }
}
