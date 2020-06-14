package com.java.datingapp.model;

import java.util.List;
import java.util.Objects;

public class User {
    private String name;
    private String gender;
    private int age;
    private List<String> interests;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", interests=" + interests +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age &&
                Objects.equals(name, user.name) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(interests, user.interests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender, age, interests);
    }


    public User() {
    }
}
