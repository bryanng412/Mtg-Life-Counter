package com.Kyoukai.lifecounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.security.SecureRandom;
import java.util.ArrayList;


public class FragmentMenu extends Fragment {
    private View view;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> historyEntries;
    private String background;
    private boolean cmdr = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        setRetainInstance(true);

        //load previous background
        final int id = this.getId();
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("lifeInfo", Context.MODE_PRIVATE);
        boolean wasPoisoned = sharedPref.getBoolean("togglePoison", false);
        boolean wasCmdr = sharedPref.getBoolean("toggleCmdr", false);
        if (wasPoisoned) {
            togglePoison();
        }
        if (wasCmdr) {
            toggleCmdr();
        }

        sharedPref = this.getActivity().getSharedPreferences("backgroundInfo", Context.MODE_PRIVATE);
        background = sharedPref.getString(Integer.toString(id), "");

        ViewPager vpg = (ViewPager) getActivity().findViewById(id);
        final ImageButton manaButton = (ImageButton) view.findViewById(R.id.manaButton);
        final ImageButton infectButton = (ImageButton) view.findViewById(R.id.infectButton);
        final ImageButton cmdrButton = (ImageButton) view.findViewById(R.id.cmdrButton);
        final ImageButton diceButton = (ImageButton) view.findViewById(R.id.diceButton);
        final Animation animDice = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_dice);
        final ImageView dice1 = (ImageView) view.findViewById(R.id.dice1);
        final ImageView dice2 = (ImageView) view.findViewById(R.id.dice2);
        final Handler handleTimer = new Handler();


        switch (background) {
            case "blue":
                vpg.setBackgroundResource(R.drawable.blue);
                manaButton.setBackgroundResource(R.drawable.blue_symbol);
                break;
            case "black":
                vpg.setBackgroundResource(R.drawable.black);
                manaButton.setBackgroundResource(R.drawable.black_symbol);
                break;
            case "red":
                vpg.setBackgroundResource(R.drawable.red);
                manaButton.setBackgroundResource(R.drawable.red_symbol);
                break;
            case "green":
                vpg.setBackgroundResource(R.drawable.green);
                manaButton.setBackgroundResource(R.drawable.green_symbol);
                break;
            case "white":
                vpg.setBackgroundResource(R.drawable.white);
                manaButton.setBackgroundResource(R.drawable.white_symbol);
                break;
            default:
                if (vpg.getBackground().getConstantState().equals(
                        ContextCompat.getDrawable(getActivity(), R.drawable.blue).getConstantState())
                        ) {
                    manaButton.setBackgroundResource(R.drawable.blue_symbol);
                    background = "blue";
                }
                else {
                    manaButton.setBackgroundResource(R.drawable.red_symbol);
                    background = "red";
                }
                break;
        }

        createHistory();

        manaButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager vp = (ViewPager) getActivity().findViewById(id);
                        switch (background) {
                            case "blue":
                                vp.setBackgroundResource(R.drawable.black);
                                manaButton.setBackgroundResource(R.drawable.black_symbol);
                                background = "black";
                                break;
                            case "black":
                                vp.setBackgroundResource(R.drawable.red);
                                manaButton.setBackgroundResource(R.drawable.red_symbol);
                                background = "red";
                                break;
                            case "red":
                                vp.setBackgroundResource(R.drawable.green);
                                manaButton.setBackgroundResource(R.drawable.green_symbol);
                                background = "green";
                                break;
                            case "green":
                                vp.setBackgroundResource(R.drawable.white);
                                manaButton.setBackgroundResource(R.drawable.white_symbol);
                                background = "white";
                                break;
                            case "white":
                                vp.setBackgroundResource(R.drawable.blue);
                                manaButton.setBackgroundResource(R.drawable.blue_symbol);
                                background = "blue";
                                break;
                            default:
                                break;
                        }
                    }
                }
        );

        infectButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) getActivity()).togglePoison();
                    }
                }
        );

        cmdrButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) getActivity()).toggleCmdr();
                        if (historyEntries.size() == 1) {
                            ((MainActivity) getActivity()).resetLife();
                        }
                    }
                }
        );

        diceButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateDiceAlpha(dice1);
                        updateDiceAlpha(dice2);
                        dice1.startAnimation(animDice);
                        dice2.startAnimation(animDice);
                    }
                }
        );

        animDice.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        diceButton.setEnabled(false);
                        diceButton.setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rollDice(dice1, dice2);
                        handleTimer.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateDiceAlpha(dice1);
                                updateDiceAlpha(dice2);
                                diceButton.setEnabled(true);
                                diceButton.setClickable(true);
                                dice1.setBackgroundResource(R.drawable.dice);
                                dice2.setBackgroundResource(R.drawable.dice);
                            }
                        }, 3500);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                }
        );

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        //save background info
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("backgroundInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Integer.toString(this.getId()), background);
        editor.apply();

        //save history info
        sharedPref = this.getActivity().getSharedPreferences("historyInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        String tag = Integer.toString(this.getId()) + " size";
        editor.putString(tag, Integer.toString(historyEntries.size()));
        for (int i=0; i<historyEntries.size(); i++) {
            tag = Integer.toString(this.getId()) + " " + i;
            editor.putString(tag, historyEntries.get(i));
        }
        editor.apply();

    }


    public void togglePoison() {
        ImageButton infectButton = (ImageButton) view.findViewById(R.id.infectButton);
        updateInfectAlpha(infectButton);
    }

    public void toggleCmdr() {
        cmdr = !cmdr;
        ImageButton cmdrButton = (ImageButton) view.findViewById(R.id.cmdrButton);
        updateInfectAlpha(cmdrButton);
    }


    private void createHistory(){
        historyEntries = new ArrayList<>();

        //load previous history info
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("historyInfo", Context.MODE_PRIVATE);
        String tag = Integer.toString(this.getId()) + " size";
        int size = Integer.parseInt(sharedPref.getString(tag, "0"));
        ListView history = (ListView) view.findViewById(R.id.history);
        history.setDivider(null);
        history.setDividerHeight(0);

        for (int i=0; i<size; i++) {
            tag = Integer.toString(this.getId()) + " " + i;
            historyEntries.add(sharedPref.getString(tag, ""));
            if ((Integer.parseInt(historyEntries.get(i)) > 99) || (Integer.parseInt(historyEntries.get(i)) <= -9)) {
                history.getLayoutParams().width = 275;
            }
            if (Integer.parseInt(historyEntries.get(i)) <= -99) {
                history.getLayoutParams().width = 300;
            }
        }
        if (historyEntries.isEmpty()) {
            historyEntries.add("20");
        }
        adapter = new ArrayAdapter<>(getActivity(), R.layout.history, historyEntries);
        history.setAdapter(adapter);
        history.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }


    public void updateHistory(int life){
        //update only if life has changed
        ListView history = (ListView) view.findViewById(R.id.history);
        if ((life > 99) || (life <= -9)) {
            history.getLayoutParams().width = 275;
        }
        if (life <= -99) {
            history.getLayoutParams().width = 300;
        }
        if (!historyEntries.get(historyEntries.size() - 1).equals(Integer.toString(life))) {
            historyEntries.add(Integer.toString(life));
            adapter.notifyDataSetChanged();
        }
    }


    public void clearHistory() {
        ListView history = (ListView) view.findViewById(R.id.history);
        history.getLayoutParams().width = 180;
        historyEntries.clear();
        if (cmdr) {
            historyEntries.add("40");
        }
        else {
            historyEntries.add("20");
        }
        adapter.notifyDataSetChanged();
    }


    private void updateInfectAlpha(View v) {
        if (v.getAlpha() == .5) {
            v.setAlpha(1);
        }
        else {
            v.setAlpha((float) .5);
        }
    }

    private void updateDiceAlpha(View v) {
        if (v.getAlpha() == 0) {
            v.setAlpha(1);
        }
        else {
            v.setAlpha(0);
        }
    }


    private void rollDice(ImageView dice1, ImageView dice2) {
        SecureRandom rand1 = new SecureRandom();
        SecureRandom rand2 = new SecureRandom();
        int roll1 = rand1.nextInt(6) + 1;
        int roll2 = rand2.nextInt(6) + 1;

        switch (roll1) {
            case 1:
                dice1.setBackgroundResource(R.drawable.dice_one);
                break;
            case 2:
                dice1.setBackgroundResource(R.drawable.dice_two);
                break;
            case 3:
                dice1.setBackgroundResource(R.drawable.dice_three);
                break;
            case 4:
                dice1.setBackgroundResource(R.drawable.dice_four);
                break;
            case 5:
                dice1.setBackgroundResource(R.drawable.dice_five);
                break;
            case 6:
                dice1.setBackgroundResource(R.drawable.dice_six);
                break;
        }

        switch (roll2) {
            case 1:
                dice2.setBackgroundResource(R.drawable.dice_one);
                break;
            case 2:
                dice2.setBackgroundResource(R.drawable.dice_two);
                break;
            case 3:
                dice2.setBackgroundResource(R.drawable.dice_three);
                break;
            case 4:
                dice2.setBackgroundResource(R.drawable.dice_four);
                break;
            case 5:
                dice2.setBackgroundResource(R.drawable.dice_five);
                break;
            case 6:
                dice2.setBackgroundResource(R.drawable.dice_six);
                break;
        }
    }

}
