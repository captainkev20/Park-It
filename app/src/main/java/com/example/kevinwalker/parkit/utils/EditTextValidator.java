package com.example.kevinwalker.parkit.utils;

import android.content.Context;
import android.widget.EditText;

public class EditTextValidator {

    private Context mContext;

    public EditTextValidator(Context mContext) {
        this.mContext = mContext;
    }

    public boolean validateEditText(EditText editText) {
        boolean pass = false;

        if (!editText.getText().toString().isEmpty()) {
            pass = true;
        }

        return pass;
    }
}
