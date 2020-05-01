package org.jh.secondhand;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SelectFragment extends Fragment {

    Button goToSell;
    Button goToBuy;
    View.OnClickListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //listener = (View.OnClickListener)getActivity();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof View.OnClickListener){
            listener = (View.OnClickListener)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_select, container, false);

        goToSell = root.findViewById(R.id.buttonSell);  //팝니다 게시판으로 가는 버튼
        goToBuy = root.findViewById(R.id.buttonBuy);    //삽니다 게시판으로 가는 버튼

        goToSell.setOnClickListener(listener);
        goToBuy.setOnClickListener(listener);



        return root;
    }
}
