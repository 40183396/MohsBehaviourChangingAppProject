package com.napier.mohs.instagramclone.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.napier.mohs.instagramclone.R;

/**
 * Created by Mohs on 18/03/2018.
 */

public class PasswordConfirmDialog extends DialogFragment{

    private static final String TAG = "PasswordConfirmDialog";

    TextView mPassword;

    // interface made to make dialog reusable
    public interface OnPasswordConfirmListener{
        public void onPasswordConfirm(String password);
    }
    OnPasswordConfirmListener mOnPasswordConfirmListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_passwordconfirm, container, false); // inflates password confirm dialog
        mPassword = (TextView) view.findViewById(R.id.edittextPasswordConfirmPassword);
        Log.d(TAG, "onCreateView: Password Confirm Dialog Created");

        TextView confirm = (TextView) view.findViewById(R.id.textviewPasswordConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Confirm pressed, password changed and updated to db ");
                String password = mPassword.getText().toString();
                // checks enter password is not null
                if(!password.equals("")){
                    mOnPasswordConfirmListener.onPasswordConfirm(password); // activates interface
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please enter a password to confirm", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.textviewPasswordCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Cancelled pressed, dialog is closed ");

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mOnPasswordConfirmListener = (OnPasswordConfirmListener) getTargetFragment(); // goes straight to edit profile fragment and skips going to main activity part
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
