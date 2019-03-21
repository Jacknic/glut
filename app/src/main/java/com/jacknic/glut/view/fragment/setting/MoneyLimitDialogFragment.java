package com.jacknic.glut.view.fragment.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.event.MoneyLimitChangeEvent;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.PreferManager;

import org.greenrobot.eventbus.EventBus;

public class MoneyLimitDialogFragment extends DialogFragment {
    private final SharedPreferences prefer;
    private int limit;

    public static void launch(Activity context) {
        MoneyLimitDialogFragment dialogFragment =
                (MoneyLimitDialogFragment) context.getFragmentManager()
                        .findFragmentByTag(MoneyLimitDialogFragment.class.getName());

        if (dialogFragment == null) {
            dialogFragment = new MoneyLimitDialogFragment();
        }
        dialogFragment.show(context.getFragmentManager(), MoneyLimitDialogFragment.class.getName());
    }

    public MoneyLimitDialogFragment() {
        prefer = PreferManager.getPrefer();
        limit = prefer.getInt(Config.KEY_MONEY_LIMIT, Config.DEFAULT_MONEY_LIMIT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(true);
        View view = View.inflate(getActivity(), R.layout.frag_setting_money_limit, null);
        final TextView textView = (TextView) view.findViewById(R.id.tv_limit);
        textView.setText("" + limit);
        final AppCompatSeekBar seekBar = (AppCompatSeekBar) view.findViewById(R.id.seek_bar);
        seekBar.setProgress(limit);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("关闭", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        prefer.edit().putInt(Config.KEY_MONEY_LIMIT, seekBar.getProgress()).apply();
                        EventBus.getDefault().post(new MoneyLimitChangeEvent());
                    }
                })
                .create();
    }
}
