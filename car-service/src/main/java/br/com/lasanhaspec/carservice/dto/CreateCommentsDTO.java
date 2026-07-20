package br.com.lasanhaspec.carservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentsDTO {


    @NotBlank
    @Size(min = 1, max = 1000)
    private String content;

    private Long parentCommentId; // opcional -- null = comentario raiz

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }

}
