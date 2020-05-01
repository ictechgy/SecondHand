package org.jh.secondhand.ui.write;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jh.secondhand.Book;
import org.jh.secondhand.BuildConfig;
import org.jh.secondhand.Bulletin;
import org.jh.secondhand.MainActivity;
import org.jh.secondhand.R;
import org.jh.secondhand.ValidationCheckListener;
import org.jh.secondhand.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class WriteSellFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, ValidationCheckListener {       //판매 글 작성 화면

    private EditText title;
    private EditText content;
    private Button addBookButton;
    private Button uploadButton;
    private RecyclerView bookListRecyclerView;
    private BookListAdapter adapter;

    private int itemPosition;
    private Calendar calendar;
    private Book book;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //private FloatingActionButton fab;

    private String uid;
    private String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write_sell, container, false);


        title = rootView.findViewById(R.id.inputTitle);
        content = rootView.findViewById(R.id.inputContent);

        bookListRecyclerView = rootView.findViewById(R.id.bookListRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        bookListRecyclerView.setLayoutManager(layoutManager);

        adapter = new BookListAdapter(this, getContext());
        bookListRecyclerView.setAdapter(adapter);
        addBookButton = rootView.findViewById(R.id.addBookButton);

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();  //팔려는 책 기입 Item 창 하나 추가
            }
        });

        uploadButton = rootView.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationCheck();   //서버 업로드
            }
        });

        //fab = container.findViewById(R.id.fab); getNull
        //fab = getActivity().findViewById(R.id.fab);     //조금 더 효율적인 방법이 없을까.
        // 뒤로 버튼을 누른경우 다시 나타나지 않는다는 단점이 존재한다.
        // 이 경우 onBackPressed로 해결가능해보이기는 하지만, MainActivity에서 아예 fab이 보이는 화면을 선택할 수는 없을까.
        //fab.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //fab = getActivity().findViewById(R.id.fab);
        //fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //fab.setVisibility(View.VISIBLE);
    }
    //fab에 관한 부분 MainActivity에서 관리.

    private void addBook(){     //팔려는 책 추가
        adapter.addBook();
        int newItemIndex = adapter.getItemCount()-1;
        adapter.notifyItemInserted(newItemIndex);
    }


    //책 목록 RecyclerView에서 하나의 아이템에 있는 아이템 삭제 버튼이나 출판일 설정버튼을 눌렀을시에 대한 작동을 이 Fragment에서 처리하도록 설계
    @Override
    public void onClick(View v) {

        int position = bookListRecyclerView.findContainingViewHolder(v).getAdapterPosition();

        switch (v.getId()){
            case (R.id.deleteButton):
                adapter.removeBook(position);
                adapter.notifyItemRemoved(position);
                break;
            case (R.id.publishingDate):
                //기존에는 v.findRootView 또는 v.getParentView로 RecyclerView에서 해당 뷰의 어댑터 위치를 찾으려 했으나 제대로 작동하지 않음.
                itemPosition = position;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    calendar = Calendar.getInstance();
                    book = adapter.getBookItem(position);
                    calendar.setTime(book.getPublishing_date());
                    DatePickerDialog datePickerDialog =
                            new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }else{
                    //Do something for OS Ver under N
                    //publishingdate부분 TextView로 변경했었나
                }
            case (R.id.usedYear):   //사용연도에 대한 처리?
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {   //사용연도를 선택한 후에 확인 누른 경우의 처리 콜백
         calendar.set(year, month, dayOfMonth);
         book.setPublishing_date(calendar.getTime());
         adapter.notifyItemChanged(itemPosition, view);
    }

    /*
    아래 함수의 전체적 작동을 Adapter로 이전(payload 이용) - 특히 setText같은건 여기서 하는게 바람직하지 않을 것 같음.
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {   //사용자가 날짜를 정한 경우
        EditText publishing_date = (EditText)listenerViewContainer;
        calendar.set(year, month, dayOfMonth);
        //String date = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(calendar.getTime());
        //publishing_date.setText(date);
        publishing_date.setText(calendar.get(Calendar.YEAR)+"년 "+calendar.get(Calendar.MONTH)+"월 "+calendar.get(Calendar.DAY_OF_MONTH)+"일");

        book.setPublishing_date(calendar.getTime());
    }

     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int adapterPosition = bookListRecyclerView.findContainingViewHolder(parent).getAdapterPosition();
        Book book = adapter.getBookItem(adapterPosition);
        book.setUsed_year((int)parent.getSelectedItem());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //기본값을 두었으므로 Do nothing
    }

    private void upload(){      //게시글 서버에 업로드.

        //값의 유효성 체크 필요한데.. 업로드시에 한번에 체크할 수도 있고 값이 변동될 때마다 체크하는 방식도 가능할 것 같긴 함.
        //값이 유효하지 않은 경우 띄우는 알림도 어떻게 띄울지.. 아니면 LoginActivity처럼 값이 유효하지 않은 칸에 setError()로 글씨 띄우는 방식같은 것도 될거같고..
        //거기서는 ViewModel써서 값이 유효한지를 계속 체크했었던 것 같은데..


        //ProgressBar 추가필요. 게시글 중 하나 클릭 시 해당 게시글 보이는 기능 추가 필요.(화면 Fragment 추가)
        final View rootView = getView();
        ProgressBar progressBar = null;
        if(rootView!=null){
            progressBar = rootView.findViewById(R.id.progressBarForWriteSell);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            Snackbar.make(rootView, "업로드 중입니다. 잠시만 기다려주세요.", 2000).show();
        }

        ArrayList<Book> list = adapter.getBookList();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null){
            uid = user.getUid();
            userName = user.getDisplayName();

            if(userName != null && userName.equals("")){    //가입시 setUserName 처리코드가 필요할 듯. getDisplayName해도 빈 값이 가져와진다.
                db.collection("Users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userName = documentSnapshot.getString("name");
                        Log.e("userName", userName+" - 유저네임");
                    }
                });
            }
            Log.e("userName", userName+" - 유저네임");  //비동기 처리구조상 값을 가져오기 전에 이 Log문이 작동되어 이 줄의 userName에는 빈값이 나온다.
            //3줄 위의 로그문에는 값이 잘 나온다.

        }else{  //user == null
            if(progressBar != null){
                progressBar.setVisibility(View.INVISIBLE);
            }

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

            builder.setCancelable(false);
            builder.setTitle("로그인이 되어있지 않습니다.");
            builder.setMessage("로그인페이지로 이동합니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
            builder.show();
            return;
        }

        CollectionReference forSaleRef = db.collection("Goods").document("ComputerEngineering").collection("ForSale");

        Bulletin bulletin = new Bulletin();
        bulletin.setTitle(title.getText().toString());
        bulletin.setDate(Calendar.getInstance().getTime());
        bulletin.setWriter(userName);
        bulletin.setIsSale(true);
        bulletin.setWriterUID(uid);
        bulletin.setContent(content.getText().toString());
        bulletin.setCount(list.size());
        bulletin.setView_count(0);
        //사용자가 입력한 내용을 Bulletin객체에 적용시킨다.

        bulletin.setBooks(list);
        //사용자가 입력한 책에 대한 내용을 Bulletin 내부 ArrayList에 넣는다.

        forSaleRef.add(bulletin).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Snackbar.make(rootView, "게시글 등록 성공", 2000).show();

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_sell);  //판매 게시글 목록으로 회귀

                //fab.setVisibility(View.VISIBLE);
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {

                Log.d("uid", uid);
                Log.d("DisplayName", userName);
                Log.d("error", e.getMessage()); //permission error 수정완료. 값 유효성 확인 필요.

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                builder.setTitle("오류발생").setMessage(e.getMessage());
                builder.setPositiveButton("오류전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //개발자에게 오류보고
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setDataAndType(Uri.parse("mailto:"), "plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.report_email));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "upload_error"+this.toString());
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "앱 버전(App Version) : " + BuildConfig.VERSION_CODE +"\n기기명 (Device Name) : \n" +
                                "안드로이드 OS(Android OS) : \n 내용(Content) : " + e.getMessage());

                        if(emailIntent.resolveActivity(getActivity().getPackageManager()) != null){
                            startActivity(emailIntent);
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        if(progressBar!=null){
            progressBar.setVisibility(View.INVISIBLE);
        }

        //-> 게시글 업로드 시 ProgressBar가 제대로 띄워지지 않는 부분 수정 필요, 게시글을 보여줄 때 시간 순서상에 맞게 띄워주는 기능도 필요. (SellFragment)
        //로그인 및 회원가입부분 상세구현 필요. LoginVM의 Lifecycle이 LoginActivity에만 생존하는 상태라면, 해당 VM은 다른 Activity에서 쓸 수는 없을 것.
        //대신 앱의 진입점을 해당 액티비티로 구현만 해두면 모든 액티비티(또는 프래그먼트)의 시작화면(onCreate)에서 로그인 여부를 계속 검사할 필요는 없어지게 됨. (대신 자동로그인 기능 넣던지 하고
        //해당 액티비티를 스킵할 수 있도록 하면 될 듯. 이 때 말하는 스킵이란, 로그인을 하는 통신부분은 살려놓고, 액티비티가 뜨는 부분을 스킵한다는 뜻)
        //글 쓰기라던지 중요한 부분에서만 로그인 체크를 한번 더 하면 되고..
        //+ 댓글기능, 쪽지기능, 자신이 쓴 글 확인, 수정, 삭제기능, 다 팔린 책 체크 기능 등.. 사진 첨부 기능, 로그아웃 버튼, 회원가입, 리스트 로딩 시 제한된 개수만 보여주고 스크롤 시 추가 로딩기능(아래로 당기는 경우 글 새로고침 기능)
    }

    //문서를 삭제해도 하위 콜렉션의 문서는 남아있을 수 있음. 따라서 기존방식 - 각각의 게시글 문서와 그 안에 책 목록에 대한 별도의 콜렉션 및 각각의 책 문서 - 방식은 적합하지 않음
    //게시글 삭제시 하위 책목록 콜렉션 및 문서들이 제대로 삭제되지 않을 수 있음....

    //여기서 게시글 업로드가 잘 되는지 확인해야하고 (summary를 없앴으므로..) 게시글을 받아와 목록으로 보여주는 부분도 수정필요. Sell부분?
    //게시글 업로드에 있어서 커스텀 객체(bulletin)와 nested 커스텀 객체(ArrayList<Book>)가 잘 들어가는지 확인해야함.


    @Override
    public void afterValidCheck(int position, int id, boolean needFocus) {
        BookListAdapter.BookListViewHolder viewHolder = (BookListAdapter.BookListViewHolder)bookListRecyclerView.findViewHolderForAdapterPosition(position);
        if(viewHolder != null){
            switch (id){
                case R.id.bookName:
                    viewHolder.binding.bookName.setError("책 이름을 입력해주세요.");
                    viewHolder.binding.bookName.setFocusable(needFocus);
                    break;
                case R.id.author:
                    viewHolder.binding.author.setError("저자명을 입력해주세요.");
                    viewHolder.binding.author.setFocusable(needFocus);
                    break;
                case R.id.numberOfHoldings:
                    viewHolder.binding.numberOfHoldings.setError("보유 개수를 정확히 입력해주세요.");
                    viewHolder.binding.numberOfHoldings.setFocusable(needFocus);
                    break;
                case R.id.costPrice:
                    viewHolder.binding.costPrice.setError("구매가를 입력해주세요.");
                    viewHolder.binding.costPrice.setFocusable(needFocus);
                    break;
                case R.id.sellingPrice:
                    viewHolder.binding.sellingPrice.setError("판매가를 입력해주세요.");
                    viewHolder.binding.sellingPrice.setFocusable(needFocus);
                    break;
                case R.id.publishingHouse:
                    viewHolder.binding.publishingHouse.setError("출판사를 입력해주세요.");
                    viewHolder.binding.publishingHouse.setFocusable(needFocus);
                    break;
                case R.id.ISDN:
                    viewHolder.binding.ISDN.setError("ISBN을 정확하게 입력해주세요.");
                    viewHolder.binding.ISDN.setFocusable(needFocus);
                    break;
            }
        }
    }

    private void validationCheck(){  //업로드 전 값 유효성 체크. -> ViewModel 등을 이용해서 값의 유효성을 실시간으로 체크하는 방식이 더 나을 것 같긴 한데..(LoginActivity처럼)

        String titleText = title.getText().toString();
        if(titleText.length()<=0){
            title.setError("제목을 입력하십시오.");
            title.setFocusable(true);
            return;
        }

        String contentText = content.getText().toString();
        if(contentText.length() <= 0){
            content.setError("내용을 입력하십시오.");
            content.setFocusable(true);
            return;
        }

        int id = 0; //체크가 필요한 항목의 id 대입. flag 역할도 수행함.
        int position = -1;

        for(Book book : adapter.getBookList()){ // bookList에서 각각의 book 체크
            //int position = book.getPosition();
            position = book.getPosition();

            String bookTitle = book.getTitle();
            if(bookTitle.length()<=0){
                //afterValidCheck(position, R.id.bookName, true);   //이렇게 제대로 입력되지 않은 부분에 대해 setFocus 또는 AlertDialog?
                //return;
                id = R.id.bookName;
                break;
            }

            String bookAuthor = book.getAuthor();
            if(bookAuthor.length()<=0){
                //afterValidCheck(position, R.id.author, true);
                //return;
                id = R.id.author;
                break;
            }

            int bookCostPrice = book.getCost_price();
            if(bookCostPrice <=0){
                //afterValidCheck(position, R.id.costPrice, true);
                //return;
                id = R.id.costPrice;
                break;
            }

            String ISDN = book.getISDN();
            if(ISDN.length()<=0){
                //afterValidCheck(position, R.id.ISDN, true);
                //return;
                id = R.id.ISDN;
                break;
            }

            int bookNumber = book.getNumber();
            if(bookNumber<=0){
                //afterValidCheck(position, R.id.numberOfHoldings, true);
                //return;
                id = R.id.numberOfHoldings;
                break;
            }

            Date publishing_date = book.getPublishing_date();
            if(publishing_date.after(Calendar.getInstance().getTime())){    //현재 날짜보다 뒤의 날짜로 출판일을 기입한경우 이는 잘못된 것이므로.
                BookListAdapter.BookListViewHolder viewHolder = (BookListAdapter.BookListViewHolder)bookListRecyclerView.findViewHolderForAdapterPosition(position);
                if(viewHolder!=null){
                    viewHolder.binding.publishingDate.setError("날짜가 잘못되었습니다!");
                    viewHolder.binding.publishingDate.setFocusable(true);
                }
                return;
            }

            String publishing_house = book.getPublishing_house();
            if(publishing_house.length() <= 0){
                //afterValidCheck(position, R.id.publishingHouse, true);
                //return;
                id = R.id.publishingHouse;
                break;
            }

            int bookSellPrice = book.getSelling_price();
            if(bookSellPrice<=0){
                //afterValidCheck(position, R.id.sellingPrice, true);
                //return;
                id = R.id.sellingPrice;
                break;
            }

            //굳이 여기서 일일히 다 체크를 한다거나 해야할까? 빈 부분이 있다면 해당 부분에 포커스를 맞추면서 setError를 해줘야할까?(book position 이용) 빈 목록들에 대해 AlertDialog를 띄워야할까?
            //체크방식은 어찌해야할까. ViewModel? 아니면 Book에 isValid 변수를? 아니면 다른 데에다가 유효성 변수를 둬야할까? 제대로 입력한 경우 setError를 없애주는 것도 필요할까?
        }

        if(id!=0 && position != -1){
            afterValidCheck(position, id, true);
            return;
        }

        upload();
    }
}
