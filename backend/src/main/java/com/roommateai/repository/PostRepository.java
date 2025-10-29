package com.roommateai.repository;

import com.roommateai.model.Post;
import com.roommateai.model.PostComment;
import com.roommateai.model.PostLike;
import com.roommateai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Post Repository
 * Data access layer for Post entity
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * Find posts by user
     */
    List<Post> findByUserId(Long userId);
    
    /**
     * Find posts by user
     */
    List<Post> findByUser(User user);
    
    /**
     * Find posts by category
     */
    List<Post> findByCategory(Post.PostCategory category);
    
    /**
     * Find active posts
     */
    List<Post> findByIsActive(Boolean isActive);
    
    /**
     * Find pinned posts
     */
    List<Post> findByIsPinned(Boolean isPinned);
    
    /**
     * Find posts by category and active status
     */
    List<Post> findByCategoryAndIsActive(Post.PostCategory category, Boolean isActive);
    
    /**
     * Find recent posts
     */
    @Query("SELECT p FROM Post p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts();
    
    /**
     * Find recent posts by category
     */
    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Post> findRecentPostsByCategory(@Param("category") Post.PostCategory category);
    
    /**
     * Find posts by user with pagination
     */
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findPostsByUserOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * Find posts with high engagement
     */
    @Query("SELECT p FROM Post p WHERE p.isActive = true ORDER BY (p.likesCount + p.commentsCount) DESC")
    List<Post> findPostsByEngagement();
    
    /**
     * Search posts by title or content
     */
    @Query("SELECT p FROM Post p WHERE " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "p.isActive = true ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("searchTerm") String searchTerm);
    
    /**
     * Count posts by user
     */
    Long countByUserId(Long userId);
    
    /**
     * Count posts by category
     */
    Long countByCategory(Post.PostCategory category);
    
    /**
     * Count active posts
     */
    Long countByIsActive(Boolean isActive);
}

/**
 * Post Like Repository
 */
@Repository
interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    /**
     * Find likes by post
     */
    List<PostLike> findByPostId(Long postId);
    
    /**
     * Find likes by user
     */
    List<PostLike> findByUserId(Long userId);
    
    /**
     * Check if user has liked post
     */
    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    boolean hasUserLikedPost(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * Find like by post and user
     */
    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.user.id = :userId")
    PostLike findByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * Count likes for a post
     */
    Long countByPostId(Long postId);
    
    /**
     * Count likes by user
     */
    Long countByUserId(Long userId);
}

/**
 * Post Comment Repository
 */
@Repository
interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    /**
     * Find comments by post
     */
    List<PostComment> findByPostId(Long postId);
    
    /**
     * Find comments by user
     */
    List<PostComment> findByUserId(Long userId);
    
    /**
     * Find comments by post with pagination
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.createdAt ASC")
    List<PostComment> findCommentsByPostOrderByCreatedAtAsc(@Param("postId") Long postId);
    
    /**
     * Find top-level comments (no parent)
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.parentComment IS NULL ORDER BY pc.createdAt ASC")
    List<PostComment> findTopLevelCommentsByPost(@Param("postId") Long postId);
    
    /**
     * Find replies to a comment
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.parentComment.id = :parentCommentId ORDER BY pc.createdAt ASC")
    List<PostComment> findRepliesByParentComment(@Param("parentCommentId") Long parentCommentId);
    
    /**
     * Count comments for a post
     */
    Long countByPostId(Long postId);
    
    /**
     * Count comments by user
     */
    Long countByUserId(Long userId);
    
    /**
     * Count replies for a comment
     */
    Long countByParentCommentId(Long parentCommentId);
}
