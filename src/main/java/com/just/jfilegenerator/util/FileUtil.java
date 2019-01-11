package com.just.jfilegenerator.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;

/**
 * 文字列に関するユーティリティクラス。
 * @author QuadJust
 */
public class FileUtil {
    /**
     * ファイル内容を文字列で取得する。
     * @param resource
     * @return ファイル内容。
     * @throws IOException 
     */
    public static String getFileContent(Resource resource) throws IOException {
        // ファイルの実パスを取得する。
        Path path = Paths.get(resource.getFile().getPath());
        // 指定のファイル URL のファイルをバイト列として読み込む。
        byte[] fileContentBytes = Files.readAllBytes(path);
        // 読み込んだバイト列を UTF-8 でデコードして文字列にする。
        String retval = new String(fileContentBytes, StandardCharsets.UTF_8);
        
        return retval;
    }
}
