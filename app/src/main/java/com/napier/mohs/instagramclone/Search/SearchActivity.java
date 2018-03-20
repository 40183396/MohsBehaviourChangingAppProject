package com.napier.mohs.instagramclone.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;
import com.napier.mohs.instagramclone.Utils.UserListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mohs on 15/03/2018.
 *
 * Activity that searches for other users
 */

public class SearchActivity extends AppCompatActivity{
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = SearchActivity.this;

    private EditText mSearch;
    private ListView mListView;

    private List<User> mUsersList;

    // global adapter
    private UserListAdapter mUserListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: started");
        mSearch = (EditText) findViewById(R.id.edittextSearchSearch);
        mListView = (ListView) findViewById(R.id.listviewSearch);

        hideKeyboard();
        setupBottomNavigationView();
        initialiseTextListener();
    }

    private void initialiseTextListener(){
        Log.d(TAG, "initialiseTextListener: ");

        mUsersList = new ArrayList<>();
        mSearch.addTextChangedListener(new TextWatcher() { // listener for when user types in keyboard
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // only occurs after text has been entered
            @Override
            public void afterTextChanged(Editable editable) {
                String text = mSearch.getText().toString().toLowerCase(Locale.getDefault());
                searchMatch(text);
            }
        });
    }

    public void searchMatch(String search){
        Log.d(TAG, "searchMatch: searching for " + search);
        mUsersList.clear();
        if(search.length() == 0){

        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child(getString(R.string.db_name_users))
                            .orderByChild(getString(R.string.username_field)).equalTo(search);
            // listener for search query
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: search has found user " + singleDataSnapShot.getValue(User.class).toString());

                        mUsersList.add(singleDataSnapShot.getValue(User.class));
                        // user list is updated
                        usersListUpdate();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void hideKeyboard(){
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // hides keyboard after user has finished typing
            Log.d(TAG, "hideKeyboard: keyboard hidden");
        }
    }

    private void usersListUpdate(){
        Log.d(TAG, "usersListUpdate: the userlist is being updated");

        // sets adapter with users list item layout and user list data
        mUserListAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_listitem_users, mUsersList);

        // sets adapter on list view
        mListView.setAdapter(mUserListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemClick: user selected " + mUsersList.get(position).toString());
                // navigating to the users profile
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM );
        menuItem.setChecked(true);
    }
}
