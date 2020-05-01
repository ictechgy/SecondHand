package org.jh.secondhand.ui.write;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.ListenerUtil;
import androidx.databinding.adapters.TextViewBindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jh.secondhand.AfterTextChangedExtendedListener;
import org.jh.secondhand.Book;
import org.jh.secondhand.R;
import org.jh.secondhand.ValidationCheckListener;
import org.jh.secondhand.databinding.ItemInsertBookBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListViewHolder> implements AfterTextChangedExtendedListener/* implements TextWatcher */{

    private ArrayList<Book> book_list;
    private View.OnClickListener fragmentListener;

    private ArrayAdapter<Integer> spinnerAdapter;
    private ArrayList<Integer> years;

    BookListAdapter(View.OnClickListener fragmentListener, Context context){
        book_list = new ArrayList<>();

        book_list.add(new Book());  //기본적 책 입력공간 하나 위한 책 객체 추가.
        this.fragmentListener = fragmentListener;   //listener 콜백처리를 fragment로

        years = new ArrayList<>();
        for(int i=2000; i<=Calendar.getInstance().get(Calendar.YEAR); i++){ //사용연도 입력창을 위한 spinner에 들어갈 아이템 생성용 for문 2000~2020
            years.add(i);
        }
        Collections.sort(years, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -(o1-o2);            //for문에서 역으로 리스트 add를 해도 되긴 함.
            }
        });
        spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void addBook(){      //사용자가 입력 책 추가시 작동
        book_list.add(new Book());
    }
    public void removeBook(int position){   //사용자가 특정 책 아이템 삭제시 작동
        book_list.remove(position);
    }
    public ArrayList<Book> getBookList(){   //fragment에서 책 list를 필요로 할 때 작동
        return book_list;
    }
    public Book getBookItem(int position){
        return book_list.get(position);
    }

    static class BookListViewHolder extends RecyclerView.ViewHolder{

        ItemInsertBookBinding binding;  //xml과 코드 연결을 binding으로 대체함.

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View bookListItemView = inflater.inflate(R.layout.item_insert_book, parent, false);
        return new BookListViewHolder(bookListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(holder.getAdapterPosition()==RecyclerView.NO_POSITION){
            //Do Something?
            return;
        }else if (payloads.isEmpty()) {        //payloads에 대한 값이 들어오지 않은 경우 기본 onBindViewHolder(holder, int) 실행
            super.onBindViewHolder(holder, position, payloads);
            return;
        }

        for(Object payload : payloads){     //사용자가 출시연도를 변경한 경우 이에 대한 처리. 기존에는 holder.binding.~.set~~ 으로 처리했으나 BindingConversion으로 처리시키도록 함.
            if(payload instanceof DatePicker){
                //holder.binding.publishingDate.invalidate();     //invalidate()해도 괜찮으려나. 데이터에 변동이 발생해서 값을 다시 가져오게 하려고 하는건데. -> 안됨.
                //holder.binding.notifyPropertyChanged(R.id.publishingDate); 작동안함.
                holder.binding.publishingDate.setText(Book.convertDateToString(book_list.get(position).getPublishing_date()));
            }
        }


        //아래 방식은 작동하지 않음.
        //if(payloads.contains(DatePicker.class)){}  -> 알아서 봐줄줄 알았더니 그렇지 않았음. for문 돌려서 일일히 확인해야함.
        //만약에 adapter.notifyItemChanged()로 "onDateSet"이라는 String 인자 넘겨줬었더라면 if(payloads.contains("onDateSet"))뭐 이렇게 동작시킬 수 있긴 했을텐데.
        //payloads.contains(new DateSet())도 가능하려나. DateSet 객체 넘겨준 경우
    }

    @Override
    public void onBindViewHolder(@NonNull final BookListViewHolder holder, int position) {  //각각의 리스트 뷰에 대해 초기화만을 수행해야 함.

        Book book = book_list.get(position);
        book.setPosition(position);
        holder.binding.setBook(book);
        holder.binding.setAdapter(this);

        holder.binding.infoText.setText(position+1+" 번째  책 ");
        if(position==0){
            holder.binding.deleteButton.setVisibility(View.INVISIBLE);
            holder.binding.deleteButton.setEnabled(false);
        }

        /*
        holder.book_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        이렇게 각각의 입력창에 TextWatcher를 만들어서 데이터바인딩을 해야하나? ViewModel? 아니면 업로드 버튼 눌렀을 때 입력된 값을 book list에 저장시키고 사용하기?
        마지막 방법이 가장 좋은 것 같은데..
        -> 데이터 바인딩 방법이 존재하네.. 라이브러리를 쓰는 것도 있고, 기존/버터나이프/자체바인딩.  ViewModel을 쓸 수도 있을거 같긴 한데. xml이 조금 길어질 수도 있고. 흠. 여러가지 방법들. MVP, MVVM 등. 흠.
         */
        // binding,bindingAdapter... 데이터 바인딩,  observable -> LiveData, ViewModel

        holder.binding.usedYear.setAdapter(spinnerAdapter);

        //holder.used_year.setOnItemSelectedListener();
        //각각의 입력창들에 대해 입력값이 변한 경우 변수 book 의 값도 변하도록 데이터 바인딩을 해놓아야 할 까 아니면 upload버튼을 눌렀을 때 반영되도록 할 수 있나.
        //아니면 viewModel을 써야하나? 실시간 데이터 바인딩?
        //어차피 창(입력공간)의 값이 바뀐 경우 해당 EditText에는 바뀐 값이 담겨있기는 할 텐데.. 이걸 변수에 적용시키는걸 어찌 해야할지의 문제.


        holder.binding.deleteButton.setOnClickListener(fragmentListener);
        holder.binding.publishingDate.setOnClickListener(fragmentListener);
        //특정 리사이클러뷰 아이템 하나에 대한 제거 처리/출시년도 클릭시 캘린더 나오는 처리를 Fragment로 위임.

        holder.binding.usedYear.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)fragmentListener);

    }

    @Override
    public int getItemCount() {
        return book_list.size();
    }

    @BindingAdapter(value={"android:beforeTextChanged", "android:onTextChanged", "android:afterTextChanged", "android:position"}, requireAll = false)
    public static void setOnTextChanged(final EditText editText, final TextViewBindingAdapter.BeforeTextChanged beforeTextChanged,
                                        final TextViewBindingAdapter.OnTextChanged onTextChanged, final AfterTextChangedExtendedListener afterTextChanged,
                                        final int position){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            TextWatcher textWatcher;
            if(beforeTextChanged == null && onTextChanged == null && afterTextChanged == null){
                textWatcher = null;
            }else{

                textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if(beforeTextChanged !=null){
                            beforeTextChanged.beforeTextChanged(s, start, count, after);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(onTextChanged != null){
                            onTextChanged.onTextChanged(s, start, before, count);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(afterTextChanged != null){
                            afterTextChanged.afterTextChanged(s, position, editText.getId());
                        }
                    }
                };
            }

            TextWatcher oldWatcher = ListenerUtil.trackListener(editText, textWatcher, R.id.textWatcher);
            if(oldWatcher != null){
                editText.removeTextChangedListener(oldWatcher);
            }
            if(textWatcher != null){
                editText.addTextChangedListener(textWatcher);
            }

        }
    }

    @Override
    public void afterTextChanged(Editable s, int position, int id) {
        Book book = book_list.get(position);
        String value = s.toString();

        //값을 지워서 "" 값이 되는 경우 Integer.parseInt()에서 Exception 발생. validation check도 필요함.
        //if(value.equals("")) return;    //값을 모두 지워서 ""가 되는 경우 일단은 book객체에 값 설정 무시.

        switch (id){
            case R.id.bookName:
                book.setTitle(value);
                break;
            case R.id.author:
                book.setAuthor(value);
                Log.d("저자 변동값 : ", book.getAuthor());       //정상작동 확인
                break;
            case R.id.numberOfHoldings:
                if(value.equals("")){
                    book.setNumber(0);
                    break;
                }
                book.setNumber(Integer.parseInt(value));
                break;
            case R.id.costPrice:
                if(value.equals("")){
                    book.setCost_price(0);
                    break;
                }
                book.setCost_price(Integer.parseInt(value));
                break;
            case R.id.sellingPrice:
                if(value.equals("")){
                    book.setSelling_price(0);
                    break;
                }
                book.setSelling_price(Integer.parseInt(value));
                break;
            case R.id.publishingHouse:
                book.setPublishing_house(value);
                Log.d("출판사 변동값 : ", book.getPublishing_house());    //작동하지 않음. -> 수정완료.
                break;
            case R.id.ISDN:
                book.setISDN(value);
                break;
        }
        //add value validation check?

        if(value.equals("")){
            ValidationCheckListener listener = (ValidationCheckListener)fragmentListener;
            listener.afterValidCheck(position, id, true);   //false -> true로 변경. false로 두니 텍스트 다 삭제한 경우 focus가 안들어가는 문제 발생.
        }

    }


    //아래에 대한 내용 -> 그냥 Listener를 두는 방식으로 해야할듯. 뷰에 대한 내용을 받을 수가 없어서 몇번째 아이템인지 확인할 수가 없다.
    //하나를 두든지 여러개 두든지 해서.. 하나의 아이템에 대해서도 하나를 두는 방식은 불가능할 것 같다.
    //TextWatcher에 아이템 position은 물론 뷰에 대한 정보가 넘어오지도 않아서 어떤 EditText가 변경중인건지 알 수가 없다... 각각의 EditText마다 watcher를 따로 둬야하는건가.

    //-> 뭔가 인터넷에 찾아보니 방법이 있긴 한듯..? 그럼 이걸 BindingAdapter쪽에도 이용 가능할까.
    //setTag()방법, hashCode방법, CustomWatcher나 CustomEditText를 만드는 방법 등등.. 흠... -> android textwatcher recyclerview, android textwatcher get edittext 구글 검색 등등...

    //업로드에 대한 처리문제. 각각의 입력칸 값을 리스트의 각각의 객체에 할당하는 부분에 대하여.
    //ViewHolder가 모든 리스트아이템에 대하여 살아있을지를 모르기때문에 업로드때에 ViewHolder를 For문돌려 가져와서 각각의 맞는 Item리스트에 넣는 것은 불가능해보임.
    //그렇다고 RecyclerView 각각의 아이템에 대한 양방향 데이터바인딩 x.
    //각각의 EditText에 TextChangedlistener를 두지 않고(하나의 Listener를 두고 처리 할 수 있을지도 모르겠다만)
    //BindingAdapter로 함수 처리해보자. 아니면 BindingAdapter로 안해도 TextChangedListener에 맞는 xml attribute 바인딩요소가 있나? (android:onClick같은.. 없는 것 같음)


    /*
    @BindingAdapter(value={"android:beforeTextChanged", "android:onTextChanged", "afterTextChanged"}, requireAll = false)
    public static void setOnTextChanged(EditText editText, final TextViewBindingAdapter.BeforeTextChanged beforeTextChanged,
                                        final TextViewBindingAdapter.OnTextChanged onTextChanged, final TextViewBindingAdapter.AfterTextChanged afterTextChanged){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            TextWatcher textWatcher;
            if(beforeTextChanged == null && onTextChanged == null && afterTextChanged == null){
                textWatcher = null;
            }else{

                textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if(beforeTextChanged !=null){
                            beforeTextChanged.beforeTextChanged(s, start, count, after);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(onTextChanged != null){
                            onTextChanged.onTextChanged(s, start, before, count);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(afterTextChanged != null){
                            afterTextChanged.afterTextChanged(s);
                        }
                    }
                };
            }

            TextWatcher oldWatcher = ListenerUtil.trackListener(editText, textWatcher, R.id.textWatcher);
            if(oldWatcher != null){
                editText.removeTextChangedListener(oldWatcher);
            }
            if(textWatcher != null){
                editText.addTextChangedListener(textWatcher);
            }

        }//버전이 N보다 같거나 높은경우에 대한 처리 끝.
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        //몇번째 아이템의 어떤 EditText인지 어떻게 알지..?

    }
    */

}
