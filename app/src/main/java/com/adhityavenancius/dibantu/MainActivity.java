package com.adhityavenancius.dibantu;




import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    SessionManager session;
    BottomNavigationView navigation;

    // masalah di import actionbar fix
    android.support.v7.app.ActionBar toolbar;

    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        toolbar = getSupportActionBar();
        // load the store fragment by default
        toolbar.setTitle("Category");
        loadFragment(new CategoryFragment(),true);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_category:
                    toolbar.setTitle("Category");
                    loadFragment(new CategoryFragment(),false);
                    return true;
                case R.id.navigation_myjobs:
                    toolbar.setTitle("My Jobs");
                    loadFragment(new JobsFragment(),false);
                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment,Boolean firstfragment) {

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

        // supaya fragment pertama ga masuk stack back ( supaya kalo di pencet back ga ke blank fragment,
        // karena ga ada apa2 di belakang first fragment.. )

        //2018-05-12,hilangin aja deh biar backnya ga ke stack sebelumnya dari semua fragment
//        if(firstfragment==false){
//            transaction.addToBackStack(null);
//        }

        transaction.commit();
    }

}
