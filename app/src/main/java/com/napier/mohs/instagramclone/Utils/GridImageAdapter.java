package com.napier.mohs.instagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.napier.mohs.instagramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Mohs on 17/03/2018.
 *
 * Custom method for adapting images to grid view
 */

public class GridImageAdapter extends ArrayAdapter{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int layoutResource;
    private String mAppend; // using image loader in grid layout, for image loader to work need append
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, ArrayList<String> imgURLs, String append) { // set append at end to make it similar to static method
        super(context, layoutResource, imgURLs);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
    }

    // reusing view holder build pattern to view images
    private static class ViewHolder{
        // makes images in grid squared
        ImagesSquaredView gridImages; // need to remake this otherwise gird images will be stretched and skewed
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            // viewHolder doesn't load all images at once only a few so app is faster
            // Similar to a recylcer view but easier to set up
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.profileProgressBar);
            viewHolder.gridImages = (ImagesSquaredView) convertView.findViewById(R.id.gridImageVIew);

            convertView.setTag(viewHolder); // Tag is a way to store widgets (View) in memory so app does not slow down
            // ViewHolder creates all widgets
            // Tag will hold entire ViewHolder
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String imgURL = (String) getItem(position); // this is why we use super so we have access to getItem

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(mAppend + imgURL, viewHolder.gridImages, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageURL, View view) {
                if(viewHolder.mProgressBar != null){
                    viewHolder.mProgressBar.setVisibility(View.VISIBLE); // This is when loading has started so want progress bar visible
                }
            }

            @Override
            public void onLoadingFailed(String imageURL, View view, FailReason failReason) {
                if(viewHolder.mProgressBar != null){
                    viewHolder.mProgressBar.setVisibility(View.GONE); // This is when loading has failed so want progress bar invisible
                }
            }

            @Override
            public void onLoadingComplete(String imageURL, View view, Bitmap loadedImage) {
                if(viewHolder.mProgressBar != null){
                    viewHolder.mProgressBar.setVisibility(View.GONE); // This is when loading has completed so want progress bar invisible
                }
            }

            @Override
            public void onLoadingCancelled(String imageURL, View view) {
                if(viewHolder.mProgressBar != null){
                    viewHolder.mProgressBar.setVisibility(View.GONE); // This is when loading has canceled so want progress bar invisible
                }
            }
        });
        return convertView;
    }
}
