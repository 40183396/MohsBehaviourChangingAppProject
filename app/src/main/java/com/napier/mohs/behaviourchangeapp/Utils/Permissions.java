package com.napier.mohs.behaviourchangeapp.Utils;


import android.Manifest;

/**
 * Created by Mohs on 18/03/2018.
 */

public class Permissions {

    // This is for gallery and camera, it checks they have these permissions before displaying them
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] PERMISSION_CAMERA = {
            Manifest.permission.CAMERA
    };

}
