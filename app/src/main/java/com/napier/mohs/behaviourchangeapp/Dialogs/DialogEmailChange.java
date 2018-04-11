package com.napier.mohs.behaviourchangeapp.Dialogs;

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

import com.napier.mohs.behaviourchangeapp.R;

/**
 * Created by Mohs on 18/03/2018.
 *
 * This class is only needed for email change user has to enter their password
 */

public class DialogEmailChange extends DialogFragment{

    private static final String TAG = "DialogEmailChange";

    TextView passwordConfirm;

    // interface made to make dialog reusable
    public interface OnEmailConfirmListener{
        public void onEmailConfirm(String password);
    }
    OnEmailConfirmListener mOnEmailConfirmListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_emailconfirm, container, false); // inflates password confirm dialog
        passwordConfirm = (TextView) view.findViewById(R.id.edittextEmailConfirmPassword);
        Log.d(TAG, "onCreateView: Password Confirm Dialog Created");

        TextView confirm = (TextView) view.findViewById(R.id.textviewEmailConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Confirm pressed, password changed and updated to db ");
                String password = passwordConfirm.getText().toString();
                // checks enter password is not null
                if(!password.equals("")){
                    mOnEmailConfirmListener.onEmailConfirm(password); // activates interface
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
            mOnEmailConfirmListener = (OnEmailConfirmListener) getTargetFragment(); // goes straight to edit profile fragment and skips going to main activity part
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
