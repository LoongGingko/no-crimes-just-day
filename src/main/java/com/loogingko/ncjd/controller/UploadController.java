package com.loogingko.ncjd.controller;

import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        String url = uploadService.upload(file);
        return new R().extra("url", url);
    }
}
