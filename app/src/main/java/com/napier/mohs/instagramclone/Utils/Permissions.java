package com.napier.mohs.instagramclone.Utils;


import android.Manifest;

/**
 * Created by Mohs on 18/03/2018.
 */

public class Permissions {

    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] PERMISSION_CAMERA = {
            Manifest.permission.CAMERA
    };

    public static final String[] PERMISSION_WRITE_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
