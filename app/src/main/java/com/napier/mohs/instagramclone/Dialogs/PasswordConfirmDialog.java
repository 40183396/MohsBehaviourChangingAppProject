package com.napier.mohs.instagramclone.Dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 18/03/2018.
 */

public class PasswordConfirmDialog extends DialogFragment{

    private static final String TAG = "PasswordConfirmDialog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_passwordconfirm, container, false); // inflates password confirm dialog
        Log.d(TAG, "onCreateView: Password Confirm Dialog Created");

        return view;
    }
}
