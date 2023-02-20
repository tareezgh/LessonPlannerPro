package com.example.lessonplannerpro;

public class Lesson {

    String subject;
    String topic;
    String date;
    String time;
    String username;
    String id;

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getID() {
        return id;
    }

    public void steID(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Lesson(String subject, String topic, String date, String time, String id, String username) {
        this.subject = subject;
        this.topic = topic;
        this.date = date;
        this.time = time;
        this.id = id;
        this.username = username;
    }

    public Lesson() {

    }


}
