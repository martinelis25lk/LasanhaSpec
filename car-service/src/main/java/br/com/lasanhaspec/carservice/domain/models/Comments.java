package br.com.lasanhaspec.carservice.domain.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "comments")
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
    private Comments parentComment; // null = comentário raiz; preenchido = resposta a outro comentário

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }


    public void setUser(User user) {
    }

    public void setChronicIssue(ChronicIssue issue) {
    }

    public void setContent(String content) {
    }

    public void setParentComment(Comments parent) {
    }

    public User getUser() {
    }

    public Long getId() {
    }

    public String getContent() {
    }

    public LocalDateTime getCreatedAt() {
    }

    public Comments getParentComment() {
    }

    public Comments getChronicIssue() {
    }
}
