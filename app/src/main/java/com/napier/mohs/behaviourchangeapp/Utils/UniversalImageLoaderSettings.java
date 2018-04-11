package com.napier.mohs.behaviourchangeapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.napier.mohs.behaviourchangeapp.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Mohs on 17/03/2018.
 *
 * Initialises Image Loader. Makes Instance Available Anywhere In App.
 * Settings Here Are Available App Widfe.
 */

public class UniversalImageLoaderSettings {

    private static final int defaultImage = R.drawable.ic_android;
    private Context mContext;

    public UniversalImageLoaderSettings(Context context) {
        mContext = context;
    }

    public ImageLoaderConfiguration getConfig(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage) // default image
                .showImageForEmptyUri(defaultImage)
                .considerExifParams(true) // if image is rotated, roatates to original oreienatation
                .showImageOnFail(defaultImage) // if image is unable to load, can handle null inputs
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions) // passes default options declareed above
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        return configuration;
    }

    // static  method for setting image, only good for setting single image on a layout
    // not for images getting changed in activity or fragment, or if they are being set in grid layouts or list view etc
    public static void setImage(String imgURL, ImageView image, final ProgressBar mProgressBar, String append){
        // imgURL for example would be 'site.com/image.png' from web,
        // append would be for example 'http://' , 'file://', 'content://', 'assets://', 'drawable://' etc
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(mProgressBar!= null){
                    mProgressBar.setVisibility(View.VISIBLE); // This is when loading has started so want progress bar visible
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(mProgressBar!= null){
                    mProgressBar.setVisibility(View.GONE); // This is when loading has started so want progress bar visible
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(mProgressBar!= null){
                    mProgressBar.setVisibility(View.GONE); // This is when loading has started so want progress bar visible
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(mProgressBar!= null){
                    mProgressBar.setVisibility(View.GONE); // This is when loading has started so want progress bar visible
                }
            }
        });
    }
}
