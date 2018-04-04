package com.napier.mohs.behaviourchangeapp.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.napier.mohs.behaviourchangeapp.Models.Exercise;
import com.napier.mohs.behaviourchangeapp.Models.Goal;
import com.napier.mohs.behaviourchangeapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohs on 23/03/2018.
 */

public class AdapterGoalList extends ArrayAdapter<Goal>{
    private static final String TAG = "AdapterExerciseList";

    private LayoutInflater mLayoutInflater;
    private int mLayoutResource;
    private Context mContext;


    public AdapterGoalList(@NonNull Context context, int resource, @NonNull List<Goal> objects) {
        super(context, resource, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mLayoutResource = resource;
    }

    static class ViewHolder{
        @BindView(R.id.textviewGoalsCurrentWeight)
        TextView current;

        @BindView(R.id.textviewGoalsGoalWeight)
        TextView goal;

        @BindView(R.id.textviewGoalsExercisesName)
        TextView name;

        @BindView(R.id.progressbarGoals)
        NumberProgressBar progressbar;

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
        viewHolder.name.setText((getItem(position).getGoal_name()));
        viewHolder.goal.setText(getItem(position).getGoal_weight() + " kgs");
        viewHolder.current.setText(getItem(position).getCurrent_weight() + " kg");


        // to work out math for progressbar
        double goal =  Double.parseDouble(getItem(position).getGoal_weight());
        double current =  Double.parseDouble(getItem(position).getCurrent_weight());
        double percentage = (current/goal) * 100;
        int progress = (int) percentage;
        viewHolder.progressbar.setProgress(progress);


        return convertView;
    }



}
