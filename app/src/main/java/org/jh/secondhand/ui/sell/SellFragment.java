package org.jh.secondhand.ui.sell;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jh.secondhand.Bulletin;
import org.jh.secondhand.R;

import org.jh.secondhand.OnFragmentInteractionListener;

import java.util.ArrayList;

public class SellFragment extends Fragment implements View.OnClickListener {

    private SellViewModel sellViewModel;
    private ArrayList<Bulletin> list;
    private ProgressBar progressBar;
    private TextView dataExistText;
    private RecyclerView recyclerView;
    private SellAdapter adapter;
    private OnFragmentInteractionListener mListener;        //communicate with MainActivity

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mListener = (OnFragmentInteractionListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sellViewModel =
        //        ViewModelProviders.of(getActivity(), new SellViewModelFactory()).get(SellViewModel.class);    //deprecated
        sellViewModel = new ViewModelProvider(getActivity(), new SellViewModelFactory()).get(SellViewModel.class);

        View root = inflater.inflate(R.layout.fragment_sell, container, false);

        /*
        LoginViewModel LoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        Log.d("LoginViewModel로 로그인확인", LoginViewModel.isUserLoggedIn()+"");
        //ViewModel을 이용하여 로그인 확인하기. - Test용 구문. 아직 테스트 안함
        -> 작동 안됨. ViewModel instance 얻기 실패
         */

        progressBar = root.findViewById(R.id.progressBar_Sell);
        dataExistText = root.findViewById(R.id.dataExistText);
        recyclerView = root.findViewById(R.id.Recycler_sell);

        sellViewModel.getData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bulletin>>() {
            @Override
            public void onChanged(ArrayList<Bulletin> bulletins) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                if(bulletins == null){  //데이터를 가져오는 도중 오류가 난 경우.
                    new AlertDialog.Builder(getContext())
                            .setTitle("오류 발생")
                            .setMessage("데이터를 가져오는 도중 오류가 발생하였습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    dataExistText.setVisibility(TextView.VISIBLE);
                    dataExistText.setText("데이터를 가져오는데에 실패하였습니다.");
                }else if(bulletins.size() == 0){    //데이터는 가져왔으나 게시글 개수가 0인 경우
                    dataExistText.setVisibility(TextView.VISIBLE);
                }else{  //데이터를 성공적으로 가져온 상태.
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new SellAdapter(bulletins);
                    recyclerView.setAdapter(adapter);
                    dataExistText.setVisibility(View.INVISIBLE);

                    adapter.setListener(SellFragment.this);

                    //adapter = new SellAdapter(bulletins, this) 또는 adapter.setListener(this); 가 여기서 안되는 이유는 this가 Observer이기 때문이다.

                //데이터가 늦게 로딩되는경우 처음에 0크기의 데이터를 반환받고(글 없음 표시가 띄워져버림), 이후에 서버의 데이터를 받음으로서 목록이 보일 수 있음


                    //추가해야하는 부분 -> 목록 새로고침을 한 경우, 글이 많은 경우 페이지로 분할, ... 등
                    //이 Fragment가 다시 띄워지는 경우 서버 재접속이 필요없게 하기. (다른 Fragment에서 뒤로가기를 누른 경우라던지 등.. 특별한 상황이 아닌경우
                    //서버로부터 글을 계속 다시 받아오는 것은 불필요)
                }
            }
        });

        /*
        if(adapter!=null){
            Log.d("adapter.setListener", "이 부분은 과연 작동할까? ");
            adapter.setListener(this);
        }
        //이 부분은 역시나 작동하지 않는다. 비동기식으로 처리되다보니, 서버로 부터 데이터를 가져오는 동안(Observer를 두고 데이터를 가져온 경우의 처리에 대한 과정 기술)
        onCreateView()의 나머지 부분은 동작하여, adapter는 언제나 null로서 처리되는 것으로 보인다.(서버로부터 값을 아직 가져오지 못했으니 dapter를 인스턴스화하지 못한 상태)
         */

        /*
        RecyclerView recyclerView = root.findViewById(R.id.Recycler_sell);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SellAdapter adapter = new SellAdapter(list);

        recyclerView.setAdapter(adapter);
         */
        //현재 권한문제로 데이터를 제대로 가져오지 못하고, getItemCount에서 뻑감.
        //게다가 이 구문을 그대로 두고 실행하는 경우 권한관련 메시지도 나오지 않는데, 아마도 비동기식 처리이다 보니 데이터 가져오기도 전에 화면을 구성하려고 해서 그런 듯.
        //ProgressBar를 띄우고 데이터를 가져오면 그 때 화면이 보이도록 해야할 것 같다.


        /*
        final ArrayList<Bulletin> list = new ArrayList<>();   //테스트용 임시 데이터
        for(int i=0; i<100; i++){
            Book book1 = new Book();
            book1.setName("펭수");
            book1.setAuthor("EBS");
            //book1.setPDate(new Date("2019-12-10"));
            book1.setPrice(20000);
            book1.setPublisher("EBS");
            book1.setSell_price(13000);
            book1.setSubTitle("펭귄 펭수");
            //book1.setUsed_year(new Date("2019"));

            Bulletin bulletin = new Bulletin();
            bulletin.addBook(book1);
            bulletin.setTitle("책팝니다.");
            bulletin.setCount(1);
            //bulletin.setDate(new Date("2019-12-21"));
            bulletin.setSale(true);
            bulletin.setWriter("ssu");

            list.add(bulletin);
        }

        RecyclerView recyclerView = root.findViewById(R.id.Recycler_sell);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SellAdapter adapter = new SellAdapter(list);

        recyclerView.setAdapter(adapter);





        sellViewModel.getData().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] title) {
                list.add(title);
                //페이지 재로드가 필요할까? 자동으로 알아서 할까
            }
        });
        */

        /*
        final TextView textView = root.findViewById(R.id.text_home);
        sellViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        return root;
    }


    @Override
    public void onClick(View v) {       //RecyclerView에서 아이템 하나 클릭시 동작 callback. 넘어오는 View v 는 RecyclerView의 ViewHolder가 가지고 있는 뷰부분 전체가 아닌,
        //그 내부의 일부분인 CardView로 설정해두었다.
        //int position = recyclerView.getChildAdapterPosition(v);   //v는 CardView이고 이는 RecyclerView 입장에서 위치를 파악할 수 있는 요소가 아님. itemView 또는 ViewHolder 필요
        //if(position == RecyclerView.NO_POSITION){     //NoPosition은 해당 itemView에 대해 위치를 제대로 찾지 못한 경우에만 작동하는 듯. CardView를 넘겨주니 getChild ~ 에서 Exception발생
        int position = -1;
            RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(v);
            if(viewHolder != null) position = viewHolder.getAdapterPosition();
        //}

        if(mListener!=null && position != -1){
            Bundle bundle = new Bundle();
            bundle.putString("from", "SellFragment");
            bundle.putInt("position", position);
            //position만을 전달해서 MainActivity를 통해 SellViewFragmnet로 가면서 sellViewModel로부터 bulletins를 얻어서 position 이용해서 해당 게시글만을 다시 얻거나
            //여기서 bundle이나 Object형식으로 해당 bulletin을 얻어서 아예 처음부터 넘겨주거나. 방식은 여러가지이다. - 이 경우 bulletin이 Serializable 또는 Parcelable
            //구현되어야 한다.(Object형식 전달은 별로일 듯. ) 만약 ViewModel이 해당 Fragment가 파기되는 순간 없어진다면, 그리고 이때문에 SellFragment 에서 SellViewFragment로 넘어가면서 ViewModel이 온전지
            //않다면 ViewModel을 이용하지 못하고 Parcelable이나 Serializable을 이용해야할지 모른다. 또는 ViewModel을 Activity에 띄워 Lifecycle을 조정하거나,..
            //(ViewModel은 그러면 큰 화면용에서 Fragment가 동시에 두개 이상 있을 때만 유용한건가) SellFragment의 onDestroy를 조정해야 할 수 있다.(ViewModel이 바로 파기 안되게)
            //애시당초 글의 상세목록을 보고 뒤로 나오면 이전 화면 Fragment가 업데이트 없이 그대로 보여야 하므로 SellFragment가 바로 Destroy 되는 것은 안좋을 것 같은데.
            //SellFragment, sellViewModel, MainActivity, SellViewFragment에서의 Lifecycle 문제.
            mListener.onFragmentInteraction(bundle);
        }else{
            //특정 아이템을 눌렀는데 예외상황이 발생한 경우에 대한 처리
        }

        //OnItemClickListener는 AdapterView용인데, 이는 RecyclerView용이 아님. RecyclerView -> OnItemTouchListener 존재. RecyclerView에 addOn 필요.
    }


}