package com.example.contact;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.Serializable;

public class Contact implements Serializable {
    private int id;
    private String name;
    private String phone;

    private String email;
    private String image;
    public Contact(){}

    public Contact(int id, String name, String phone, String email, String image) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean validate(Context context){
        if(TextUtils.isEmpty(this.name)){
            Toast.makeText(context, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(this.phone)){
            Toast.makeText(context, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(this.email)){
            Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
