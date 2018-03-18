package com.napier.mohs.instagramclone.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Dialogs.PasswordConfirmDialog;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.Models.UserSettings;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.FirebaseMethods;
import com.napier.mohs.instagramclone.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 6/4/2017.
 */

public class EditProfileFragment extends Fragment implements PasswordConfirmDialog.OnPasswordConfirmListener{

    private static final String TAG = "EditProfileFragment";

    // Firebase Stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myDBRefFirebase;
    private FirebaseMethods mFirebaseMethods;

    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    private UserSettings mUserSetings;
    private String userID;


    // override method for password confirm dialog
    @Override
    public void onPasswordConfirm(String password) {
        Log.d(TAG, "onPasswordConfirm: password entered: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(),password);

        // user prompted to reenter sign in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");
                            
                            // check email already exists
                             mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                     if(task.isSuccessful()){
                                         try{
                                         // if size eqauls 1 we have retrieved something
                                         if(task.getResult().getProviders().size() == 1){
                                             Log.d(TAG, "onComplete: Email already used");
                                             Toast.makeText(getActivity(), "That email is Already being used", Toast.LENGTH_SHORT).show();
                                         }
                                         // if null, email is free to use
                                         else{
                                             Log.d(TAG, "onComplete: email available ");
                                             // email is updated
                                             mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()) {
                                                                 Log.d(TAG, "User email address updated.");
                                                                 Toast.makeText(getActivity(), "Email has been updated", Toast.LENGTH_SHORT).show();
                                                                 mFirebaseMethods.emailUpdate(mEmail.getText().toString());
                                                             }
                                                         }});
                                         }
                                         }
                                         catch (NullPointerException e){
                                             Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                         }
                                     }
                                 }
                             });
                             
                        } else {
                             Log.d(TAG, "onComplete: Failed to reauthenticate");
                        }
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.imageEditProfilePhoto);

        mDisplayName = (EditText) view.findViewById(R.id.edittextEditDisplayName);
        mUsername = (EditText) view.findViewById(R.id.edittextEditUsername);
        mWebsite = (EditText) view.findViewById(R.id.edittextEditWebsite);
        mDescription = (EditText) view.findViewById(R.id.edittextEditDescription);
        mEmail = (EditText) view.findViewById(R.id.edittextEditEmail);
        mPhoneNumber = (EditText) view.findViewById(R.id.edittextEditPhoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.textviewEditChangeProfilePhoto);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDBRefFirebase = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(getActivity());


        //setProfileImage();

        setupFirebaseAuth();

        // back arrow which goes to profile activity
        ImageView backArrow = (ImageView) view.findViewById(R.id.imageEditBackArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going back to profile activity. ");
                getActivity().finish(); // Have to use getActivity().finish(); as we are in fragment
            }
        });


        ImageView save = (ImageView) view.findViewById(R.id.imageEditSaveChange);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: saving changes attempt ");
                saveEditSettings();
                //getActivity().finish(); // Have to use getActivity().finish(); as we are in fragment
            }
        });


        return view;

    }

    // gets data from widgets and uploads to db
    // Also checks that username is unique
    private void saveEditSettings() {
        final String displayname = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String web = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phone = Long.parseLong(mPhoneNumber.getText().toString());


        //Original search method for searching for username in db which was too slow
                /*User user = new User();
                for(DataSnapshot ds : dataSnapshot.child(getString(R.string.db_name_users)).getChildren()){
                    if(ds.getKey().equals(userID)){
                        user.setUsername(ds.getValue(User.class).getUsername());
                    }
                }*/

        // using query instead which checks username entered into text field and compare it to what was originally loaded into fragment
        Log.d(TAG, "onDataChange: current username is: " + mUserSetings.getUser().getUsername());

        // if username has nchanged
        if (!mUserSetings.getUser().getUsername().equals(username)) {
            checkUsernameExist(username);
        }

        //if email is changed
        if (!mUserSetings.getUser().getEmail().equals(email)) {

            // first reauthenticate email (only needed is emails have to be verified
            // check email is registered already
            // then email is changed
            PasswordConfirmDialog dialog = new PasswordConfirmDialog();
            dialog.show(getFragmentManager(), getString(R.string.dialog_password_confirm));
            dialog.setTargetFragment(EditProfileFragment.this, 1); // sets this as target fragment after dialog is opened

        }

        // if displayname is changed
        if(!mUserSetings.getUserAccountsettings().getDisplay_name().equals(displayname)){
            mFirebaseMethods.usersettingsUpdate(displayname, null, null, 0);
        }

        if(!mUserSetings.getUserAccountsettings().getWebsite().equals(web)){
            mFirebaseMethods.usersettingsUpdate(null, web, null, 0);
        }

        if(!mUserSetings.getUserAccountsettings().getDescription().equals(description)){
            mFirebaseMethods.usersettingsUpdate(null, null, description, 0);
        }




    }



    // checks if username is in db
    // using query means cant return anything (e.g. boolean if usename already exists)
    // Forced to run this and execute methods inside the override
    private void checkUsernameExist(final String username) {
        Log.d(TAG, "checkUsernameExist: Checking if this username exists already: " + username);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // looks for node that contains object that is being lookexd for then gets field in that object
        Query qry = ref
                .child(getString(R.string.db_name_users))
                .orderByChild(getString(R.string.username_field))
                .equalTo(username);
        qry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // only returns datasnapshot if match is foound
                if (!dataSnapshot.exists()) {
                    // username added
                    mFirebaseMethods.usernameUpdate(username);
                    Toast.makeText(getActivity(), "Username changed.", Toast.LENGTH_SHORT).show();
                }
                //loops through results
                // single snapshot as only one item from db is being returned
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    if (singleDataSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: username already exists in db: " + singleDataSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "Username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // Test method to display profil image without db
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: profile image is being set");
//        String imgURL = "http://cdn.newsapi.com.au/image/v1/9fdbf585d17c95f7a31ccacdb6466af9";
//        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "");
//
//        // if have 'www' use append as 'http://'
//        // String imgURL = "http://cdn.newsapi.com.au/image/v1/9fdbf585d17c95f7a31ccacdb6466af9";
//        // UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "");
//    }

    // sets up the edit profile page with data from db
    private void seupWidgets(UserSettings userSettings) {
        Log.d(TAG, "seupWidgets: settings up edit profile widgets with data from firebase db ");

        // when activity starts this user settings object is set
        mUserSetings = userSettings;

        User user = userSettings.getUser();
        UserAccountSettings userAccountSettings = userSettings.getUserAccountsettings();

        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfilePhoto, null, ""); // image loader for profile photo

        // sets up widgets with db data
        mDisplayName.setText(userAccountSettings.getDisplay_name());
        mUsername.setText(userAccountSettings.getUsername());
        mWebsite.setText(userAccountSettings.getWebsite());
        mDescription.setText(userAccountSettings.getDescription());
        mEmail.setText(String.valueOf(user.getEmail()));
        mPhoneNumber.setText(String.valueOf(user.getPhone_number()));
    }


    //------------------------FIRESBASE STUFF-------------
    // Method to check if a user is signed in app

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: firbase auth is being setup");
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: user signed in " + user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");
                }
            }
        };

        // allows to get datasnapshot and allows to read or write to db
        // This is always listening
        myDBRefFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrievs the user info from db
                seupWidgets(mFirebaseMethods.getUserSettings(dataSnapshot)); // retrieves datasnapshot of user settings and sets up widgets
                // retrievs images for the user
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
