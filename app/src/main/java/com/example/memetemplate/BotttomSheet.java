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
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_bottom_sheet, container, false);
        topsbar = v.findViewById(R.id.top_seek_bar);
        bottomsbar = v.findViewById(R.id.bottom_seek_bar);
        topsbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                TextView t1 = v.findViewById(R.id.TopTextValue);
                t1.setText(String.valueOf(progress));
                mListener.onTopSeekbarChanged(progress);
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
                TextView t2 = v.findViewById(R.id.BottomTextValue);
                t2.setText(String.valueOf(progress));
                mListener.onBottomSeekbarChanged(progress);
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
        void onTopSeekbarChanged(int progress);
        void onBottomSeekbarChanged(int progress);
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
}
