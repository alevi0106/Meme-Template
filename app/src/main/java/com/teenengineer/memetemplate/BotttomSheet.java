package com.teenengineer.memetemplate;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BotttomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    SeekBar topsbar, bottomsbar;
    TextView t1,t2;
    Button black, white, red, orange, yellow, green, blue, pink, violet, skyblue;
    private static int top_value=0,bottom_value=0, color = Color.WHITE;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_bottom_sheet, container, false);
        topsbar = v.findViewById(R.id.top_seek_bar);
        bottomsbar = v.findViewById(R.id.bottom_seek_bar);
        topsbar.setProgress(top_value);
        bottomsbar.setProgress(bottom_value);
        t1 = v.findViewById(R.id.TopTextValue);
        t2 = v.findViewById(R.id.BottomTextValue);
        black = v.findViewById(R.id.black);
        white = v.findViewById(R.id.white);
        red = v.findViewById(R.id.red);
        orange = v.findViewById(R.id.orange);
        yellow = v.findViewById(R.id.yellow);
        green = v.findViewById(R.id.green);
        blue = v.findViewById(R.id.blue);
        pink = v.findViewById(R.id.pink);
        violet = v.findViewById(R.id.violet);
        skyblue = v.findViewById(R.id.skyblue);
        t1.setText(String.valueOf(top_value));
        t2.setText(String.valueOf(bottom_value));
        black.setOnClickListener(view ->{
            color = Color.BLACK;
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        white.setOnClickListener(view ->{
            color = Color.WHITE;
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        red.setOnClickListener(view ->{
            color = Color.parseColor("#ff0000");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        orange.setOnClickListener(view ->{
            color = Color.parseColor("#ff4411");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        yellow.setOnClickListener(view ->{
            color = Color.parseColor("#ffff00");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        green.setOnClickListener(view -> {
            color = Color.parseColor("#00b100");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        blue.setOnClickListener(view -> {
            color = Color.parseColor("#00008b");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        pink.setOnClickListener(view -> {
            color = Color.parseColor("#ff1493");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        violet.setOnClickListener(view -> {
            color = Color.parseColor("#9400d3");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        skyblue.setOnClickListener(view -> {
            color = Color.parseColor("#0fbed8");
            mListener.onSeekbarChanged(top_value, bottom_value, color);
        });
        topsbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                top_value = progress;
                t1.setText(String.valueOf(top_value));
                mListener.onSeekbarChanged(top_value, bottom_value, color);
//                dismiss();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bottomsbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                bottom_value = progress;
                t2.setText(String.valueOf(bottom_value));
                mListener.onSeekbarChanged(top_value,bottom_value, color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;
    }
    public interface BottomSheetListener {
        void onSeekbarChanged(int top_progress, int bottom_seekbar, int color);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
    public static void set_pixel_value(){
        top_value = 0;
        bottom_value = 0;
        color = Color.WHITE;
    }
}
