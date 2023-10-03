package com.garnet.security.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@Table(name = "role")
public class Role implements Serializable {

    private int id;

    private String name;

    private String code;
}
