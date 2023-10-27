package igoMoney.BE.service;


import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageService {

    private final S3Client s3Client;

    // Upload 하고자 하는 버킷의 이름
    private String bucketName = "igomoney-bucket";


    public String uploadImage(MultipartFile multipartFile) throws IOException {
        String key = UUID.randomUUID().toString(); // Storage에 저장될 파일 이름
        return this.putS3(multipartFile, key);
    }

    // 파일 업로드를 하기위한 PutObjectRequest를 반환합니다.
    // 주의 사항은 com.amazonaws Package가 아닌 software.amazon.awssdk를 사용해야 합니다.
    // key는 저장하고자 하는 파일의 이름(?)을 의미합니다.
//    private PutObjectRequest getPutObjectRequest(String key) {
//        return PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//    }

    // MultipartFile을 업로드 하기위해 RequestBody.fromInputStream에 InputStream과 file의 Size를 넣어줍니다.
    private RequestBody getFileRequestBody(MultipartFile file) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    // S3Utilities를 통해 GetUrlRequest를 파라미터로 넣어 파라미터로 넘어온 key의 접근 경로를 URL로 반환받아 경로를 사용할 수 있다.
//    private String findUploadKeyUrl (String key) {
//        S3Utilities s3Utilities = s3Client.utilities();
//        GetUrlRequest request = GetUrlRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//
//        URL url = s3Utilities.getUrl(request);
//
//        return url.toString();
//    }

    // 실제 업로드 하는 메소드
    private String putS3(MultipartFile file, String key) throws IOException {

        String ext = file.getContentType();
//        ObjectMetadata metadata = new ObjectMetadata();

//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("Content-Type", ext);
//        metadata.put("Content-Type", ext);
//        metadata.setContentType(ext);
//        metadata.setContentLength(file.getSize());
        PutObjectRequest objectRequest =PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(ext)
                .contentLength(file.getSize())
                .build();

        RequestBody rb = getFileRequestBody(file);
        s3Client.putObject(objectRequest, rb);
//        s3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
//        return findUploadKeyUrl(key);
        return key;
    }



    //=========== 파일 다운로드 ===========
    public String processImage(String image) {

        if (StringUtils.isBlank(image)) {
            return null;
        }

        System.out.println(">>>>>>>>>> >>>>>>>>>>"+ image);
        return "https://igomoney-bucket.s3.ap-northeast-2.amazonaws.com/" + image;
    }


}