package br.com.lasanhaspec.carservice.domain.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comments {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chronic_issue_id")
    private ChronicIssue chronicIssue;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comments parentComment;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public void setUser(User user) { this.user = user; }
    public void setChronicIssue(ChronicIssue issue) { this.chronicIssue = issue; }
    public void setContent(String content) { this.content = content; }
    public void setParentComment(Comments parent) { this.parentComment = parent; }

    public User getUser() { return user; }
    public Long getId() { return id; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Comments getParentComment() { return parentComment; }
    public ChronicIssue getChronicIssue() { return chronicIssue; }
}