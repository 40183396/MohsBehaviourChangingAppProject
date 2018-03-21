package com.napier.mohs.instagramclone.Utils;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.Like;
import com.napier.mohs.instagramclone.Models.Photo;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 21/03/2018.
 */

public class MainFeedListAdapter extends ArrayAdapter<Photo> {
    private static final String TAG = "MainFeedListAdapter";

    private LayoutInflater mLayoutInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private String currentUsername = "";

    public MainFeedListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Photo> objects) {
        super(context, resource, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
    }

    static class ViewHolder{
        TextView comments;
        CircleImageView imageProfile;
        TextView username;
        TextView timestamp;
        TextView caption;
        TextView likes;
        ImageView yellowStar, hollowStar, speechBubble;
        String stringLikes;
        ImagesSquaredView imagePost;

        // we are saving sopecific things for each post in this view holder
        UserAccountSettings mUserAccountSettings = new UserAccountSettings();
        User mUser = new User();
        StringBuilder usersStringBuilder;
        String mStringLikes;
        boolean likedCurrentUser;
        Star star;
        GestureDetector mGestureDetector;
        Photo mPhoto;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = mLayoutInflater.inflate(mLayoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.comments = (TextView) convertView.findViewById(R.id.textviewMainFeedComments);
            viewHolder.username = (TextView) convertView.findViewById(R.id.textviewMainFeedUsername);
            viewHolder.timestamp = (TextView) convertView.findViewById(R.id.textviewMainFeedTimeStamp);
            viewHolder.caption = (TextView) convertView.findViewById(R.id.textviewMainFeedCaption);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.textviewMainFeedLikes);

            viewHolder.yellowStar = (ImageView) convertView.findViewById(R.id.imageMainFeedStarYellow);
            viewHolder.hollowStar = (ImageView) convertView.findViewById(R.id.imageMainFeedStarHollow);
            viewHolder.speechBubble = (ImageView) convertView.findViewById(R.id.imageMainFeedSpeechBubble);

            viewHolder.imagePost = (ImagesSquaredView) convertView.findViewById(R.id.imageMainFeedPostPicture);

            viewHolder.imageProfile = (CircleImageView) convertView.findViewById(R.id.imageMainFeedProfile);

            viewHolder.star = new Star(viewHolder.yellowStar, viewHolder.hollowStar);
            viewHolder.mPhoto = getItem(position);
            viewHolder.mGestureDetector = new GestureDetector(mContext, new GestureListener(viewHolder));
            viewHolder.usersStringBuilder = new StringBuilder();

            // set tag  on the vonvert view
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        ViewHolder mViewHolder;
        // we dont have global photo soo we create a default constructor and pass the view holder
        public GestureListener(ViewHolder viewHolder){
            mViewHolder = viewHolder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: ");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: ");

            // Test to see if star was working
            // mStar.likeToggle();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference
                    .child(mContext.getString(R.string.db_name_photos)) // looks in photos node
                    .child(mViewHolder.mPhoto.getPhoto_id()) // looks for photo_id of photo
                    .child(mContext.getString(R.string.likes_field)); // checks likes field
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleDataSnapshot.getKey();
                        //  user liked photo already
                        if(mViewHolder.likedCurrentUser &&
                                singleDataSnapshot.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){ // this makes sure we are removing a like for the current user only

                            // removes like from photos node
                            mDatabaseReference.child(mContext.getString(R.string.db_name_photos))
                                    .child(mViewHolder.mPhoto.getPhoto_id())
                                    .child(mContext.getString(R.string.likes_field))
                                    .child(keyID) // gets key of id
                                    .removeValue();

                            // removes like from user photos node
                            mDatabaseReference.child(mContext.getString(R.string.db_name_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //current users user_id
                                    .child(mViewHolder.mPhoto.getPhoto_id())
                                    .child(mContext.getString(R.string.likes_field))
                                    .child(keyID) // gets key of id
                                    .removeValue();

                            mViewHolder.star.likeToggle();
                            getStringLikes(mViewHolder);
                        }
                        // user has not liked photo
                        else if(!mViewHolder.likedCurrentUser){
                            // add a like to db
                            addLikeNew(mViewHolder);
                            Log.d(TAG, "onDataChange: new like added");
                            break;
                        }

                    }

                    // checks if datasnapshot does not exist
                    if(!dataSnapshot.exists()){
                        // new like is added to db
                        Log.d(TAG, "onDataChange: datasnapshot did not exist, new like added");
                        addLikeNew(mViewHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    // need to pass viewholder to reference the objects
    private void addLikeNew(final ViewHolder viewHolder){
        Log.d(TAG, "addLikeNew: adding a new like");

        String newLikeID = mDatabaseReference.push().getKey(); // creates a new key
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // adds like to photos node
        mDatabaseReference.child(mContext.getString(R.string.db_name_photos))
                .child(viewHolder.mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.likes_field))
                .child(newLikeID) // gets key of id
                .setValue(like);

        // adds like to user photos node
        mDatabaseReference.child(mContext.getString(R.string.db_name_user_photos))
                .child(viewHolder.mPhoto.getUser_id()) //current users user_id
                .child(viewHolder.mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.likes_field))
                .child(newLikeID) // gets key of id
                .setValue(like);

        viewHolder.star.likeToggle();
        getStringLikes(viewHolder);
    }

    private void getStringLikes(final ViewHolder viewHolder){
        Log.d(TAG, "getStringLikes: getting likes");


        try{



        // Test to see if star was working
        // mStar.likeToggle();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // query that checks if the photo has any likes
        Query query = databaseReference
                .child(mContext.getString(R.string.db_name_photos)) // looks in photos node
                .child(viewHolder.mPhoto.getPhoto_id()) // looks for photo_id of photo
                .child(mContext.getString(R.string.likes_field)); // checks likes field
        query.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewHolder.usersStringBuilder = new StringBuilder();
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    // user liked photo already

                    // user has not liked photo
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    // query that checks if the photo has any likes
                    Query query = databaseReference
                            .child(mContext.getString(R.string.db_name_users)) // looks in users node
                            .orderByChild(mContext.getString((R.string.user_id_field))) // looks for particular user_id
                            .equalTo(singleDataSnapshot.getValue(Like.class).getUser_id()); // checks likes field
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { // this is only called if a like is found
                            // if we we find anyything we want to append those found strings
                            for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                                // user liked photo already we loop and add user to string builder
                                Log.d(TAG, "onDataChange: like has been found " + singleDataSnapshot.getValue(User.class).getUsername());
                                // user is appended to string builder
                                viewHolder.usersStringBuilder.append(singleDataSnapshot.getValue(User.class).getUsername());
                                // append comma to make it easier to handle data
                                viewHolder.usersStringBuilder.append(",");

                            }
                            // when loop done we have to split users where we have comma
                            String[] usersSplit = viewHolder.usersStringBuilder.toString().split(",");

                            //deteermine if current user has liked photo or not
                            if(viewHolder.usersStringBuilder.toString().contains(viewHolder.mUser.getUsername() + ",")){ // needs comma otherwise there is a problem when multiple users like it
                                // means user has liked photo
                                viewHolder.likedCurrentUser = true;
                            } else {
                                viewHolder.likedCurrentUser = false;
                            }

                            int length = usersSplit.length;
                            // different cases for number of users who liked photo determines how like comment is displayed
                            if(length == 1){ // only one user likes photo etc.
                                viewHolder.mStringLikes = "Liked by " + usersSplit[0];
                            } else if (length == 2){
                                viewHolder.mStringLikes = "Liked by " + usersSplit[0] +
                                        " and " + usersSplit[1];
                            }else if (length == 3){
                                viewHolder.mStringLikes = "Liked by " + usersSplit[0] +
                                        ", " + usersSplit[1] +
                                        " and " + usersSplit[2];
                            }else if (length == 4){
                                viewHolder.mStringLikes = "Liked by " + usersSplit[0] +
                                        ", " + usersSplit[1] +
                                        ", " + usersSplit[2] +
                                        " and " + usersSplit[3];
                            }else if (length > 4){
                                viewHolder.mStringLikes = "Liked by " + usersSplit[0] +
                                        ", " + usersSplit[1] +
                                        ", " + usersSplit[2] +
                                        " and " +  (usersSplit.length - 3) + " others"; // -3 as we have already 3 users displayed
                            }
                            //likes string set up here
                            likesStringSetup(viewHolder, viewHolder.mStringLikes);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                // if no users have liked photo
                if(!dataSnapshot.exists()){
                    viewHolder.mStringLikes = "";
                    viewHolder.likedCurrentUser = false;
                    //likes string set up here
                    likesStringSetup(viewHolder, viewHolder.mStringLikes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        } catch (NullPointerException e){
            Log.e(TAG, "getStringLikes: NullPointerException e" + e.getMessage() );
            viewHolder.mStringLikes = "";
            viewHolder.likedCurrentUser = false;
            //likes string set up here
        }
    }

    // sets up the strings of like of a photo
    private void likesStringSetup(final ViewHolder viewHolder, String likesString){
        Log.d(TAG, "likesStingeSetup: string of likes: " + viewHolder.mStringLikes);

        // if photo is like by current user
        if(viewHolder.likedCurrentUser){
            Log.d(TAG, "likesStingeSetup: current user likes photo");
            viewHolder.hollowStar.setVisibility(View.GONE);
            viewHolder.yellowStar.setVisibility(View.VISIBLE);
            viewHolder.yellowStar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return viewHolder.mGestureDetector.onTouchEvent(motionEvent);
                }
            });
        } else { // if photo is not liked by current user
            Log.d(TAG, "likesStingeSetup: current user does not like hpoto");
            viewHolder.hollowStar.setVisibility(View.VISIBLE);
            viewHolder.yellowStar.setVisibility(View.GONE);
            viewHolder.hollowStar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return viewHolder.mGestureDetector.onTouchEvent(motionEvent);
                }
            });
        }

        viewHolder.likes.setText(likesString); // sets up the likes string here
    }

    // returns date string that shows how many days ago the post was made
    private String getDateTimeStampDifference(Photo photo){
        Log.d(TAG, "getDateTimeStampDifference: retrieving date timestamp");

        String difference = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Date timestamp;
        Date today = calendar.getTime();
        simpleDateFormat.format(today); // need to format date object and convert to string
        final String timestampPhoto = photo.getDate_created();
        try{
            timestamp = simpleDateFormat.parse(timestampPhoto);
            difference = String.valueOf(Math.round(((today.getTime() -  timestamp.getTime()) / 1000 / 60 / 60 / 24))); // works out number of days, getTime() converts string to long
            Log.d(TAG, "getDateTimeStampDifference: timestamp: " + difference);
        } catch(ParseException e){
            Log.e(TAG, "getDateTimeStampDifference: ParseException" + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

}
