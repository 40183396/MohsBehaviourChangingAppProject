package com.napier.mohs.instagramclone.Utils;

import android.os.Environment;

/**
 * Created by Mohs on 18/03/2018.
 */

public class FilePaths {
    //public variables for file paths
    public String DIRECTORY_ROOT = Environment.getExternalStorageDirectory().getPath(); //"storage/emulated/0"

    public String PICTURES = DIRECTORY_ROOT + "/Pictures"; // Directory for pictures
    public String CAMERA = DIRECTORY_ROOT + "/DCIM/camera"; // Directory for camera

    public String IMAGE_FIREBASE_STORAGE = "photos/users/"; // Directory for where images are stored in firebase storage
}
