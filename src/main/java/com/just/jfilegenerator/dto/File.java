package com.just.jfilegenerator.dto;

import java.io.Serializable;

/**
 * ファイルのDTOクラス。
 * @author QuadJust
 */
public class File implements Serializable {
    /**
     * シリアルバージョンUID。
     */
    private static final long serialVersionUID = -8843769890437241507L;

    /**
     * 名前。
     */
    private String name;
    
    /**
     * 親ディレクトリ。
     */
    private String parent;
    
    /**
     * 内容。
     */
    private String content;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
