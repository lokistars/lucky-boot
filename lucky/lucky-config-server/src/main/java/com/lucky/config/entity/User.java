package com.lucky.config.entity;

import java.io.Serializable;

/**
 * @author: Loki
 * @data: 2021-12-03 20:57
 **/
public class User implements Serializable {

    private static final long serialVersionUID = -5179523762034025860L;

    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
