package com.likelion.basecode.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.likelion.basecode.common.error.ErrorCode;
import com.likelion.basecode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();

        try {
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL, ErrorCode.S3_UPLOAD_FAIL.getMessage());
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에서 이미지 삭제
     *
     * @param imageUrl 삭제할 이미지의 URL
     * @throws IllegalArgumentException 이미지 삭제 실패 시 발생하는 예외
     */
    public void delete(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL, "이미지 URL이 비어 있습니다.");
        }

        try {
            // 전달받은 imageUrl이 key인 경우 → 바로 사용
            String decodedKey = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);

            if (!amazonS3.doesObjectExist(bucket, decodedKey)) {
                log.warn("S3에 존재하지 않는 파일입니다: {}", decodedKey);
                return;
            }

            amazonS3.deleteObject(bucket, decodedKey);
            log.info("파일 삭제 성공: {}", decodedKey);

        } catch (Exception e) {
            log.error("S3 이미지 삭제 실패", e);
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAIL, "이미지 삭제가 실패했습니다.");
        }
    }



}
