package com.devup.opointdoacai.opointdoacaiserver.Model;

import com.devup.opointdoacai.opointdoacaiserver.UserId;

public class Users extends UserId {

    String name;

    public Users() {
    }

    public Users(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
