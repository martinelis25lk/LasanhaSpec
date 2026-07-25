package br.com.lasanhaspec.carservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Long id;
    private Long authorId;
    private String authorUsername;
    private String parentAuthorUsername;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private boolean ownsVehicle; // selo "dono confirmado"
    private List<CommentDTO> replies;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isOwnsVehicle() { return ownsVehicle; }
    public void setOwnsVehicle(boolean ownsVehicle) { this.ownsVehicle = ownsVehicle; }
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getParentAuthorUsername() { return parentAuthorUsername; }
    public void setParentAuthorUsername(String parentAuthorUsername) { this.parentAuthorUsername = parentAuthorUsername; }
}