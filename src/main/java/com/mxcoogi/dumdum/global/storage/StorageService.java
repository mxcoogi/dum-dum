package com.mxcoogi.dumdum.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * 파일 업로드 후 접근 가능한 URL 반환
     * @param file 업로드할 파일
     * @param directory 저장 디렉토리 (예: "products", "stores")
     */
    String upload(MultipartFile file, String directory);

    /** URL로 파일 삭제 */
    void delete(String fileUrl);
}
