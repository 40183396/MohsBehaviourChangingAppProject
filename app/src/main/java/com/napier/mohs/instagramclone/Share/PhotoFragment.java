package com.napier.mohs.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.napier.mohs.instagramclone.Profile.AccountSettingsActivity;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.Permissions;

/**
 * Created by Mohs on 18/03/2018.
 */

public class PhotoFragment extends Fragment{

    private static final String TAG = "PhotoFragment";
    private static final int PHOTOFRAGMENT_NUM = 1;
    private static final int GALLERYFRAGMENT_NUM = 2;
    private static final int REQUEST_CODE_CAMERA = 3; // does not matter what value this is, just added for consistency

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        Log.d(TAG, "onCreateView: photo fragment started");

        // Button which launches the camera
        Button buttonOpenCamera = (Button) view.findViewById(R.id.buttonPhotoOpenCamera);
        buttonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Camera is being launched");

                if(((ShareActivity)getActivity()).getTabCurrentNumber() == PHOTOFRAGMENT_NUM){

                    // Check if camera permission is verified
                    if(((ShareActivity)getActivity()).permissionsCheck(Permissions.PERMISSION_CAMERA[0])){
                        Log.d(TAG, "onClick: permission verified camera is being started");
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    } else {
                        // if permission not verified restart share activity
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        // set flags to intent to clear activity  stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

    // checks if there is a  root task
    private boolean isTaskRoot() {
        // if flag is 0 this means this is root task
        if (((ShareActivity) getActivity()).taskGet() == 0) {
            return true;
        } else {
            return false; // meaning this is not root task
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CAMERA){
            Log.d(TAG, "onActivityResult: photo is done being taken");
            Log.d(TAG, "onActivityResult: attempt navigation to share screen");

            // goes to share screen to have photo published
            // checks if task was root
            // retrieves a bitmap
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data"); // data is a keyword argument
            if(isTaskRoot()){

            } else {
                try{
                    Log.d(TAG, "onActivityResult: bitmap recieved from camera " + bitmap);
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.bitmap_selected), bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.fragment_edit_profile));
                    startActivity(intent);
                    getActivity().finish();
                } catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException " + e.getMessage());
                }
            }

        }
    }
}
