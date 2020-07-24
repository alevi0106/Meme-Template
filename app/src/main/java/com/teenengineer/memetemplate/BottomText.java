package com.teenengineer.memetemplate;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomText extends BottomSheetDialogFragment {
    private BottomTextListener mListener;
    int textColor = Color.BLACK;
    Button black, white, red, orange, yellow, green, blue, pink, violet, skyblue;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_bottom_text, container, false);
        final EditText inputBox = v.findViewById(R.id.image_edit_text);
        ImageButton okButton = v.findViewById(R.id.submit_text);

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
        black.setOnClickListener(view ->{
            textColor = Color.BLACK;
            inputBox.setTextColor(textColor);
        });
        white.setOnClickListener(view ->{
            textColor = Color.WHITE;
            inputBox.setTextColor(textColor);
        });
        red.setOnClickListener(view ->{
            textColor = Color.parseColor("#ff0000");
            inputBox.setTextColor(textColor);
        });
        orange.setOnClickListener(view ->{
            textColor = Color.parseColor("#ff4411");
            inputBox.setTextColor(textColor);
        });
        yellow.setOnClickListener(view ->{
            textColor = Color.parseColor("#ffff00");
            inputBox.setTextColor(textColor);
        });
        green.setOnClickListener(view -> {
            textColor = Color.parseColor("#00b100");
            inputBox.setTextColor(textColor);
        });
        blue.setOnClickListener(view -> {
            textColor = Color.parseColor("#00008b");
            inputBox.setTextColor(textColor);
        });
        pink.setOnClickListener(view -> {
            textColor = Color.parseColor("#ff1493");
            inputBox.setTextColor(textColor);
        });
        violet.setOnClickListener(view -> {
            textColor = Color.parseColor("#9400d3");
            inputBox.setTextColor(textColor);
        });
        skyblue.setOnClickListener(view -> {
            textColor = Color.parseColor("#0fbed8");
            inputBox.setTextColor(textColor);
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textString = inputBox.getText().toString();
                mListener.onTextAdded(textString, textColor);
                dismiss();
            }
        });
        return v;
    }
    public interface BottomTextListener {
        void onTextAdded(String textString, int textColor);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        BottomSheetDialog bottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogc = (BottomSheetDialog) dialog;
            // When using AndroidX the resource can be found at com.google.android.material.R.id.design_bottom_sheet
            FrameLayout bottomSheet =  dialogc.findViewById( com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return bottomSheetDialog;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomTextListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomTextListener");
        }
    }
}
