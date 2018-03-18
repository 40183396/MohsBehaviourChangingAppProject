package com.napier.mohs.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.Models.UserSettings;
import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 17/03/2018.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private Context mContext;
    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private String userID;


    public FirebaseMethods(Context context) {
        Log.d(TAG, "setupFirebaseAuth: firebase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mContext = context;



        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    // method to check is username is already in use in db
    public boolean checkUsernameExists(String username, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkUsernameExists: checking if the user " + username + " is already in db");

        User user = new User();

        // Now loops through DataSnapshot to see if user exists
        // DataSnapshot goes through all nodes
        // 0th iteration only gets first node in this case user_acount settings
        // 1st would be users etc.
        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
            Log.d(TAG, "checkUsernameExists: DataSnapshot " + ds);

            //sets user to username
            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkUsernameExists: username: " + user.getUsername());

            // checks if username already exists by removing period and replacing with space and then comparing to String username
            if (ManipulateStrings.usernameRemovePeriod(user.getUsername()).equals(username)) {
                Log.d(TAG, "checkUsernameExists: username already exists: " + user.getUsername());
                return true;
            }
        }

        return false;
    }

    // registers the given email and pasword to firebase db
    public void newEmailRegister(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Because not in activty can not apply context here
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            // send verif email
                            sendVerEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "createUserWithEmail:success, userID: " + userID);
                            Toast.makeText(mContext, R.string.success_auth,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, R.string.failed_auth,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    // sends verification email to user
    public void sendVerEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            } else
                                Toast.makeText(mContext, "Verification email could not be sent", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    // this will add a user to the firebase db
    // it will include things such as website and profile pic
    // and user setting

    public void addNewUser(String email, String username, String description, String website, String profile_photo) {

        // Creates new user and adds to db
        // Removes spaces and adds periods to make usernames
        User user = new User( userID,  1,  email,  ManipulateStrings.usernameRemoveSpace(username));

        // call dbref look for child node users, look for child node user_id and add data to db
        myDBRefFirebase.child(mContext.getString(R.string.db_name_users))
                .child(userID)
                .setValue(user);

        Log.d(TAG, "addNewUser: username: " + user);

        // sets up user_account_settings
        UserAccountSettings userAccSettings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                ManipulateStrings.usernameRemoveSpace(username),
                website
        );
        Log.d(TAG, "addNewUser: userAccSettings: " + userAccSettings);
        myDBRefFirebase.child(mContext.getString(R.string.db_name_user_account_settings))
                .child(userID)
                .setValue(userAccSettings);

    }

    // gets user account settings from firebase db for user logged in
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings: getting user acc settings from firebase");

        UserAccountSettings userAccountSettings = new UserAccountSettings();
        User user = new User();

        // loops through all main parent nodes
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            // this if statement handle user_account_settings
            if (ds.getKey().equals(mContext.getString(R.string.db_name_user_account_settings))) {// if key equals user_account_settings look in that node
                Log.d(TAG, "getUserAccountSettings: DataSnapshot: " + ds); // used for ddebugging

                try {
                    userAccountSettings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    userAccountSettings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    userAccountSettings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    userAccountSettings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    userAccountSettings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    userAccountSettings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                    userAccountSettings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );
                    userAccountSettings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );
                    Log.d(TAG, "getUserAccountSettings: from user_account_settings: " + userAccountSettings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }


            }

            if (ds.getKey().equals(mContext.getString(R.string.db_name_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()
                );
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );

                Log.d(TAG, "getUserAccountSettings: from users: " + user.toString());
            }
        }
            return new UserSettings(user, userAccountSettings); // returns custom data model
        }
    }
