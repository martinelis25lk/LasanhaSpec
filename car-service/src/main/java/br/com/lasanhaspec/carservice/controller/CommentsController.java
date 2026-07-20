package br.com.lasanhaspec.carservice.controller;

import br.com.lasanhaspec.carservice.dto.CommentDTO;
import br.com.lasanhaspec.carservice.dto.CreateCommentsDTO;
import br.com.lasanhaspec.carservice.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chronic-issues/{issueId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Long> create(
            @PathVariable Long issueId,
            @Valid @RequestBody CreateCommentsDTO dto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long id = commentService.createComment(issueId, dto, email);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> list(@PathVariable Long issueId) {
        return ResponseEntity.ok(commentService.listComments(issueId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long issueId,
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}