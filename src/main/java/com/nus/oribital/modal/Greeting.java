// filepath: c:\Coding Practice\spring\oribital\src\main\java\com\nus\oribital\modal\Greeting.java
package com.nus.oribital.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "greetings")
public class Greeting {
    @Id
    private String id;
    private String title;
    private String message;

    public Greeting() {}

    public Greeting(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
}