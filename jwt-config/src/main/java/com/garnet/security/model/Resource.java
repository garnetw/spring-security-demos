package com.garnet.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Table;

@Data
@AllArgsConstructor
@Table(name = "resource")
public class Resource {

    private int id;

    private String path;

    private String name;

    private int type;

    public Resource(String path, String name) {
        this.path = path;
        this.name = name;
        this.type = 0;
    }

    public Resource(String path, String name, int type) {
        this.path = path;
        this.name = name;
        this.type = type;
    }
}
