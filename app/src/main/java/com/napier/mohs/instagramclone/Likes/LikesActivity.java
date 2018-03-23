package com.napier.mohs.instagramclone.Likes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.napier.mohs.instagramclone.R;
import com.napier.mohs.instagramclone.Utils.BottomNavigationViewHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static android.graphics.Typeface.BOLD_ITALIC;

/**
 * Created by Mohs on 15/03/2018.
 */

public class LikesActivity extends AppCompatActivity{
    private static final String TAG = "LikesActivity";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = LikesActivity.this;

    String toastMsg = "Hello World!";

    @OnClick(R.id.button2)
    public void submit(View view) {
        new StyleableToast.Builder(this)
                .text(toastMsg)
                .textColor(Color.CYAN)
                .textBold()
                .show();
    }
    @OnClick(R.id.button3)
    public void submit1(View view) {
        new StyleableToast.Builder(this)
                .text(toastMsg)
                .textColor(Color.CYAN)
                .textBold()
                .show();
    }
    @OnClick(R.id.button4)
    public void submit2(View view) {
        new StyleableToast.Builder(this)
                .text(toastMsg)
                .cornerRadius(5)
                .show();
    }
    @OnClick(R.id.button5)
    public void submit3(View view) {
        new StyleableToast
                .Builder(mContext)
                .text("Hello world!")
                .textColor(Color.WHITE)
                .backgroundColor(Color.BLUE)
                .show();
    }
    @OnClick(R.id.button6)
    public void submit4(View view) {

    }
    @OnClick(R.id.button7)
    public void submit5(View view) {
        Drawable icon = getResources().getDrawable(R.drawable.ic_arrow);
        Toasty.normal(mContext, "Normal toast w/ icon", icon).show();
    }

    private CharSequence getFormattedMessage() {
        final String prefix = "Formatted ";
        final String highlight = "bold italic";
        final String suffix = " text";
        SpannableStringBuilder ssb = new SpannableStringBuilder(prefix).append(highlight).append(suffix);
        int prefixLen = prefix.length();
        ssb.setSpan(new StyleSpan(BOLD_ITALIC),
                prefixLen, prefixLen + highlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started");

        setupBottomNavigationView();
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM );
        menuItem.setChecked(true);
    }
}
