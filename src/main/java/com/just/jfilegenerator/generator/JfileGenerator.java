package com.just.jfilegenerator.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.just.jfilegenerator.dto.File;
import com.just.jfilegenerator.util.StringUtil;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;

/**
 * Javaファイルの自動生成クラス。
 * @author QuadJust
 */
public class JfileGenerator {
    /**
     * Javaファイルの拡張子名。
     */
    private static final String JAVA_EXTENSION = ".java";
    
    /**
     * XML文字列からJavaファイルを出力する。
     * @param xml XML文字列。
     * @return ファイル。
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws UnsupportedEncodingException 
     */
    public static File parseXML(String xml) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
        File retval = new File();
        // XMLの改行とインデントを取り除く。
        xml = StringUtil.clean(xml);
        
        // DOMパーサ用ファクトリの生成。
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 解析とDocumentインスタンスの取得。
        Document doc = (Document) factory.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));

        // TODO getNodeType()を使ってDOCTYPE宣言をスキップする
        Node node = doc.getFirstChild();
        String className = node.getNodeName();
        
        retval.setName(StringUtil.upperCaseFirst(className) + JAVA_EXTENSION);
        // 序文の書き出し。
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("public "));
        
        LinkedHashMap<String, StringBuilder> buildMap = new LinkedHashMap<String, StringBuilder>();
        buildMap.put("public", builder);
        
        parseJavaFileString(node, className, buildMap);
        
        StringBuilder javaFileBuilder = new StringBuilder();
        
        if (buildMap.get("import") != null) {
            javaFileBuilder.append(buildMap.remove("import"));
        }
        javaFileBuilder.append(buildMap.remove("public"));
        
        for (Entry<String, StringBuilder> entry : buildMap.entrySet()) {
            javaFileBuilder.append(entry.getValue());
        }
        
        System.out.println(javaFileBuilder.toString());
        
        retval.setContent(javaFileBuilder.toString());
        
        return retval;
    }
    
    /**
     * JSON文字列からJavaファイルを出力する。
     * @param json JSON文字列。
     * @return　ファイル。
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static File parseJSON(String json) throws JsonParseException, JsonMappingException, IOException {
        File retval = new File();
        
        // XMLの改行とインデントを取り除く。
        json = StringUtil.clean(json);
        
        // nodeの生成。
        JsonNode node = new ObjectMapper().readValue(json.toString(), JsonNode.class);
        // 親要素の取得。
        Iterator<String> fieldNames = node.fieldNames();
        String className = fieldNames.next();
        
        retval.setName(StringUtil.upperCaseFirst(className) + JAVA_EXTENSION);
        // 序文の書き出し。
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("public "));
        
        LinkedHashMap<String, StringBuilder> buildMap = new LinkedHashMap<String, StringBuilder>();
        buildMap.put("public", builder);
        
        parseJavaFileString(node, className, buildMap);
        
        StringBuilder javaFileBuilder = new StringBuilder();
        
        if (buildMap.get("import") != null) {
            javaFileBuilder.append(buildMap.remove("import"));
        }
        javaFileBuilder.append(buildMap.remove("public"));
        
        for (Entry<String, StringBuilder> entry : buildMap.entrySet()) {
            javaFileBuilder.append(entry.getValue());
        }
        
        System.out.println(javaFileBuilder.toString());
        
        retval.setContent(javaFileBuilder.toString());
        
        return retval;
    }
    
    /**
     * Javaファイルの文字列の書き出しを行う。
     * @param node 対象クラスのノード。
     * @param className 対象クラスのクラス名。
     * @param builder
     */
    private static void parseJavaFileString(Node node, String className,  LinkedHashMap<String, StringBuilder> buildMap) {
        // 同一クラスが既に存在した場合書き出しをスキップする。
        if (buildMap.get(className) != null) {
            return;
        }
        
        StringBuilder builder = new StringBuilder();
        
        // クラス名の書き出し。
        builder.append(String.format("class %s {\n", StringUtil.upperCaseFirst(className)));
        // getter/setterを格納するコレクションの初期化。
        LinkedHashMap<String, String> accessors = new LinkedHashMap<String, String>();
        // サブクラスを格納するコレクションの初期化。
        LinkedHashMap<String, Node> subClasses = new LinkedHashMap<String, Node>();
        // フィールド部分の取得。
        node = node.getFirstChild();
        // フィールドの書き出し。
        while (node != null) {
            String field;
            
            if (node instanceof DeferredElementImpl) {
                field = node.getNodeName();
            } else {
                field = node.getParentNode().getNodeName();
            }
            
            // フィールド名が重複する場合は読み飛ばす。
            if (accessors.get(field) != null) {
                node = node.getNextSibling();
                continue;
            }
            
            // インデントの書き出し。
            builder.append(String.format("\t"));
            String dataType;
            
            // 子要素がDeferredElementImplであれば新規classとしてファイルに書きだす。
            if(node.getFirstChild() instanceof DeferredElementImpl) {
                dataType = StringUtil.upperCaseFirst(field);
                
                Node childNode = node.getFirstChild();
                
                // 末尾がsのフィールドでnodeの子要素が単数形のnodeしかない場合、子要素のListとして扱う。
                if(dataType.endsWith("s") && !dataType.endsWith("ss")) {
                    // 単数形の取得。
                    String singular;
                    if (!dataType.endsWith("es")) {
                        singular = dataType.substring(0, dataType.length() - 1);
                    } else {
                        String tmp = dataType.substring(0, dataType.length() - 2);
                        if (!tmp.endsWith("o") && !tmp.endsWith("x") && !tmp.endsWith("s") && !tmp.endsWith("ch") && !tmp.endsWith("sh")) {
                            singular = dataType.substring(0, dataType.length() - 1);
                        } else {
                            singular = dataType.substring(0, dataType.length() - 2);
                        }
                    }
                    // nodeを先読みして配列化可能か調べる。
                    boolean isObjectArray = false;
                    String childText = null;
                    if (childNode.getFirstChild() != null && childNode.getFirstChild() instanceof DeferredElementImpl) {
                        isObjectArray = true;
                    } else {
                        childText = childNode.getTextContent();
                    }
                    
                    boolean isArray = true;
                    childNode = childNode.getNextSibling();
                    while (childNode != null) {
                        // 異なる名前のフィールドがあれば配列化不可。
                        if (!childNode.getNodeName().equals(childNode.getPreviousSibling().getNodeName())) {
                            isArray = false;
                        }
                        childNode = childNode.getNextSibling();
                    }
                    
                    if (childNode == null && isArray) {
                        if (isObjectArray) {
                            subClasses.put(StringUtil.lowerCaseFirst(singular), node.getFirstChild());
                            dataType = String.format("List<%s>", singular);
                        } else {
                            dataType = String.format("List<%s>", getDataType(childText));
                        }
                        
                        if (buildMap.get("import") == null) {
                            buildMap.put("import", new StringBuilder("import java.util.List;\n\n"));
                        }
                    } else {
                        subClasses.put(StringUtil.lowerCaseFirst(dataType), node);
                    }
                } else {
                    // 末尾がs意外のフィールドでも連続する場合はListとしてまとめる。
                    if (node.getNextSibling() != null && node.getNextSibling().getNodeName().equals(field)) {
                        Node target = node;
                        // nodeの連続が終わるまで読み飛ばす。
                        node = node.getNextSibling();
                        while (node.getNextSibling() != null && node.getNextSibling().equals(field))  {
                            node = node.getNextSibling();
                        }
                        subClasses.put(StringUtil.lowerCaseFirst(dataType), target);
                        dataType = String.format("List<%s>", dataType);
                        if (buildMap.get("import") == null) {
                            buildMap.put("import", new StringBuilder("import java.util.List;\n\n"));
                        }
                    } else {
                        subClasses.put(StringUtil.lowerCaseFirst(dataType), node);
                    }
                }
                
                builder.append(String.format("private %s %s;\n", dataType, field));
            } else {
                String text = node.getTextContent();
                dataType = getDataType(text);
                builder.append(String.format("private %s %s;\n", dataType, field));
            }
            // アクセサをセットする。
            accessors.put(field, getAccessor(field, dataType));
            
            node = node.getNextSibling();
            // 最後のフィールドかどうか調べる。
            if (node != null) {
                builder.append(String.format("\n"));
            }
        }
        
        for (Entry<String, String> entry : accessors.entrySet()) {
            builder.append(entry.getValue());
        }
        
        builder.append(String.format("}\n\n"));
        
        buildMap.put(className, builder);
        
        for (Entry<String, Node> entry : subClasses.entrySet()) {
            parseJavaFileString(entry.getValue(), entry.getKey(), buildMap);
        }
    }
    
    /**
     * Javaファイルの文字列の書き出しを行う。
     * @param node 対象クラスのノード。
     * @param className 対象クラスのクラス名。
     * @param builder
     */
    private static void parseJavaFileString(JsonNode node, String className,  LinkedHashMap<String, StringBuilder> buildMap) {
        // 同一クラスが既に存在した場合書き出しをスキップする。
        if (buildMap.get(className) != null) {
            return;
        }
        
        StringBuilder builder = new StringBuilder();
        
        // クラス名の書き出し。
        builder.append(String.format("class %s {\n", StringUtil.upperCaseFirst(className)));
        // getter/setterを格納するコレクションの初期化。
        LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
        // サブクラスを格納するコレクションの初期化。
        LinkedHashMap<String, JsonNode> subClasses = new LinkedHashMap<String, JsonNode>();
        // フィールド部分の取得。
        if (node.get(className) != null) {
            node = node.get(className);
        }
        Iterator<String> fieldNames = node.fieldNames();
        if (node instanceof ArrayNode) {
            fieldNames = node.get(0).fieldNames();
            node = node.get(0);
        }
        
        // スキップフラグ。
        boolean skip = false;
        String field = null;
        String tmpField = null;
        
        // フィールドの書き出し。
        while (fieldNames.hasNext()) {
            if (skip) {
                field = tmpField;
                skip = !skip;
            } else {
                field = fieldNames.next();
            }
            JsonNode childNode = node.get(field);
            
            // フィールド名が重複する場合は読み飛ばす。
            if (fields.get(field) != null) {
                continue;
            }
            
            // インデントの書き出し。
            builder.append(String.format("\t"));
            String dataType;
            
            // 子要素がDeferredElementImplであれば新規classとしてファイルに書きだす。
            if(childNode instanceof ObjectNode || (childNode instanceof ArrayNode && childNode.get(0) instanceof ObjectNode)) {
                dataType = StringUtil.upperCaseFirst(field);
                
                Iterator<String> childFieldNames = childNode.fieldNames();
                Iterator<JsonNode> childElements = childNode.elements();
                
                // 末尾がsのフィールドでnodeの子要素が単数形のnodeしかない場合、子要素のListとして扱う。
                if(dataType.endsWith("s") && !dataType.endsWith("ss")) {
                    // 単数形の取得。
                    String singular;
                    if (!dataType.endsWith("es")) {
                        singular = dataType.substring(0, dataType.length() - 1);
                    } else {
                        String tmp = dataType.substring(0, dataType.length() - 2);
                        if (!tmp.endsWith("o") && !tmp.endsWith("x") && !tmp.endsWith("s") && !tmp.endsWith("ch") && !tmp.endsWith("sh")) {
                            singular = dataType.substring(0, dataType.length() - 1);
                        } else {
                            singular = dataType.substring(0, dataType.length() - 2);
                        }
                    }
                    
                    // nodeを先読みして配列化可能か調べる。
                    String childField = null;
                    JsonNode childElement = null;
                    boolean isObjectArray = false;
                    boolean isArray = true;
                    String childText = null;
                    if (childFieldNames.hasNext() && childElements.hasNext()) {
                        childField = childFieldNames.next();
                        childElement = childElements.next();
                        if(childElement instanceof ObjectNode || (childElement instanceof ArrayNode && childElement.get(0) instanceof ObjectNode)) {
                            isObjectArray = true;
                        } else {
                            childText = childElement.asText();
                        }
                        while(childFieldNames.hasNext() && childElements.hasNext()) {
                            String nextChildField = childFieldNames.next();
                            // 異なる名前のフィールドがあれば配列化不可。
                            if (childField.equals(nextChildField)) {
                                childElements.next();
                            } else {
                                isArray = false;
                                break;
                            }
                        }
                    }
                    
                    if (!childFieldNames.hasNext() && isArray) {
                        if (isObjectArray) {
                            subClasses.put(StringUtil.lowerCaseFirst(singular), childNode);
                            dataType = String.format("List<%s>", singular);
                        } else {
                            dataType = String.format("List<%s>", getDataType(childText));
                        }
                        
                        if (buildMap.get("import") == null) {
                            buildMap.put("import", new StringBuilder("import java.util.List;\n\n"));
                        }
                    } else {
                        subClasses.put(StringUtil.lowerCaseFirst(dataType), node);
                    }
                } else {
                    // 末尾がs意外のフィールドでも連続する場合はListとしてまとめる。
                    if (fieldNames.hasNext()) {
                        tmpField = fieldNames.next();
                        if (field.equals(tmpField)) {
                            while (field.equals(tmpField)) {
                                tmpField = fieldNames.next();
                            }
                            
                            JsonNode target = node;
                            subClasses.put(StringUtil.lowerCaseFirst(dataType), target);
                            dataType = String.format("List<%s>", dataType);
                            if (buildMap.get("import") == null) {
                                buildMap.put("import", new StringBuilder("import java.util.List;\n\n"));
                            }
                        } else {
                            subClasses.put(StringUtil.lowerCaseFirst(dataType), node);
                        }
                        skip = true;
                    } else {
                        subClasses.put(StringUtil.lowerCaseFirst(dataType), node);
                    }
                }
                
                builder.append(String.format("private %s %s;\n", dataType, field));
            } else {
                if (childNode instanceof ArrayNode) {
                    childNode =  childNode.get(0);
                }
                if (childNode.isInt()) {
                    dataType = "Integer";
                } else if (childNode.isLong()) {
                    dataType = "Long";
                } else if (childNode.isFloat()) {
                    dataType = "Float";
                } else if (childNode.isDouble()) {
                    dataType = "Double";
                } else if (childNode.isBoolean()) {
                    dataType = "Boolean";
                } else {
                    String o = childNode.asText();
                    if (StringUtil.isDate(o)) {
                        dataType = "Date";
                    } else {
                        dataType = "String";
                    }
                }
                builder.append(String.format("private %s %s;\n", dataType, field));
            }
            // アクセサをセット。
            fields.put(field, getAccessor(field, dataType));
            // 最後のフィールドかどうか調べる。
            if (fieldNames.hasNext()) {
                builder.append(String.format("\n"));
            }
        }
        
        for (Entry<String, String> entry : fields.entrySet()) {
            builder.append(entry.getValue());
        }
        
        builder.append(String.format("}\n\n"));
        
        buildMap.put(className, builder);
        
        for (Entry<String, JsonNode> entry : subClasses.entrySet()) {
            parseJavaFileString(entry.getValue(), entry.getKey(), buildMap);
        }
    }
    
    /**
     * アクセサを取得する。
     * @param str
     * @param dataType
     * @return アクセサ文字列。
     */
    private static String getAccessor(String str, String dataType) {
        StringBuilder retval = new StringBuilder();
        // getter部。
        retval.append(String.format("\n"));
        retval.append(String.format("\t"));
        retval.append(
                String.format("public %s get%s() {\n",
                        dataType,
                        StringUtil.upperCaseFirst(str)));
        retval.append(String.format("\t\t"));
        retval.append(
                String.format("return this.%s;\n",
                        str));
        retval.append(String.format("\t"));
        retval.append(String.format("}\n\n"));
        // setter部。
        retval.append(String.format("\t"));
        retval.append(
                String.format("public void set%s(%s %s) {\n",
                        StringUtil.upperCaseFirst(str),
                        dataType,
                        str));
        retval.append(String.format("\t\t"));
        retval.append(
                String.format("this.%s = %s;\n",
                        str,
                        str));
        retval.append(String.format("\t"));
        retval.append(String.format("}\n"));
        
        return retval.toString();
    }
    
    /**
     * データ型を取得する。
     * @param str
     * @return データ型。
     */
    private static String getDataType(String str) {
        if (StringUtil.isDate(str)) {
            return "Date";
        } else if (StringUtil.isInteger(str)) {
            return "Integer";
        } else if (StringUtil.isLong(str)) {
            return "Long";
        } else if (StringUtil.isFloat(str)) {
            return "Float";
        } else if (StringUtil.isDouble(str)) {
            return "Double";
        } else if (StringUtil.isBoolean(str)) {
            return "Boolean";
        } else {
            return "String";
        }
    }
    
}
