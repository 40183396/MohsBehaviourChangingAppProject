package com.napier.mohs.instagramclone.Profile;

/**
 * Created by Mohs on 17/03/2018.
 */

public class Test {
    //package com.napier.mohs.instagramclone.Profile;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//
//import java.util.ArrayList;
//
//import com.napier.mohs.instagramclone.R;
//import com.napier.mohs.instagramclone.Utils.SectionsStatePagerAdapter;
//
//
///**
// * Created by Mohs on 16/03/2018.
// */
//
//public class AccountSettingsActivity extends AppCompatActivity{
//
//    private static final String TAG = "AccountSettingsActivity";
//
//    private Context mContext;
//
//    private SectionsStatePagerAdapter pagerAdapter;
//    private ViewPager mViewPager;
//    private RelativeLayout mRelativeLayout;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_accountsettings);
//        mContext = AccountSettingsActivity.this;
//        Log.d(TAG, "onCreate: started account settings activity");
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);
//
//        setupSettingsList();
//
//        // setup back arrow for navigating back to "Profile Activity"
//        ImageView imageAccountBackArrow = (ImageView) findViewById(R.id.imageAccountBackArrow);
//        imageAccountBackArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: navigating back to profile page");
//                finish();
//            }
//        });
//    }
//
//    private void setUpFragments(){
//        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
//        // adds fragments to pagerAdapter
//        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.fragment_edit_profile));
//        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.fragment_sign_out));
//    }
//
//    private void setupSettingsList(){
//        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
//        ListView listview = (ListView) findViewById(R.id.listviewAccountSettings);
//
//        ArrayList<String> options = new ArrayList<>();
//        options.add(getString(R.string.fragment_edit_profile));
//        options.add(getString(R.string.fragment_sign_out));
//
//        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
//        listview.setAdapter(adapter);
//    }
//}


  //  public class EditProfileFragment extends Fragment {
//
//    private static final String TAG = "EditProfileFragment";
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
//
//        return view;
//    }
//}

//    public class EditProfileFragment extends Fragment {
//
//    private static final String TAG = "EditProfileFragment";
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
//
//        return view;
//    }
//}
}
