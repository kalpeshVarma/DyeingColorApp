package com.kalpv.todoapp;

public class ToDo {

    private String id, parentId, title, description;

    public ToDo(String id, String parentId, String title, String description) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.description = description;
    }

    public ToDo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
