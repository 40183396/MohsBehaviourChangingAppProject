package com.napier.mohs.instagramclone.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.Comment;
import com.napier.mohs.instagramclone.Models.Like;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.MainFeedListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohs on 15/03/2018.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";


    private ArrayList<String> mFollowingArrayList;
    private ArrayList<Photo> mPhotoArrayList; // holds all photos of people you are following
    private ArrayList<Photo> mPhotosPaginatedArrayList; // Photos that are added incrementally as you scroll through feed

    private ListView mListView;
    private MainFeedListAdapter mAdapter;
    private int mResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = (ListView) view.findViewById(R.id.listviewHome);
        mFollowingArrayList = new ArrayList<>();
        mPhotoArrayList = new ArrayList<>();


        getFollowing();
        return view;
    }

    // if we are following a user get his posts
    private void getFollowing() {
        Log.d(TAG, "getFollowing: seraching for who is following user");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // points to following node
        Query query = databaseReference
                .child(getString(R.string.db_name_following)) // looks in following node
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()); // checks if we have a match to the current users user id

        Log.d(TAG, "getPostDetails: query: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: User found: " + singleDataSnapshot.child(getString(R.string.user_id_field)).getValue());

                    mFollowingArrayList.add(singleDataSnapshot.child(getString(R.string.user_id_field)).getValue().toString()); //sets who we are following to array list
                }
                // this makes sure users own photos are added to following array list and main feed
                mFollowingArrayList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                // retrieve photos
                retrievePhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query has been cancelled");
            }
        });
    }

    private void retrievePhotos() {
        Log.d(TAG, "retrievePhotos: retrieving the photos ");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // need to loop through user id the user is following
        for (int i = 0; i < mFollowingArrayList.size(); i++) {
            final int count = i;
            // points to following node
            Query query = databaseReference
                    .child(getString(R.string.db_name_user_photos)) // looks in following node
                    .child(mFollowingArrayList.get(i))// i here is the user id
                    .orderByChild(getString(R.string.user_id_field)) // look in user id field
                    .equalTo(mFollowingArrayList.get(i)); // check if we have a match in array list

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {

                        // get photo
                        Photo photo = new Photo();
                        // set object map
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleDataSnapshot.getValue();
                        // set photo properties
                        photo.setCaption(objectMap.get(getString(R.string.caption_field)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.user_id_field)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.photo_id_field)).toString());
                        photo.setTags(objectMap.get(getString(R.string.tags_field)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.date_created_field)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.image_path_field)).toString());

                        // gets comments for photo
                        ArrayList<Comment> commentsArrayList = new ArrayList<Comment>();
                        for (DataSnapshot dataSnapshot1 : singleDataSnapshot
                                .child(getString(R.string.comments_field)).getChildren()) { // loop[ through all comments
                            Comment comment = new Comment();
                            comment.setUser_id(dataSnapshot1.getValue(Comment.class).getUser_id());
                            comment.setComment(dataSnapshot1.getValue(Comment.class).getComment());
                            comment.setDate_created(dataSnapshot1.getValue(Comment.class).getDate_created());
                            commentsArrayList.add(comment);
                        }

                        // sets comments for photo
                        photo.setComments(commentsArrayList);

                        // add photo to photo array list
                        mPhotoArrayList.add(photo);

                    }

                    // when we have all list items
                    if(count >= mFollowingArrayList.size() - 1){ // means we have reached end
                        // display photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query has been cancelled");
                }
            });

        }

    }

    // method to display the photos
    private void displayPhotos(){
        Log.d(TAG, "displayPhotos: attempting to display pictures ");
        mPhotosPaginatedArrayList = new ArrayList<>(); // instantiate pagination
        try{
            if(mPhotoArrayList != null){
                Log.d(TAG, "displayPhotos: Photo Array List is not null");
                // we want to sort the ArrayList
                Collections.sort(mPhotoArrayList, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo photo, Photo t1) {
                        return t1.getDate_created().compareTo(photo.getDate_created()); // sorting by date created
                    }
                });
                int iterations = mPhotoArrayList.size();
                if(iterations > 10 ){ // setting threshold for iterations at 10
                    iterations = 10; // set it to 10 if there are more than 10 to display
                }
                mResult = 10;
                for(int i = 0; i < iterations; i++){
                    // only want to add first 10 photos
                    mPhotosPaginatedArrayList.add(mPhotoArrayList.get(i));
                }
                // set up adapter with layout amd photo array list in paginated way (10 posts)
                mAdapter = new MainFeedListAdapter(getActivity(), R.layout.layout_listitem_mainfeed, mPhotosPaginatedArrayList);
                mListView.setAdapter(mAdapter); // sets list view with adapter
            }

        } catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
        } catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
        }
    }

    // if we want to display more photos when we have scrolled to bottom of list
    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: attempting to display more photos");
        try{
            if(mPhotoArrayList.size() > mResult && mPhotoArrayList.size() > 0){
                int iterations;
                // need to know if there are more than 10 photos
                if(mPhotoArrayList.size() > (mResult + 10)){ // means more than 10 photos
                    Log.d(TAG, "displayMorePhotos: there are more than 10 more photos");
                    iterations = 10; // same as before set to 10 if that is case
                } else{
                    Log.d(TAG, "displayMorePhotos: less than 10 more photos");
                    iterations = mPhotoArrayList.size() - mResult; // for example if there are 12 photos, iteration would become 2 so 2 more photos willl be displayed
                }

                // add new photos to paginated array list
                for(int i = mResult; i < mResult + iterations;  i++){
                    mPhotosPaginatedArrayList.add(mPhotoArrayList.get(i)); //adds photos to paginated array list
                }
                // reset results count
                mResult = mResult + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
        } catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
        }
    }
}
