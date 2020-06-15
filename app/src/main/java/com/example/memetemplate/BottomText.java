package com.example.memetemplate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomText extends BottomSheetDialogFragment {
    private BottomTextListener mListener;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_bottom_text, container, false);
        final EditText inputBox = v.findViewById(R.id.image_edit_text);
        ImageButton okButton = v.findViewById(R.id.submit_text);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textString = inputBox.getText().toString();
                mListener.onTextAdded(textString);
                dismiss();
            }
        });
        return v;
    }
    public interface BottomTextListener {
        void onTextAdded(String textString);
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
