package com.demo.api.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.api.payload.response.FileResponse;
import com.demo.api.payload.response.MessageResponse;
import com.demo.api.services.FileStorageService;
import com.demo.api.models.FileInfo;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/files")
public class FilesController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        try {
            fileStorageService.store(file);

            message = "Uploaded file successfully: " + file.getOriginalFilename();
            return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
        } catch (Exception e) {
            message = "Could not upload file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return new ResponseEntity<MessageResponse>(new MessageResponse(message), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileResponse>> getAllFiles() {
        List<FileResponse> files = fileStorageService.getAllFiles().map(file -> {
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/files/")
                    .path(file.getId()).toUriString();

            return new FileResponse(
                    file.getName(),
                    fileDownloadUri,
                    file.getType(),
                    file.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        FileInfo file = fileStorageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getData());
    }
}
