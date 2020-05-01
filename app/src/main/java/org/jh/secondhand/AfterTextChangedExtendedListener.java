package org.jh.secondhand;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Editable;

@TargetApi(Build.VERSION_CODES.N)
public interface AfterTextChangedExtendedListener {
    void afterTextChanged(Editable s, int position, int id);
}
