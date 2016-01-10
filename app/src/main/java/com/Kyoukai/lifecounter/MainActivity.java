package com.Kyoukai.lifecounter;


import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
        }
        else {
            setContentView(R.layout.activity_main_portrait);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        populateActivity();
    }


    private void populateActivity() {
        ViewPager topLifePager = (ViewPager) findViewById(R.id.topLife);
        ViewPager botLifePager = (ViewPager) findViewById(R.id.botLife);
        final PagerAdapter topLifeAdapter = new PagerAdapter(getSupportFragmentManager());
        final PagerAdapter botLifeAdapter = new PagerAdapter(getSupportFragmentManager());
        topLifePager.setAdapter(topLifeAdapter);
        botLifePager.setAdapter(botLifeAdapter);

        topLifePager.setBackgroundResource(R.drawable.red);
        botLifePager.setBackgroundResource(R.drawable.blue);

        final ImageButton resetButton = (ImageButton) findViewById(R.id.resetButton);
        final ImageView resetButtonDest = (ImageView) findViewById(R.id.resetButtonDest);
        final int [] location = new int[2];

        resetButton.setOnTouchListener(
                new ImageButton.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        resetButtonDest.getLocationOnScreen(location);
                        if ((event.getAction() == MotionEvent.ACTION_DOWN) || (event.getAction() == MotionEvent.ACTION_MOVE)) {
                            if (event.getRawX() >= location[0]) {
                                resetLife();

                                resetButton.setX(0);
                            } else if (event.getRawX() > 0) {
                                resetButton.setX(event.getRawX() - 50);
                            }
                        }
                        return true;
                    }
                }
        );
    }

    public void updateHistory(int life, int id) {
        Fragment frag = getSupportFragmentManager().findFragmentById(id);
        ((FragmentMenu) frag).updateHistory(life);
    }


    public void togglePoison() {
        List<Fragment> fragList = getSupportFragmentManager().getFragments();
        for (int i=0; i<fragList.size(); i++) {
            if (fragList.get(i) instanceof FragmentLife) {
                ((FragmentLife) fragList.get(i)).togglePoison();
            }
            else {
                ((FragmentMenu) fragList.get(i)).togglePoison();
            }
        }
    }

    public void toggleCmdr() {
        List<Fragment> fragList = getSupportFragmentManager().getFragments();
        for (int i=0; i<fragList.size(); i++) {
            if (fragList.get(i) instanceof FragmentLife) {
                ((FragmentLife) fragList.get(i)).toggleCmdr();
            }
            else {
                ((FragmentMenu) fragList.get(i)).toggleCmdr();
            }
        }
    }

    public void resetLife() {
        List<Fragment> fragList = getSupportFragmentManager().getFragments();
        for (int i=0; i<fragList.size(); i++) {
            if (fragList.get(i) instanceof FragmentLife) {
                ((FragmentLife) fragList.get(i)).resetLife();
            }
            else {
                ((FragmentMenu) fragList.get(i)).clearHistory();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        finish();
        startActivity(getIntent());

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_landscape);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_main_portrait);
        }

    }
}