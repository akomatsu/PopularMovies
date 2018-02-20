package com.example.android.popularmovies.Data;

public class Trailer {

    private String id;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;
    private boolean placeholder;

    public Trailer() {
        placeholder = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public String toString() {
        return "ID: " + id + "\nKey: " + key + "\nName: " + name + "\nSite: " +
                site + "\nSize: " + size + "\nType: " + type;
    }
}
