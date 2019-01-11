package com.just.filegenerator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.just.jfilegenerator.dto.File;
import com.just.jfilegenerator.generator.JfileGenerator;
import com.just.jfilegenerator.util.FileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class JfileGeneratorParseTest {
    @Autowired
    ResourceLoader resourceLoader;
    
    @Test
    public void xmlTest1() throws Exception {
        // XMLファイルの読み込み。
        String xml = FileUtil.getFileContent(resourceLoader.getResource("classpath:test/test1.xml"));
        File file = JfileGenerator.parseXML(xml);
        
        // 期待するJavaファイルの読み込み。
        String expected = FileUtil.getFileContent(resourceLoader.getResource("classpath:test/test1.java"));

        // 正当性の検証。
        assertEquals(expected, file.getContent());
    };
    
    @Test
    public void jsonTest1() throws Exception {
        // JSONファイルの読み込み。
        String xml = FileUtil.getFileContent(resourceLoader.getResource("classpath:test/test1.json"));
        File file = JfileGenerator.parseJSON(xml);
        
        // 期待するJavaファイルの読み込み。
        String expected = FileUtil.getFileContent(resourceLoader.getResource("classpath:test/test1.java"));

        // 正当性の検証。
        assertEquals(expected, file.getContent());
    };
}