package com.napier.mohs.behaviourchangeapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mohs on 17/03/2018.
 */

public class User  implements Parcelable{

    // text has to match exactly to what is in firebase
    private String user_id, email, username;
    private long phone_number; // fb stores numbers as longs

    public User(String user_id, long phone_number, String email, String username) {
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.email = email;
        this.username = username;
    }

    // have to have empty constructor to work
    public User(){

    }

    protected User(Parcel in) {
        user_id = in.readString();
        email = in.readString();
        phone_number = in.readLong();
        username = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Do not change or won't work, let Android Studio auto generate for you the getters and setters
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(email);
        parcel.writeLong(phone_number);
        parcel.writeString(username);
    }
}

