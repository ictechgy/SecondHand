package org.jh.secondhand.ui.sell;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SellViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(SellViewModel.class)){
            return (T) new SellViewModel(SellRepository.getInstance(new SellDataSource()));
        }else{
            throw new IllegalArgumentException("Unkown ViewModel class");
        }
    }

}
