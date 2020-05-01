package org.jh.secondhand.ui.sell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jh.secondhand.Book;
import org.jh.secondhand.Bulletin;
import org.jh.secondhand.R;
import org.jh.secondhand.OnFragmentInteractionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.ViewHolder> {

    private ArrayList<Bulletin> mSellData;
    private Context context;
    //private AdapterView.OnItemClickListener listener;
    private View.OnClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title;
        TextView date;
        TextView writer;
        TextView isSale;
        LinearLayout bookListLayout;
        TextView view_count;
        CardView cardView;

        ViewHolder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.titleView);
            date = itemView.findViewById(R.id.dateView);
            writer = itemView.findViewById(R.id.writerView);
            isSale = itemView.findViewById(R.id.isSaleView);
            bookListLayout = itemView.findViewById(R.id.bookListLinearLayout);
            view_count = itemView.findViewById(R.id.view_count);
            cardView = itemView.findViewById(R.id.itemCardView);

            cardView.setOnClickListener(listener);
        }

    }

    SellAdapter(ArrayList<Bulletin> list){
        mSellData = list;
    }

    void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_sell, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Bulletin item = mSellData.get(position);
        String title = item.getTitle();
        String writer = item.getWriter();
        Date date = item.getDate();
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("ko","KR"));
        String formattedDate = dateFormat.format(date);

        //String bookInfo = item.getBooks().get(0).getTitle();    //여기 에러. FireStore에서 내부 콜렉션을 가져오지 못함.

        boolean isSale = item.getIsSale();  //해당 게시글의 모든 책이 팔린 경우 false


        holder.title.setText(title);
        holder.date.setText(formattedDate);
        holder.writer.setText(writer);

        //addBookListToLayout(holder, item.getSummary()); summary 삭제
        addBookListToLayout(holder, item.getBooks());


        if(!isSale){    //해당 게시글의 모든 책이 판배 다 된 경우
            holder.isSale.setText("판매 완료");
            holder.isSale.setTextColor(Color.RED);
            holder.isSale.setPaintFlags(holder.isSale.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        }

        int view_count = item.getView_count();
        holder.view_count.append(String.valueOf(view_count));


        //setOnClickListener를 ViewHolder 생성자에서 해주어야 할까? 그러면 position 정보는 모르는데. RecyclerView.find~ 메소드로 구할 수는 있을 듯. 외부에서.
        //오호. ViewHolder 생성자에서도 getAdapterPosition을 제공한다.

        //holder.cardView.setOnClickListener(new View.OnClickListener() { //목록 중에서 하나의 아이템을 클릭한 경우 해당 글 상세보기 페이지(SellViewFragment로 이동)
        //    @Override
        //    public void onClick(View v) {
        //      listener.onItem...
        //    }
        //});

        //holder.cardView.setOnClickListener(listener);   //여기서 이렇게 계속 반복등록할 필요는 없다고 한다. 다른 Adapter들도 바꿔주면 좋을듯...
    }


    @Override
    public int getItemCount() {
        return mSellData.size();
    }

    /*
    private void addBookListToLayout(@NonNull ViewHolder holder, ArrayList<HashMap<String, Object>> bookList){
        for(HashMap<String, Object> map : bookList){
            String bookName = map.get("name").toString();
            boolean isBookSale = (boolean)map.get("isSale");

            TextView bookTextView = new TextView(context);
            bookTextView.setText(bookName);

            if(!isBookSale) {    //리스트 중 해당 책이 판매중이 아니라면
                bookTextView.setPaintFlags(bookTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                bookTextView.setTypeface(null, Typeface.ITALIC);
            }

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 0, 20, 0);
            bookTextView.setLayoutParams(params);

            holder.bookListLayout.addView(bookTextView);
        }
    }
     */

    //일단 서버의 글을 Bulletin 객체로 받아와지는건 잘 받아와진다. 문제는 그 내부 ArrayList 각각의 인덱스 값이 HashMap이 아닌 Book 객체로 받아와지는지와
    //서버에 글을 올릴 때 Bulletin과 내부의 ArrayList<Book>이 잘 올라가는지를 봐야함. 내부의 Book이 제대로 다운/업로드 안되면 HashMap 변환과정 필요.


    private void addBookListToLayout(@NonNull ViewHolder holder, ArrayList<Book> books){    //하나의 게시글에 포함되어있는 각각의 책들을 목록에서 이름만 간결하게 보여주기 위한 함수.

        for(Book book : books){
            String bookName = book.getTitle();
            boolean isSale = book.getIsSale();

            TextView bookText = new TextView(context);  //onCreateView의 context를 그대로 써도 괜찮은걸까. adapter 생성자에서 Fragment를 context로 받아와야하는걸까.
            bookText.setText(bookName);

            if(!isSale){
                bookText.setPaintFlags(bookText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                bookText.setTypeface(null, Typeface.ITALIC);
            }

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 0, 20, 0);

            bookText.setLayoutParams(params);

            holder.bookListLayout.addView(bookText);
        }

    }
    //테스트 결과 서버상의 게시글을 bulletin으로 잘 받아와짐과 동시에 내부의 Array도 bulletin 내의 ArrayList<Book> 으로 잘 가져와진다.

}
