package com.napier.mohs.behaviourchangeapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Mohs on 19/03/2018.
 */

public class ManageImages {
    private static final String TAG = "ManageImages";


    public static Bitmap getBtm(String imgURL){
        File imgFile = new File(imgURL); // creating file from pointer of image url in memory
        FileInputStream fileInputStream = null; // create file input stream
        Bitmap bitmap = null;
        try{
            fileInputStream = new FileInputStream(imgFile); // importing image file into project
            bitmap = BitmapFactory.decodeStream(fileInputStream); // convert to bitmap using bitmap factory and decode input stream
        } catch (FileNotFoundException e){
            Log.e(TAG, "getBtm: FileNotFoundException: " + e.getMessage() );
        } finally {
            try{
                fileInputStream.close(); // closing input stream
            }catch (IOException e){
                Log.e(TAG, "getBtm: IOException: " + e.getMessage() );
            }
        }
        return bitmap; // returns converted bitmap
    }

    // method that changes bitmap to array of bytes, quality is > 0 but < 100
    public static byte[] getBytesOfBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // choose quality and format
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream); //quality here for example could be 50 for 50% or 90  for 90% etc.

        return stream.toByteArray(); // returns byte array
    }
}
