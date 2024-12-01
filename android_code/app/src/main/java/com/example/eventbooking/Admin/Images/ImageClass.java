package com.example.eventbooking.Admin.Images;



public class ImageClass {
    private String imageUrl;
    private String source;
    private String documentId;
    private String collection;

    /**
     * Constuctor for the image class
     */
    public ImageClass (String imageUrl, String source, String documentId, String collection) {
        this.imageUrl = imageUrl;
        this.source = source;
        this.documentId = documentId;
        this.collection = collection;
    }

    /**
     * Getters for this custom data class
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public String getSource() {
        return source;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getCollection() {
        return collection;
    }

    /**
     * Setters for this custom data class
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
