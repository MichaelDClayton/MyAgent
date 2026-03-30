package com.concierge.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final Path root = Paths.get("/uploads");

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }
        try{
            String fileName = file.getOriginalFilename();
            String cleanName = StringUtils.cleanPath(fileName);
            long fileSize = file.getSize();
            //Move file to uploads directory
            Files.copy(file.getInputStream(), this.root.resolve(Objects.requireNonNull(cleanName)));
            return ResponseEntity.ok("File uploaded successfully: "+fileName);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Could not upload the file");
        }
    }
}