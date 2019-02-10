package com.devup.opointdoacai.opointdoacaiserver.Model;

public class Juices {

    private String name;
    private String description;
    private String price;
    private String base;

    public Juices() {
    }

    public Juices(String name, String description, String price, String base) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.base = base;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
