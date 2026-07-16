package br.com.lasanhaspec.carservice.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    S3StorageService.UploadResult uploadFile(MultipartFile file);
    /// ????????

    void deleteFile(String fileKey);
    //???????
}

