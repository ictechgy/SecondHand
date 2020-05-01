package org.jh.secondhand.ui.sell;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jh.secondhand.Bulletin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SellViewModel extends ViewModel implements Handler.Callback {

    /*
    ViewModel에서는 데이터에 대해서 단순히 가지고 있는다. (게시판 리스트 데이터 밑 각각의 게시글을 눌렀을때의 게시글 데이
    SellFragment에서 요청된 함수(기능)에 따라 ViewModel은 그대로 SellRepository로 해당 요청을 위임한다.
    SellRepository에서는 위임받은 요청을 적절히 분류하여 SellDataSource를 작동시키고 결과값을 돌려받는다.
    결과값은 그대로 ViewModel에까지 가지고오고, Result의 instance에 따라 데이터를 업데이트할지 그대로 둘지 등을 이곳에서 결정한다.

    ViewModel - 데이터 보유 및 fetch한 데이터에 대해 변경여부에 따른 update, 사용자가 요구한 요청은 그대로 SellRepo로 위임
    SellRepo - 위임받은 요청에 따라 적절한 방식으로 SellDataSource 작동시키기. 돌려받은 결과값을 그대로 ViewModel로 전달
    SellDataSource - 통신 코드부분
    SellResult - 결과값을 분류시키기 위한 클래스 - 통신 성공, 통신 실패, 데이터 없음에 대한 분류를 위함.
     */

    private MutableLiveData<ArrayList<Bulletin>> mData;
    private SellRepository sellRepository;
    private Handler handler;


    SellViewModel(SellRepository sellRepository) {
        mData = new MutableLiveData<>();
        this.sellRepository = sellRepository;
        handler = new Handler(this);
    }

    public LiveData<ArrayList<Bulletin>> getData() {        //getData는 onCreateView에서 첫 Observer설정시에도 작동.
        if(mData == null){
            mData = new MutableLiveData<>();
        }
        init();
        return mData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        sellRepository = null;
        handler = null;
    }

    private void init(){
        Thread thread = new DataFetchThread();
        thread.start();
    }

    class DataFetchThread extends Thread{
        @Override
        public void run() {
            super.run();
            sellRepository.getList(handler);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg == null){
            mData.setValue(null);
            return true;
        }

        SellResult result = (SellResult)msg.obj;
        if(result instanceof SellResult.Error){
            mData.setValue(null);
            return true;
        }

        int caseCode = msg.what;
        switch (caseCode){
            case 200:   //게시판 글목록
                ArrayList<Bulletin> list = ((SellResult.Success<ArrayList<Bulletin>>)result).getData();
                mData.setValue(list);
                break;
        }

        return false;
    }



    //핸들러 쓰레드도 존재한다. 핸들러 쓰레드는 핸들러를 가지는 쓰레드인가? 원래는 별도의 쓰레드가 핸들러를 가지려면 Looper와 Handler설정을 해줘야 하는데.

    /*
    class DataFetchThread extends Thread{
        @Override
        public void run() {
            super.run();
            sellRepository.getList(SellViewModel.this);   //ViewModel을 콜백으로 넘겨주는게 과연 바람직한걸까
        }
    }

    @Override
    public void listDataCallBack(SellResult result) {

    }

     */

    /*
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        SellResult result;
        if(task.isSuccessful()){
            ArrayList<Bulletin> list = new ArrayList<>();
            for(QueryDocumentSnapshot document : task.getResult()){
                Bulletin bulletin =  document.toObject(Bulletin.class);
                list.add(bulletin);
            }
            result = new SellResult.Success<>(list);
            mData.setValue(list);
        }else{
            result = new SellResult.Error(task.getException());
            mData.setValue(null);
        }
    }       //클래스의 implements를 Task<QuerySnapshot>으로 받으면 onComplement는 한번밖에 정의를 못하는데,, Task<QuerySnapshot으로 받은 상황에서 일반적인 Task task를 얻는게 가능한걸까.

     */



    //ExecutorService, Future, CompleteableFuture, AsyncTask 들이 있다. - 비동기 처리
    //별도의 콜백 인터페이스를 정의하지 말고 이미 존재하는 콜백인 OnCompleteListener를 이용하는게 낫지 않을까.

    /*
    @SuppressWarnings("unchecked")
    private void init(){
        SellResult result = sellRepository.getList();
        if(result instanceof SellResult.Success){
            ArrayList<Bulletin> list = (ArrayList<Bulletin>) ((SellResult.Success) result).getData();
            //mData.setValue(list);
        }else{ //result instanceof SellResult.Error
            //mData.setValue(null);
        }
    }
     */

    /*
    private void init(){
        sellRepository.getList();
    }

    @verride
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        if(msg.what!=INITIATE) return true;

        SellResult result = (SellResult)msg.obj;
        if(result instanceof SellResult.Success){
            ArrayList<Bulletin> list = (ArrayList<Bulletin>) ((SellResult.Success) result).getData();
            mData.setValue(list);
        }else{
            mData.setValue(null);
        }
        return false;
    }

     */

    //ViewModel에서 쓰레드로 넘기고, 쓰레드에서 sendMessage 한 것을 여기서 handleMessage 한 후 mData.setValue해주면 되나..? 흠.
    //아니면 ViewModel에서 인터페이스 상속한 콜백을 만들고 Repo > DataSource로 참조를 넘긴 다음에 데이터 받으면
    //밑에서부터 다시 콜백으로 호출시켜서 ViewModel의 메소드(setValue)를 실행시키도록
    //하면 될 거 같은데 굳이 그렇게까지 의존되게 해야하나.
    //Handler를 쓰거나.. 아니면 onCompleteListener를 implement해서 사용하거나.. 흠


    //추가적으로 필요한 기능 - 사용자가 위로 스크롤을 한 경우 최신글 업데이트, 다음페이지 요구시 다음페이지 로드

}