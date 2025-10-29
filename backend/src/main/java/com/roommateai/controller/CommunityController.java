package com.roommateai.controller;

import com.roommateai.dto.PostRequest;
import com.roommateai.dto.PostResponse;
import com.roommateai.dto.CommentRequest;
import com.roommateai.dto.CommentResponse;
import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Community Controller
 * REST endpoints for community posts, likes, and comments
 */
@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AuthService authService;

    /**
     * Create a new post
     */
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest request,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            PostResponse response = communityService.createPost(request, user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create post");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all posts
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            Long currentUserId = user != null ? user.getId() : null;

            List<PostResponse> posts = communityService.getAllPosts(currentUserId);
            return ResponseEntity.ok(posts);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get posts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get posts by category
     */
    @GetMapping("/posts/category/{category}")
    public ResponseEntity<?> getPostsByCategory(@PathVariable String category,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            Long currentUserId = user != null ? user.getId() : null;

            List<PostResponse> posts = communityService.getPostsByCategory(category, currentUserId);
            return ResponseEntity.ok(posts);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get posts by category");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get posts by user
     */
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable Long userId,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            Long currentUserId = user != null ? user.getId() : null;

            List<PostResponse> posts = communityService.getPostsByUser(userId, currentUserId);
            return ResponseEntity.ok(posts);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get posts by user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get post by ID
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            Long currentUserId = user != null ? user.getId() : null;

            Optional<PostResponse> post = communityService.getPostById(postId, currentUserId);
            
            if (post.isPresent()) {
                return ResponseEntity.ok(post.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get post");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Search posts
     */
    @GetMapping("/posts/search")
    public ResponseEntity<?> searchPosts(@RequestParam String q,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            Long currentUserId = user != null ? user.getId() : null;

            List<PostResponse> posts = communityService.searchPosts(q, currentUserId);
            return ResponseEntity.ok(posts);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to search posts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Like or unlike a post
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            boolean isLiked = communityService.togglePostLike(postId, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("message", isLiked ? "Post liked" : "Post unliked");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to toggle post like");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Add a comment to a post
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                      @Valid @RequestBody CommentRequest request,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            CommentResponse response = communityService.addComment(postId, request, user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add comment");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get comments for a post
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable Long postId) {
        List<CommentResponse> comments = communityService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Update a post
     */
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                      @Valid @RequestBody PostRequest request,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Optional<PostResponse> updatedPost = communityService.updatePost(postId, request, user);
            
            if (updatedPost.isPresent()) {
                return ResponseEntity.ok(updatedPost.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update post");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a post
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            boolean deleted = communityService.deletePost(postId, user);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete post");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Pin or unpin a post (Admin only)
     */
    @PostMapping("/posts/{postId}/pin")
    public ResponseEntity<?> togglePostPin(@PathVariable Long postId,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null || !user.getRole().equals(User.UserRole.ADMIN)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Admin access required"));
            }

            boolean isPinned = communityService.togglePostPin(postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isPinned", isPinned);
            response.put("message", isPinned ? "Post pinned" : "Post unpinned");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to toggle post pin");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get community statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCommunityStats() {
        Map<String, Object> stats = communityService.getCommunityStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get trending posts
     */
    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingPosts(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            Long currentUserId = user != null ? user.getId() : null;

            List<PostResponse> posts = communityService.getTrendingPosts(currentUserId);
            return ResponseEntity.ok(posts);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get trending posts");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get available post categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getPostCategories() {
        Map<String, Object> categories = new HashMap<>();
        categories.put("categories", List.of(
            Map.of("id", "GENERAL", "name", "General", "description", "General discussions"),
            Map.of("id", "BUY_SELL", "name", "Buy/Sell", "description", "Buying and selling items"),
            Map.of("id", "ROOMMATE_SEARCH", "name", "Roommate Search", "description", "Looking for roommates"),
            Map.of("id", "ANNOUNCEMENT", "name", "Announcement", "description", "Important announcements")
        ));
        return ResponseEntity.ok(categories);
    }
}
