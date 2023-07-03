package com.demo.api.services;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.demo.api.models.FileInfo;
import com.demo.api.repositories.FileRepository;

@Service
public class FileStorageService {

    @Autowired
    private FileRepository fileRepository;

    public FileInfo store(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        FileInfo fileInfo = new FileInfo(filename, file.getContentType(), file.getBytes());

        return fileRepository.save(fileInfo);
    }

    public FileInfo getFile(String id) {
        return fileRepository.findById(id).get();
    }

    public Stream<FileInfo> getAllFiles() {
        return fileRepository.findAll().stream();
    }
}
