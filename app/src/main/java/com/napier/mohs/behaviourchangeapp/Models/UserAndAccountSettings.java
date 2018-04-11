package com.napier.mohs.behaviourchangeapp.Models;

/**
 * Created by Mohs on 18/03/2018.
 *
 * This allows a model of more than one node from database to be retrieved
 * Allows us to do it in one query
 * Holds settings of User and AccountSettings
 */

public class UserAndAccountSettings {
    private User user;
    private AccountSettings mAccountsettings;

    public UserAndAccountSettings(User user, AccountSettings accountsettings) {
        this.user = user;
        this.mAccountsettings = accountsettings;
    }

    public UserAndAccountSettings() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountSettings getAccountsettings() {
        return mAccountsettings;
    }

    public void setAccountsettings(AccountSettings accountsettings) {
        this.mAccountsettings = accountsettings;
    }

    @Override
    public String toString() {
        return "UserAndAccountSettings{" +
                "user=" + user +
                ", mAccountsettings=" + mAccountsettings +
                '}';
    }
}
