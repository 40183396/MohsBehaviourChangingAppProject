package com.napier.mohs.behaviourchangeapp.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.napier.mohs.behaviourchangeapp.Models.User;
import com.napier.mohs.behaviourchangeapp.Profile.ActivityProfile;
import com.napier.mohs.behaviourchangeapp.R;
import com.napier.mohs.behaviourchangeapp.Utils.AdapterUserList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Mohs on 15/03/2018.
 */

public class FragmentSearch extends Fragment {
    private static final String TAG = "FragmentSearch";

    @BindView(R.id.edittextSearch)
    EditText mSearch;
    @BindView(R.id.listviewSearch)
    ListView mListView;

    private List<User> mUsersList;
    private Context mContext;

    // global adapter
    private AdapterUserList mUserListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homesearch, container, false);
        Log.d(TAG, "onCreate: started");
        ButterKnife.bind(this, view);
        mContext = getActivity(); // keeps context constant

        initialiseTextListener();
        hideKeyboard();
        return view;
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
        if(getActivity().getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); // hides keyboard after user has finished typing
            Log.d(TAG, "hideKeyboard: keyboard hidden");
        }
    }

    private void usersListUpdate(){
        Log.d(TAG, "usersListUpdate: the userlist is being updated");

        // sets adapter with users list item layout and user list data
        mUserListAdapter = new AdapterUserList(getActivity(), R.layout.listitem_users, mUsersList);

        // sets adapter on list view
        mListView.setAdapter(mUserListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemClick: user selected " + mUsersList.get(position).toString());
                // navigating to the users profile
                Intent intent = new Intent(getActivity(), ActivityProfile.class);
                // need extra to differenitate between viewing own profile and other users profiles
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity)); // this is where we put activity name we are coming from, here it is search activity
                intent.putExtra(getString(R.string.user_extra), mUsersList.get(position)); // make sure User class is parcelable
                startActivity(intent);
            }
        });
    }
}
