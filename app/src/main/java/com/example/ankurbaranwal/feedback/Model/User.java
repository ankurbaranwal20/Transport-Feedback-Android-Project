package com.example.ankurbaranwal.feedback.Model;

import com.example.ankurbaranwal.feedback.MainActivity;

public class User
{
    private String name,password,mobile;

    public User(String name, String password, String mobile) {
        this.name = name;
        this.password = password;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
