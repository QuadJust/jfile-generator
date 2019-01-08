package com.just.jfilegenerator.sample;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.just.jfilegenerator.dto.File;
import com.just.jfilegenerator.generator.JfileGenerator;

@Controller
public class SampleController {
    @Autowired
    ResourceLoader resourceLoader;
    
    @RequestMapping(value = "/generate-xml", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> generateXML(HttpServletResponse response) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:sample.xml");
        
        // 指定のファイル URL のファイルをバイト列として読み込む
        byte[] fileContentBytes = Files.readAllBytes(Paths.get(resource.getFile().getPath()));
        // 読み込んだバイト列を UTF-8 でデコードして文字列にする
        String fileContent = new String(fileContentBytes, StandardCharsets.UTF_8);
                
        File file = JfileGenerator.parseXML(fileContent);

        //ファイル書き込み
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("filename", file.getName());
        headers.setContentLength(file.getContent().getBytes().length);
        
        return new ResponseEntity<>(file.getContent().getBytes(), headers, HttpStatus.OK);
    }
}
