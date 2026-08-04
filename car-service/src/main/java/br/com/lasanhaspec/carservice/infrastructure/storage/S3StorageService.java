package br.com.lasanhaspec.carservice.infrastructure.storage;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.UUID;

@Service
public class S3StorageService implements  StorageService{

    private final S3Client s3Client;
    private final String bucketName;
    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);



    public S3StorageService(
            @Value("${spring.aws.s3.bucket-name}") String bucketName,
            @Value("${spring.aws.region}") String region
    ){
        this.bucketName = bucketName;
        this.s3Client   = S3Client.builder()
                .region(Region.of(region))
                .build();
    }





    @Override
    public UploadResult uploadFile(MultipartFile file){

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename().replace(" ", "_");

        //String fileName = UUID.randomUUID() + "-shmalugou-" + file.getOriginalFilename();


        try{
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException e) {
            //log.error("FIPE API request failed for brandCode={}, modelCode={}, yearCode={}: {}",
             //       brandCode, modelCode, yearCode, e.getMessage(), e);
            return null;
        }

        String url = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

        return new UploadResult(url, fileName);

    }


    @Override
    public void deleteFile(String fileKey){
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build()


        );
    }


    public static class UploadResult {

        private final String url;
        private final String key;

        public UploadResult(String url, String key) {
            this.url = url;
            this.key = key;
        }

        public String getUrl() {
            return url;
        }

        public String getKey() {
            return key;
        }
    }
}
