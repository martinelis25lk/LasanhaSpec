package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.domain.models.ChronicIssue;
import br.com.lasanhaspec.carservice.domain.models.Comments;
import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.dto.CommentDTO;
import br.com.lasanhaspec.carservice.dto.CreateCommentsDTO;
import br.com.lasanhaspec.carservice.exception.BusinessException;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.repository.ChronicIssueRepository;
import br.com.lasanhaspec.carservice.repository.CommentsRepository;
import br.com.lasanhaspec.carservice.repository.UserRepository;
import br.com.lasanhaspec.carservice.repository.UserVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentsServiceTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private ChronicIssueRepository chronicIssueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserVehicleRepository userVehicleRepository;

    @InjectMocks
    private CommentsService commentsService;

    private User regularUser;
    private User adminUser;
    private ChronicIssue issue;
    private VehicleCatalogModel vehicleModel;

    @BeforeEach
    void setUp() {
        vehicleModel = new VehicleCatalogModel();
        vehicleModel.setId(100L);

        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setEmail("user@test.com");
        regularUser.setRole(Role.ROLE_USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(Role.ROLE_ADMIN);

        issue = new ChronicIssue();
        issue.setId(10L);
        issue.setVehicleCatalogModel(vehicleModel);
    }

    // ---------- createComment ----------

    @Test
    void shouldCreateRootCommentSuccessfully() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("Aconteceu comigo também");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));

        Comments saved = new Comments();
        saved.setUser(regularUser);
        saved.setChronicIssue(issue);
        saved.setContent(dto.getContent());
        when(commentsRepository.save(any(Comments.class))).thenReturn(saved);

        commentsService.createComment(10L, dto, "user@test.com");

        ArgumentCaptor<Comments> captor = ArgumentCaptor.forClass(Comments.class);
        verify(commentsRepository).save(captor.capture());
        assertNull(captor.getValue().getParentComment());
        assertEquals("Aconteceu comigo também", captor.getValue().getContent());
    }

    @Test
    void shouldCreateReplySuccessfully() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("Troquei pela peça X");
        dto.setParentCommentId(5L);

        Comments parent = new Comments();
        parent.setId(5L);
        parent.setChronicIssue(issue);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));
        when(commentsRepository.findById(5L)).thenReturn(Optional.of(parent));
        when(commentsRepository.save(any(Comments.class))).thenAnswer(inv -> inv.getArgument(0));

        commentsService.createComment(10L, dto, "user@test.com");

        ArgumentCaptor<Comments> captor = ArgumentCaptor.forClass(Comments.class);
        verify(commentsRepository).save(captor.capture());
        assertEquals(parent, captor.getValue().getParentComment());
    }

    @Test
    void shouldAllowReplyingToAReply() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("Resposta de resposta");
        dto.setParentCommentId(6L);

        Comments grandParent = new Comments();
        grandParent.setId(5L);
        grandParent.setChronicIssue(issue);

        Comments parentThatIsAlreadyAReply = new Comments();
        parentThatIsAlreadyAReply.setId(6L);
        parentThatIsAlreadyAReply.setChronicIssue(issue);
        parentThatIsAlreadyAReply.setParentComment(grandParent); // já é uma resposta

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));
        when(commentsRepository.findById(6L)).thenReturn(Optional.of(parentThatIsAlreadyAReply));
        when(commentsRepository.save(any(Comments.class))).thenAnswer(inv -> inv.getArgument(0));

        commentsService.createComment(10L, dto, "user@test.com");

        ArgumentCaptor<Comments> captor = ArgumentCaptor.forClass(Comments.class);
        verify(commentsRepository).save(captor.capture());
        // encadeamento em N niveis: responder a uma resposta agora eh permitido
        assertEquals(parentThatIsAlreadyAReply, captor.getValue().getParentComment());
    }

    @Test
    void shouldThrowWhenParentCommentBelongsToDifferentIssue() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("Comentário em issue errada");
        dto.setParentCommentId(5L);

        ChronicIssue otherIssue = new ChronicIssue();
        otherIssue.setId(999L);

        Comments parentFromOtherIssue = new Comments();
        parentFromOtherIssue.setId(5L);
        parentFromOtherIssue.setChronicIssue(otherIssue);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));
        when(commentsRepository.findById(5L)).thenReturn(Optional.of(parentFromOtherIssue));

        assertThrows(BusinessException.class,
                () -> commentsService.createComment(10L, dto, "user@test.com"));

        verify(commentsRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserDoesNotExistOnCreate() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("qualquer coisa");

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentsService.createComment(10L, dto, "missing@test.com"));

        verify(chronicIssueRepository, never()).findById(any());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenChronicIssueDoesNotExistOnCreate() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("qualquer coisa");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentsService.createComment(10L, dto, "user@test.com"));

        verify(commentsRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenParentCommentDoesNotExist() {
        CreateCommentsDTO dto = new CreateCommentsDTO();
        dto.setContent("resposta orfã");
        dto.setParentCommentId(999L);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));
        when(commentsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentsService.createComment(10L, dto, "user@test.com"));

        verify(commentsRepository, never()).save(any());
    }

    // ---------- listComments ----------

    @Test
    void shouldBuildCommentTreeWithRepliesNestedUnderRoot() {
        Comments root = new Comments();
        root.setId(1L);
        root.setUser(regularUser);
        root.setChronicIssue(issue);
        root.setContent("comentário raiz");

        Comments reply = new Comments();
        reply.setId(2L);
        reply.setUser(adminUser);
        reply.setChronicIssue(issue);
        reply.setContent("uma resposta");
        reply.setParentComment(root);

        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));
        when(commentsRepository.findByChronicIssueIdOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(root, reply));
        when(userVehicleRepository.existsByUserIdAndVehicleCatalogModelId(any(), any()))
                .thenReturn(false);

        List<CommentDTO> result = commentsService.listComments(10L);

        assertEquals(1, result.size());
        assertEquals("comentário raiz", result.get(0).getContent());
        assertEquals(1, result.get(0).getReplies().size());
        assertEquals("uma resposta", result.get(0).getReplies().get(0).getContent());
    }
    @Test
    void shouldBuildCommentTreeWithMultipleLevelsOfReplies() {
        Comments root = new Comments();
        root.setId(1L);
        root.setUser(regularUser);
        root.setChronicIssue(issue);
        root.setContent("comentário raiz");

        Comments reply = new Comments();
        reply.setId(2L);
        reply.setUser(adminUser);
        reply.setChronicIssue(issue);
        reply.setContent("uma resposta");
        reply.setParentComment(root);

        Comments replyToReply = new Comments();
        replyToReply.setId(3L);
        replyToReply.setUser(regularUser);
        replyToReply.setChronicIssue(issue);
        replyToReply.setContent("resposta da resposta");
        replyToReply.setParentComment(reply);

        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.of(issue));
        when(commentsRepository.findByChronicIssueIdOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(root, reply, replyToReply));
        when(userVehicleRepository.existsByUserIdAndVehicleCatalogModelId(any(), any()))
                .thenReturn(false);

        List<CommentDTO> result = commentsService.listComments(10L);

        assertEquals(1, result.size());
        CommentDTO rootDto = result.get(0);
        assertEquals("comentário raiz", rootDto.getContent());
        assertEquals(1, rootDto.getReplies().size());

        CommentDTO replyDto = rootDto.getReplies().get(0);
        assertEquals("uma resposta", replyDto.getContent());
        assertEquals(1, replyDto.getReplies().size());
        assertEquals("resposta da resposta", replyDto.getReplies().get(0).getContent());
    }

    @Test
    void shouldThrowWhenChronicIssueDoesNotExistOnList() {
        when(chronicIssueRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentsService.listComments(10L));
    }

    // ---------- deleteComment ----------

    @Test
    void shouldAllowAuthorToDeleteOwnComment() {
        Comments comment = new Comments();
        comment.setId(1L);
        comment.setUser(regularUser);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentsService.deleteComment(1L, "user@test.com");

        verify(commentsRepository).delete(comment);
    }

    @Test
    void shouldAllowAdminToDeleteAnyComment() {
        Comments comment = new Comments();
        comment.setId(1L);
        comment.setUser(regularUser); // dono é o usuario comum

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentsService.deleteComment(1L, "admin@test.com");

        verify(commentsRepository).delete(comment);
    }

    @Test
    void shouldThrowWhenNonAuthorNonAdminTriesToDelete() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setEmail("outro@test.com");
        anotherUser.setRole(Role.ROLE_USER);

        Comments comment = new Comments();
        comment.setId(1L);
        comment.setUser(regularUser); // dono é outro usuario

        when(userRepository.findByEmail("outro@test.com")).thenReturn(Optional.of(anotherUser));
        when(commentsRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(BusinessException.class,
                () -> commentsService.deleteComment(1L, "outro@test.com"));

        verify(commentsRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenCommentDoesNotExistOnDelete() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(regularUser));
        when(commentsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentsService.deleteComment(999L, "user@test.com"));
    }
}