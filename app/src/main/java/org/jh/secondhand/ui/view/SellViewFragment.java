package org.jh.secondhand.ui.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import org.jh.secondhand.Book;
import org.jh.secondhand.Bulletin;
import org.jh.secondhand.R;
import org.jh.secondhand.ui.sell.SellViewModel;
import org.jh.secondhand.ui.sell.SellViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ITEM_POSITION = "item_position";
    private SellViewModel sellViewModel;

    // TODO: Rename and change types of parameters
    private int item_position;

    public SellViewFragment() {
        // Required empty public constructor        메모리가 부족하여 fragment가 파괴되었다가 다시 복구될 때 시스템에서는 기본적으로 empty parameter constructor를 찾음.
    }//따라서 Fragment 생성시 어떤 인자를 넘겨줘야 한다면 newInstance방식으로 생성시키는 것을 이용하자.

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SellViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellViewFragment newInstance(int position_param) {        //new Instance 방식으로 Fragment들 만드는 것이 좋음. Fragment는 기본생성자를 이용하자.
        SellViewFragment fragment = new SellViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_POSITION, position_param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item_position = getArguments().getInt(ARG_ITEM_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sell_view, container, false);
        //view 자체나 adapter를 BookListAdapter, fragment_write_sell을 재사용해도 나쁘지 않을 듯 하다... item_insert_book 포함.

        sellViewModel = new ViewModelProvider(getActivity()).get(SellViewModel.class);      //ViewModel의 LifeCycleOwner(ViewModelStoreOwner)를 Activity로 변경함. 이는 해당 ViewModel을 생성하는 SellFragment에서 먼저 조정함.
        Bulletin bulletin =  sellViewModel.getData().getValue().get(item_position);     //not Observing. just get Data once

        MaterialTextView title = rootView.findViewById(R.id.viewTitle);
        MaterialTextView content = rootView.findViewById(R.id.viewContent);

        title.setText(bulletin.getTitle());
        content.setText(bulletin.getContent());

        MaterialTextView total_number_of_types_of_books = rootView.findViewById(R.id.typeOfBooks);  //책의 총 권수가 아닌 종류 수 자체에 대한 표시용.
        total_number_of_types_of_books.setText(bulletin.getBooks().size()+"개");

        MaterialTextView total_number_of_books = rootView.findViewById(R.id.totalNumberOfBooks);    //전체 책의 전체 총 권 수
        int count = 0;
        for(Book book : bulletin.getBooks()){
            count += book.getNumber();
        }
        total_number_of_books.setText(count+"개");


        RecyclerView recyclerView = rootView.findViewById(R.id.view_bookListRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ViewBookListAdapter adapter = new ViewBookListAdapter(bulletin.getBooks());
        recyclerView.setAdapter(adapter);

        return rootView;
    }


}
