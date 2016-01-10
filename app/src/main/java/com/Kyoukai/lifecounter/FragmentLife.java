package com.Kyoukai.lifecounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentLife extends Fragment {

    private View view;
    private int life = 20;
    private int poisonVal= 0;
    private int cmdrVal = 0;
    private boolean isTicking = false;
    private boolean poison = false;
    private boolean cmdr = false;
    private boolean [] modes = {true, false, false}; //life, poison, cmdr
    private int currentMode = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_life, container, false);
        setRetainInstance(true);

        //load previous life info
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("lifeInfo", Context.MODE_PRIVATE);
        life = sharedPref.getInt(Integer.toString(this.getId()) + " life", 20);
        poisonVal = sharedPref.getInt(Integer.toString(this.getId()) + " poison", 0);
        cmdrVal = sharedPref.getInt(Integer.toString(this.getId()) + " cmdr", 0);
        boolean wasPoisoned = sharedPref.getBoolean("togglePoison", false);
        boolean wasCmdr = sharedPref.getBoolean("toggleCmdr", false);
        ImageView infectIcon = (ImageView) view.findViewById(R.id.infectIcon);
        ImageView cmdrIcon = (ImageView) view.findViewById(R.id.cmdrIcon);
        TextView cmdrCount = (TextView) view.findViewById(R.id.cmdrCount);
        TextView poisonCount = (TextView) view.findViewById(R.id.poisonCount);
        final ImageButton lifeModeButton = (ImageButton) view.findViewById(R.id.lifeModeButton);

        setLifeButtons();

        if (wasPoisoned) {
            togglePoison();
        }
        else {
            zeroAlpha(infectIcon);
            zeroAlpha(poisonCount);
        }
        if (wasCmdr) {
            toggleCmdr();
        }
        else {
            zeroAlpha(cmdrIcon);
            zeroAlpha(cmdrCount);
        }

        if (modes[0] && !modes[1] && !modes[2]) {
            zeroAlpha(lifeModeButton);
        }
        else {
            oneAlpha(lifeModeButton);
        }

        lifeModeButton.setOnClickListener(

                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setModeButton();
                    }
                }
        );

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        //save life values
        ((MainActivity) getActivity()).updateHistory(life, this.getId());
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("lifeInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Integer.toString(this.getId()) + " life", life);
        editor.putInt(Integer.toString(this.getId()) + " poison", poisonVal);
        editor.putInt(Integer.toString(this.getId()) + " cmdr", cmdrVal);
        editor.putBoolean("togglePoison", poison);
        editor.putBoolean("toggleCmdr", cmdr);
        editor.apply();
    }


    public void resetLife() {
        if (cmdr) {
            life = 40;
        }
        else {
            life = 20;
        }
        poisonVal = 0;
        cmdrVal = 0;
        //set mode button back to default
        currentMode = 2;
        setModeButton();

        TextView lifeValue = (TextView) view.findViewById(R.id.life);
        TextView poisonCount = (TextView) view.findViewById(R.id.poisonCount);
        TextView cmdrCount = (TextView) view.findViewById(R.id.cmdrCount);
        lifeValue.setText("" + life);
        poisonCount.setText("" + poisonVal);
        cmdrCount.setText("" + cmdrVal);
    }


    public void togglePoison()
    {
        poison = !poison;
        modes[1] = poison;

        ImageButton lifeModeButton = (ImageButton) view.findViewById(R.id.lifeModeButton);
        if (modes[0] && !modes[1] && !modes[2]) {
            zeroAlpha(lifeModeButton);
        }
        else {
            oneAlpha(lifeModeButton);
        }

        if ((currentMode == 1) && !poison) {
            setModeButton();
        }

        ImageView infectIcon = (ImageView) view.findViewById(R.id.infectIcon);
        final TextView poisonCount = (TextView) view.findViewById(R.id.poisonCount);

        poisonCount.setText("" + poisonVal);

        if (poison) {
            oneAlpha(infectIcon);
            oneAlpha(poisonCount);
        }
        else {
            zeroAlpha(infectIcon);
            zeroAlpha(poisonCount);
        }
    }

    public void toggleCmdr()
    {
        cmdr = !cmdr;
        modes[2] = cmdr;

        ImageButton lifeModeButton = (ImageButton) view.findViewById(R.id.lifeModeButton);
        if (modes[0] && !modes[1] && !modes[2]) {
            zeroAlpha(lifeModeButton);
        }
        else {
            oneAlpha(lifeModeButton);
        }

        if ((currentMode == 2) && !cmdr) {
            setModeButton();
        }

        ImageView cmdrIcon = (ImageView) view.findViewById(R.id.cmdrIcon);
        final TextView cmdrCount = (TextView) view.findViewById(R.id.cmdrCount);

        cmdrCount.setText("" + cmdrVal);

        if (cmdr) {
            oneAlpha(cmdrIcon);
            oneAlpha(cmdrCount);
        }
        else {
            zeroAlpha(cmdrIcon);
            zeroAlpha(cmdrCount);
        }
    }


    private void zeroAlpha(View v) {
        v.setAlpha(0);
    }

    private void oneAlpha(View v) {
        v.setAlpha(1);
    }

    private int getMode(int currMode) {
        switch (currMode) {
            case 0:
                if (modes[1])
                    return 1;
                else if (modes[2])
                    return 2;
                break;
            case 1:
                if (modes[2])
                    return 2;
                break;
            case 2:
                return 0;
            default:
                break;
        }
        return 0;
    }

    private void setLifeButtons() {
        final ImageButton incButton = (ImageButton) view.findViewById(R.id.incButton);
        ImageButton decButton = (ImageButton) view.findViewById(R.id.decButton);
        final TextView lifeValue = (TextView) view.findViewById(R.id.life);
        lifeValue.setText("" + life);

        final int id = this.getId();
        final CountDownTimer timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                isTicking = true;
            }
            public void onFinish() {
                isTicking = false;
                ((MainActivity) getActivity()).updateHistory(life, id);
            }
        };

        incButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        life++;
                        lifeValue.setText("" + life);
                        if (isTicking) {
                            timer.cancel();
                            timer.start();
                        } else {
                            timer.start();
                        }
                    }
                }
        );

        decButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        life--;
                        lifeValue.setText("" + life);
                        if (isTicking) {
                            timer.cancel();
                            timer.start();
                        } else {
                            timer.start();
                        }
                    }
                }
        );
    }

    private void setPoisonButtons() {
        ImageButton poisonIncButton = (ImageButton) view.findViewById(R.id.incButton);
        ImageButton poisonDecButton = (ImageButton) view.findViewById(R.id.decButton);
        final TextView poisonCount = (TextView) view.findViewById(R.id.poisonCount);
        poisonIncButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        poisonVal++;
                        poisonCount.setText("" + poisonVal);
                    }
                }
        );

        poisonDecButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poisonVal > 0) {
                            poisonVal--;
                        }
                        poisonCount.setText("" + poisonVal);
                    }
                }
        );
    }

    private void setCmdrButtons() {
        ImageButton cmdrIncButton = (ImageButton) view.findViewById(R.id.incButton);
        ImageButton cmdrDecButton = (ImageButton) view.findViewById(R.id.decButton);
        final TextView cmdrCount = (TextView) view.findViewById(R.id.cmdrCount);

        cmdrIncButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cmdrVal++;
                        cmdrCount.setText("" + cmdrVal);
                    }
                }
        );

        cmdrDecButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cmdrVal > 0) {
                            cmdrVal--;
                        }
                        cmdrCount.setText("" + cmdrVal);
                    }
                }
        );
    }

    private void setModeButton() {
        final ImageButton lifeModeButton = (ImageButton) view.findViewById(R.id.lifeModeButton);

        if (modes[0] && !modes[1] && !modes[2]) {
            zeroAlpha(lifeModeButton);
        }
        else {
            oneAlpha(lifeModeButton);
        }

        switch (getMode(currentMode)) {
            case 0:
                lifeModeButton.setBackgroundResource(R.drawable.heart_sq);
                setLifeButtons();
                currentMode = 0;
                break;
            case 1:
                lifeModeButton.setBackgroundResource(R.drawable.infect_sq);
                setPoisonButtons();
                currentMode = 1;
                break;
            case 2:
                lifeModeButton.setBackgroundResource(R.drawable.cmdr_sq);
                setCmdrButtons();
                currentMode = 2;
                break;
            default:
                break;
        }
    }
}
