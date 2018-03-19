package com.napier.mohs.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.napier.mohs.instagramclone.Profile.AccountSettingsActivity;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.FilePaths;
import com.napier.mohs.instagramclone.Utils.FileSearch;
import com.napier.mohs.instagramclone.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mohs on 18/03/2018.
 */

public class GalleryFragment extends Fragment{
    private static final String TAG = "GalleryFragment";
    private static final int NUM_COLS_GRID = 3;


    private GridView mGridView;
    private ImageView mGalleryImage;
    private ProgressBar mProgressBar;
    private Spinner mDirectorySpinner;

    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mImageSelected;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Log.d(TAG, "onCreateView: gallery fragment started");

        mGalleryImage = (ImageView) view.findViewById(R.id.imageGalleryImageView);
        mGridView = (GridView) view.findViewById(R.id.gridviewGallery);
        mDirectorySpinner = (Spinner) view.findViewById(R.id.spinnerGalleryDirectory);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbarGallery);
        mProgressBar.setVisibility(View.GONE);

        directories = new ArrayList<>();

        initialiseFolders();

        // Button to close gallery
        ImageView close = (ImageView) view.findViewById(R.id.imageGalleryClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: gallery fragment is being closed");
                getActivity().finish();
            }
        });

        // goes to share activty
        TextView next = (TextView) view.findViewById(R.id.textviewGalleryNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: going to share screen");

                // checks if task was root
                if(isTaskRoot()){
                    // Intent to go to Shared Activity
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.image_selected), mImageSelected);
                    startActivity(intent);
                } else {
                    // goes to account settings activity
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.image_selected), mImageSelected);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.fragment_edit_profile));
                    startActivity(intent);
                    // finishes activity so user cannot press back and return here from edit profile fragment
                    getActivity().finish();
                }


            }
        });
        return view;
    }

    // checks if there is a  root task
    private boolean isTaskRoot(){
        // if flag is 0 this means this is root task
    if(((ShareActivity)getActivity()).taskGet() == 0){
        return true;
    } else {
        return false; // meaning this is not root task
    }

    }

    private void initialiseFolders(){
        FilePaths filesPaths = new FilePaths();
        //checks directory '/storage/emulated/0/pictures' for other folders, this is default directory
        if(FileSearch.retrieveDirectoryPaths(filesPaths.PICTURES) != null){ // if there is directories inside this search make list of directories
            directories = FileSearch.retrieveDirectoryPaths(filesPaths.PICTURES); // list of directories in pictures directory
        }

        // ArrayList of formatted directory names
        ArrayList<String> namesDirectory = new ArrayList<>();
        for(int i = 0; i < directories.size(); i++){
            // looks for last index of forward slash
            int index = directories.get(i).lastIndexOf("/")+ 1; // + 1 gets rid of slash
            // substring to get very last bit of string
            String substringDirectories = directories.get(i).substring(index);
            // substring added to ArrayList
            namesDirectory.add(substringDirectories);
        }

        directories.add(filesPaths.CAMERA); // List of directories for camera added

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, namesDirectory); // displays formatted directory names
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDirectorySpinner.setAdapter(adapter);// sets up spinner with directories

        mDirectorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selected item: " + directories.get(position));

                // grid view is set up with images of selected directory
                gridViewSetup(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // sets up grid view with image files from directory
    private void gridViewSetup(String directorySelected){
        Log.d(TAG, "gridViewSetup: selected directory: " + directorySelected);
        final ArrayList<String> imgURLs = FileSearch.retrieveFilePaths(directorySelected);

        // integer which represents the grid width
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        // divides width of grid and divides by number of columns to get same width for each image
        int widthImage = widthGrid/NUM_COLS_GRID;
        mGridView.setColumnWidth(widthImage);

        // grid adapter is used to adapt images to grid view
        // append is //file:
       GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, imgURLs, mAppend);
       mGridView.setAdapter(adapter);

       // sets that is first displayed when fragment is inflated
        imageSet(imgURLs.get(0), mGalleryImage, mAppend);

        // sets first image as selected (default) when fragment is loaded
        mImageSelected = imgURLs.get(0);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "onItemClick: image selected: " + imgURLs.get(position));

                imageSet(imgURLs.get(position), mGalleryImage, mAppend);

                // every time image is clicked it becomes selected image
                mImageSelected = imgURLs.get(position);
            }
        });
    }

    private void imageSet(String imgURL, ImageView img, String append){
        Log.d(TAG, "imageSet: image is being set");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, img, new ImageLoadingListener() {
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
