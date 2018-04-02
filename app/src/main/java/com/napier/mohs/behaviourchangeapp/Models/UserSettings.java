package com.napier.mohs.behaviourchangeapp.Models;

/**
 * Created by Mohs on 18/03/2018.
 *
 * This allows a model of more than one node from database to be retrieved
 * Allows us to do it in one query
 * Holds settings of User and UserAccountSettings
 */

public class UserSettings {
    private User user;
    private UserAccountSettings userAccountsettings;

    public UserSettings(User user, UserAccountSettings userAccountsettings) {
        this.user = user;
        this.userAccountsettings = userAccountsettings;
    }

    public UserSettings() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getUserAccountsettings() {
        return userAccountsettings;
    }

    public void setUserAccountsettings(UserAccountSettings userAccountsettings) {
        this.userAccountsettings = userAccountsettings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user +
                ", userAccountsettings=" + userAccountsettings +
                '}';
    }
}
