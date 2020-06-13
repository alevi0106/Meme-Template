package com.example.memetemplate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BotttomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    SeekBar topsbar, bottomsbar;
    TextView t1,t2;
    private static int top_value=0,bottom_value=0;
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
        t1.setText(String.valueOf(top_value));
        t2.setText(String.valueOf(bottom_value));
        topsbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                top_value = progress;
                t1.setText(String.valueOf(top_value));
                mListener.onSeekbarChanged(top_value, bottom_value);
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
                mListener.onSeekbarChanged(top_value,bottom_value);
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
        void onSeekbarChanged(int top_progress, int bottom_seekbar);
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
    }
}
