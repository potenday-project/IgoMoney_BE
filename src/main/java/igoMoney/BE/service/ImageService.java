package igoMoney.BE.service;
/*
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageService {

    private final String endPoint = "https://kr.object.ncloudstorage.com"; // Naver Object Storage
    private final String regionName = "kr-standard";

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private  String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private  String secretKey;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    String objectName = "sample-large-object";

    // S3 client
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            .build();


    public String uploadImage(MultipartFile image) throws IOException {

        File file = multipartFileToFile(image);
        long contentLength = file.length();
        long partSize = 10 * 1024 * 1024;
        String uuid = UUID.randomUUID().toString();

        try {


            // initialize and get upload ID
            InitiateMultipartUploadResult initiateMultipartUploadResult = s3.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, objectName));
            String uploadId = initiateMultipartUploadResult.getUploadId();

            // upload parts
            List<PartETag> partETagList = new ArrayList<PartETag>();

            long fileOffset = 0;
            for (int i = 1; fileOffset < contentLength; i++) {
                partSize = Math.min(partSize, (contentLength - fileOffset));

                UploadPartRequest uploadPartRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(uuid)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFile(file)
                        .withFileOffset(fileOffset)
                        .withPartSize(partSize);

                UploadPartResult uploadPartResult = s3.uploadPart(uploadPartRequest);
                partETagList.add(uploadPartResult.getPartETag());

                fileOffset += partSize;
            }

            // abort
            // s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, objectName, uploadId));

            // complete
            CompleteMultipartUploadResult completeMultipartUploadResult = s3.completeMultipartUpload(new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETagList));
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch(SdkClientException e) {
            e.printStackTrace();
        }
        return uuid;
    }*/

//        public String processImage(String image) {
//
//            try {
//                S3Object s3Object = s3.getObject(bucketName, objectName);
//                S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
//
//                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFilePath));
//                byte[] bytesArray = new byte[4096];
//                int bytesRead = -1;
//                while ((bytesRead = s3ObjectInputStream.read(bytesArray)) != -1) {
//                    outputStream.write(bytesArray, 0, bytesRead);
//                }
//
//                outputStream.close();
//                s3ObjectInputStream.close();
//                System.out.format("Object %s has been downloaded.\n", objectName);
//            } catch (AmazonS3Exception e) {
//                e.printStackTrace();
//            } catch(SdkClientException e) {
//                e.printStackTrace();
//            }
//        return "https://storage.googleapis.com/" + bucketName + "/" + image;
//    }


//    private final Storage storage;
//
//    public String uploadImage(MultipartFile image) throws IOException {
//
//        String uuid = UUID.randomUUID().toString(); // Google Cloud Storage에 저장될 파일 이름
//        String ext = image.getContentType();
//
//        BlobInfo blobInfo = storage.create(
//                BlobInfo.newBuilder(bucketName, uuid)
//                        .setContentType(ext)
//                        .build(),
//                image.getInputStream()
//        );
//
//        return uuid;
//    }
//
//    public String processImage(String image) {
//
//        if (StringUtils.isBlank(image)) {
//            return null;
//        }
//        if (image.startsWith("https://")) { // 구글 프로필 이미지 처리
//            return image;
//        }
//        return "https://storage.googleapis.com/" + bucketName + "/" + image;
//    }

//    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
//        File file = new File(multipartFile.getOriginalFilename());
//        multipartFile.transferTo(file);
//        return file;
//    }
//}