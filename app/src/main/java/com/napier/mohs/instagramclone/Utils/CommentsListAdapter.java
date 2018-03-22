package com.napier.mohs.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.Comment;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 20/03/2018.
 */

public class CommentsListAdapter extends ArrayAdapter<Comment>{
    private static final String TAG = "CommentsListAdapter";

    private LayoutInflater mLayoutInflater;
    private int mLayoutResource;
    private Context mContext;

    public CommentsListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mLayoutResource = resource;
    }

    // View Holder build pattern for list view
    static class ViewHolder{ // using this instead of recycler view, does same thing with but with less code
        // load widgets into memory instead of loading them all at once

        @BindView(R.id.textviewCommentsComments) TextView comment;
        @BindView(R.id.textviewCommentsUsername) TextView username;
        @BindView(R.id.textviewCommentsTime) TextView timestamp;
        @BindView(R.id.textviewCommentsReply) TextView reply;
        @BindView(R.id.textviewCommentsLikes) TextView likes;
        @BindView(R.id.imageCommentLike) ImageView imageLike;
        @BindView(R.id.imageCommentsProfile) ImageView imageProfile;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view); // Butterknife For ViewHolder Pattern
        }


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){ // means we have a new view
            convertView = mLayoutInflater.inflate(mLayoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);

            // stores view in memory
            convertView.setTag(viewHolder);
        } else {
            // if view not null retrieve view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // comment set
        viewHolder.comment.setText((getItem(position).getComment()));

        // timestamp set
        String differenceTimestamp = getDateTimeStampDifference(getItem(position));
        if(!differenceTimestamp.equals("0")){
            viewHolder.timestamp.setText(differenceTimestamp + " d");
        } else {
            viewHolder.timestamp.setText("today");
        }

        // username and profile pic set
        Log.d(TAG, "getPostDetails: started");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // points to user id and retrieves their photos instead of going through db of all  photos
        Query query = databaseReference
                .child(mContext.getString(R.string.db_name_user_account_settings)) // looks in user_account_settings node
                .orderByChild(mContext.getString(R.string.user_id_field)) // looks for user_id field
                .equalTo(getItem(position).getUser_id()); // checks if photo user_id matches a user_id

        query.addListenerForSingleValueEvent(new ValueEventListener(){


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                    // sets username of comment
                    viewHolder.username.setText(singleDataSnapshot.getValue(UserAccountSettings.class).getUsername());

                    // sets profile picture of user of comment
                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(
                            singleDataSnapshot.getValue(UserAccountSettings.class).getProfile_photo(), viewHolder.imageProfile
                    );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query has been cancelled");
            }
        });

        // for first comment in comment thread of photo
        if(position == 0){
            // we set the visiibility of the imagelike, likes, reply widgets as GONE
            viewHolder.likes.setVisibility(View.GONE);
            viewHolder.imageLike.setVisibility(View.GONE);
            viewHolder.reply.setVisibility(View.GONE);
        }
        return convertView;
    }

    // returns date string that shows timestamp of comment
    private String getDateTimeStampDifference(Comment comment){
        Log.d(TAG, "getDateTimeStampDifference: retrieving date timestamp");

        String difference = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Date timestamp;
        Date today = calendar.getTime();
        simpleDateFormat.format(today); // need to format date object and convert to string
        final String timestampPhoto = comment.getDate_created();
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

    private void getCommentDetails() {

    }

}
