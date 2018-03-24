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


import com.napier.mohs.instagramclone.Models.Exercise;
import com.napier.mohs.instagramclone.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 23/03/2018.
 */

public class AdapterExerciseList extends ArrayAdapter<Exercise>{
    private static final String TAG = "AdapterExerciseList";

    private LayoutInflater mLayoutInflater;
    private int mLayoutResource;
    private Context mContext;


    public AdapterExerciseList(@NonNull Context context, int resource, @NonNull List<Exercise> objects) {
        super(context, resource, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mLayoutResource = resource;
    }

    static class ViewHolder{
        @BindView(R.id.textviewExercisesName)
        TextView name;

        @BindView(R.id.textviewExercisesWeight)
        TextView weight;

        @BindView(R.id.textviewExercisesReps)
        TextView reps;

        @BindView(R.id.imageExercises)
        CircleImageView picture;

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

        // set name and number
        viewHolder.name.setText((getItem(position).getExercise_name()));
        viewHolder.weight.setText(getItem(position).getExercise_weight() + " kgs");
        viewHolder.reps.setText(getItem(position).getExercise_reps() + " reps");



        return convertView;
    }



}
