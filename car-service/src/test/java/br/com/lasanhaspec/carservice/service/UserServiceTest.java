package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.infrastructure.storage.S3StorageService;
import br.com.lasanhaspec.carservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3StorageService storageService;

    @InjectMocks
    private UserService userService;

    // ---------- loadUserByUsername ----------

    @Test
    void shouldLoadUserWhenEmailExists() {
        User user = new User();
        user.setEmail("user@test.com");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("user@test.com");

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenEmailDoesNotExistOnLoad() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("missing@test.com"));
    }

    // ---------- uploadProfileImage ----------

    @Test
    void shouldUploadImageAndUpdateUserWhenNoPreviousImage() {
        User user = new User();
        user.setEmail("user@test.com");
        user.setProfileImageS3Key(null); // sem imagem anterior

        MultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "conteudo-fake".getBytes());

        S3StorageService.UploadResult uploadResult =
                new S3StorageService.UploadResult("https://s3/avatar.png", "key123");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(storageService.uploadFile(file)).thenReturn(uploadResult);

        String resultUrl = userService.uploadProfileImage("user@test.com", file);

        assertEquals("https://s3/avatar.png", resultUrl);
        assertEquals("key123", user.getProfileImageS3Key());
        verify(storageService, never()).deleteFile(any()); // nao tinha imagem antiga pra apagar
        verify(userRepository).save(user);
    }

    @Test
    void shouldDeleteOldImageBeforeUploadingNewOne() {
        User user = new User();
        user.setEmail("user@test.com");
        user.setProfileImageS3Key("old-key");

        MultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "conteudo-fake".getBytes());

        S3StorageService.UploadResult uploadResult =
                new S3StorageService.UploadResult("https://s3/new.png", "new-key");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(storageService.uploadFile(file)).thenReturn(uploadResult);

        userService.uploadProfileImage("user@test.com", file);

        verify(storageService).deleteFile("old-key");
    }

    @Test
    void shouldThrowWhenUserDoesNotExistOnUpload() {
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "conteudo-fake".getBytes());

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.uploadProfileImage("missing@test.com", file));

        verify(storageService, never()).uploadFile(any());
    }

    @Test
    void shouldThrowWhenFileIsEmpty() {
        User user = new User();
        user.setEmail("user@test.com");

        MultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.uploadProfileImage("user@test.com", emptyFile));
    }

    @Test
    void shouldThrowWhenFileIsTooLarge() {
        User user = new User();
        user.setEmail("user@test.com");

        byte[] tooLarge = new byte[6 * 1024 * 1024]; // 6MB, acima do limite de 5MB
        MultipartFile bigFile = new MockMultipartFile("file", "big.png", "image/png", tooLarge);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.uploadProfileImage("user@test.com", bigFile));
    }

    @Test
    void shouldThrowWhenContentTypeIsNotImage() {
        User user = new User();
        user.setEmail("user@test.com");

        MultipartFile pdfFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "conteudo".getBytes());

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.uploadProfileImage("user@test.com", pdfFile));
    }

    @Test
    void shouldThrowWhenExtensionIsUnsupportedEvenIfContentTypeIsImage() {
        User user = new User();
        user.setEmail("user@test.com");

        // content-type diz "image", mas a extensao do arquivo nao e suportada
        MultipartFile gifFile = new MockMultipartFile(
                "file", "avatar.gif", "image/gif", "conteudo".getBytes());

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.uploadProfileImage("user@test.com", gifFile));
    }
}