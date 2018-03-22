package com.napier.mohs.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.napier.mohs.instagramclone.Models.User;
import com.napier.mohs.instagramclone.Models.UserAccountSettings;
import com.napier.mohs.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 20/03/2018.
 */

public class UserListAdapter extends ArrayAdapter<User>{
    private static final String TAG = "UserListAdapter";

    private LayoutInflater mLayoutInflater;
    private List<User> mUsers;
    private int mLayoutResource;
    private Context mContext;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mUsers = objects;
    }


    // View holder holds these items
    static  class ViewHolder{
        // attach widgets to their relative id's
        @BindView(R.id.textviewUsersListItemUsername) TextView username;
        @BindView(R.id.textviewUsersListItemEmail) TextView email;
        @BindView(R.id.imageUsersListItemProfile) CircleImageView imageProfile;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view); // Butterknife For ViewHolder Pattern
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        // view holder build pattern
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mLayoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);

          convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.username.setText(getItem(position).getUsername());
        viewHolder.email.setText(getItem(position).getEmail());

        // Need to reference database for image as it is in different node

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child(mContext.getString(R.string.db_name_user_account_settings)) // look in user_account_settings node
                .orderByChild(mContext.getString(R.string.user_id_field)) // look in user_id node
                .equalTo(getItem(position).getUser_id()); // check if user_id matches one in viewholder
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                for (DataSnapshot singleDataSnapShot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: user found: " + singleDataSnapShot.getValue(UserAccountSettings.class).toString());

                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(singleDataSnapShot.getValue(UserAccountSettings.class).getProfile_photo(),
                            viewHolder.imageProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }
}
