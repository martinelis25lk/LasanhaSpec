package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.domain.models.ChronicIssue;
import br.com.lasanhaspec.carservice.domain.models.Comments;
import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.dto.CommentDTO;
import br.com.lasanhaspec.carservice.dto.CreateCommentsDTO;
import br.com.lasanhaspec.carservice.exception.BusinessException;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.repository.ChronicIssueRepository;
import br.com.lasanhaspec.carservice.repository.CommentsRepository;
import br.com.lasanhaspec.carservice.repository.UserRepository;
import br.com.lasanhaspec.carservice.repository.UserVehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentsService {

    private final CommentsRepository commentRepository;
    private final ChronicIssueRepository chronicIssueRepository;
    private final UserRepository userRepository;
    private final UserVehicleRepository userVehicleRepository;

    public CommentsService(CommentsRepository commentRepository,
                          ChronicIssueRepository chronicIssueRepository,
                          UserRepository userRepository,
                          UserVehicleRepository userVehicleRepository) {
        this.commentRepository = commentRepository;
        this.chronicIssueRepository = chronicIssueRepository;
        this.userRepository = userRepository;
        this.userVehicleRepository = userVehicleRepository;
    }

    public Long createComment(Long chronicIssueId, CreateCommentsDTO dto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChronicIssue issue = chronicIssueRepository.findById(chronicIssueId)
                .orElseThrow(() -> new ResourceNotFoundException("Chronic issue not found"));

        Comments comment = new Comments();
        comment.setChronicIssue(issue);
        comment.setUser(user);
        comment.setContent(dto.getContent());

        if (dto.getParentCommentId() != null) {
            Comments parent = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            if (!parent.getChronicIssue().getId().equals(chronicIssueId)) {
                throw new BusinessException("Parent comment does not belong to this chronic issue");
            }
            // so 1 nivel de resposta: nao permite responder a uma resposta
            if (parent.getParentComment() != null) {
                throw new BusinessException("Cannot reply to a reply — only one level of nesting is allowed");
            }
            comment.setParentComment(parent);
        }

        return commentRepository.save(comment).getId();
    }

    public List<CommentDTO> listComments(Long chronicIssueId) {

        ChronicIssue issue = chronicIssueRepository.findById(chronicIssueId)
                .orElseThrow(() -> new ResourceNotFoundException("Chronic issue not found"));

        List<Comments> all = commentRepository.findByChronicIssueIdOrderByCreatedAtAsc(chronicIssueId);

        // separa raizes de respostas, monta a arvore de 1 nivel
        List<Comments> roots = all.stream().filter(c -> c.getParentComment() == null).toList();

        return roots.stream().map(root -> {
            CommentDTO dto = toDTO(root, issue);
            List<CommentDTO> replies = all.stream()
                    .filter(c -> c.getParentComment() != null &&
                            c.getParentComment().getId().equals(root.getId()))
                    .map(reply -> toDTO(reply, issue))
                    .collect(Collectors.toList());
            dto.setReplies(replies);
            return dto;
        }).toList();
    }

    public void deleteComment(Long commentId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        boolean isAuthor = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new BusinessException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentDTO toDTO(Comments comment, ChronicIssue issue) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setAuthorId(comment.getUser().getId());
        dto.setAuthorName(comment.getUser().getFullName());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setOwnsVehicle(userVehicleRepository.existsByUserIdAndVehicleCatalogModelId(
                comment.getUser().getId(), issue.getVehicleCatalogModel().getId()));
        return dto;
    }
}