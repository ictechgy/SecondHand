package org.jh.secondhand.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jh.secondhand.Book;
import org.jh.secondhand.R;
import org.jh.secondhand.databinding.ItemViewBookBinding;

import java.util.ArrayList;

public class ViewBookListAdapter extends RecyclerView.Adapter<ViewBookListAdapter.ViewBookListViewHolder> {

    private ArrayList<Book> book_list;

    ViewBookListAdapter(ArrayList<Book> book_list){
        this.book_list = book_list;
    }

    static class ViewBookListViewHolder extends RecyclerView.ViewHolder{
        ItemViewBookBinding binding;

        ViewBookListViewHolder(@NonNull ItemViewBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewBookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewBookListViewHolder(ItemViewBookBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBookListViewHolder holder, int position) {
        Book book = book_list.get(position);
        book.setPosition(position); //position 값은 서버에 저장하지 않음.

        holder.binding.setBook(book);
        //Book list Recyclerview ui 추가 수정 필요.
        //그리고 해당 xml 에서 text="@{'책이름 : ' + book.title}" 이나 text="책이름 : '@{book.title}'" 이 안되는 것 같아서 값설정이 추가로 필요해보인다.
    }

    @Override
    public int getItemCount() {
        return book_list.size();
    }

}
