package org.sumanta.rest.api;

public class Content {

    private String contentName = null;
    private String contentType = null;
    private byte[] byteContent = null;

    public Content(String name,String contentType, byte[] byteContent) {
        this.contentName = name;
        this.contentType = contentType;
        this.byteContent = byteContent;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getByteContent() {
        return byteContent;
    }

    public void setByteContent(byte[] byteContent) {
        this.byteContent = byteContent;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

}
