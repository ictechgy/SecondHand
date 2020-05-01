package org.jh.secondhand.ui.buy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BuyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BuyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("현재 준비중인 기능입니다.\n조금만 더 기다려주세요!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}