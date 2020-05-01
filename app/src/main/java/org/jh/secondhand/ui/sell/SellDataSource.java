package org.jh.secondhand.ui.sell;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.jh.secondhand.Bulletin;

import java.util.ArrayList;

public class SellDataSource {   //이 클래스에서는 데이터를 fetch한다.
    FirebaseFirestore db;
    SellResult result;

    SellDataSource(){
        db = FirebaseFirestore.getInstance();
    }

    public void getList(final Handler handler){ //원하는 페이지 들어가도록 하는 것 요구됨
        final ArrayList<Bulletin> list = new ArrayList<>();

        db.collection("Goods").document("ComputerEngineering").collection("ForSale").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d("Task 형태 : ", task.getResult().toString());
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("Snapshot 형태 : ", document.getId()+"");
                                Bulletin bulletin =  document.toObject(Bulletin.class);

                                //Log.d("게시글 ID : ", bulletin.getId()); ID부분 제거. 게시글 각각에 대한 구분을 숫자에서 firestore 자동 생성값으로 변경함. 따라서 게시글 내에
                                //이 번호를 가지고 있는 필드도 제거함. 나중에 필요시 재생성
                                Log.d("게시글 제목 : ", bulletin.getTitle());
                                Log.d("게시글 내용 : ", bulletin.getContent());
                                Log.d("게시글에서 파는 책의 수 : ", bulletin.getCount()+"");
                                Log.d("게시글 작성일자 : ", bulletin.getDate()+"");
                                Log.d("게시글 작성자 : ", bulletin.getWriter());
                                Log.d("게시글 작성자 UID : ", bulletin.getWriterUID());
                                Log.d("게시글 책 판매여부 : ", bulletin.getIsSale()+"");

                                //데이터 변환시 객체클래스에는 존재하나 DB에는 존재하지 않는 데이터는 값 주입 시 0이나 false 값 등으로 자동 설정해주는 것으로 보임.

                                Log.d("글에 속한 책 확인 : ", bulletin.getBooks().size()+"");  //0나옴.
                                //Log.d("확인 : ", document.getDocumentReference("bookref").getId()); //null값 나오네.. 왜지.
                                Log.d("확인 2 : ", document.contains("content")+"");
                                Log.d("확인 3 : ", document.contains("bookref")+"");
                                //왜 bookref가 안가져와지는지 모르겠다.

                                //내부 collection을 같이 가져오는 방법은? 현재 이 방법으로도 가져와 지고 있는건지 아니면 따로 또 2번 가져와야 하는건지. bookref를 쓸 수 있는건지.
                                //2번을 가져와야 한다면 위의 메소드에 대해서 ref 참조변수로 받고 한번 더 접근해야하는건지 어떻게 해야하는건지..

                                /*
                                DocumentReference reference = document.getReference();
                                reference.collection("/books").document("/1").get().
                                 */


                                //1. QueryDocumentSnapshot과 그냥 DocumentSnapshot은 무슨 차이?
                                //2. document.getDocumentReference는 뭘까? 인자로 String field를 요구하는데..reference 필드값을 이용해 뭔가를 얻는게 맞는 것 같긴 한데 어떻게 사용하는거야?
                                //document.getDocumentReference("bookref").get()  이런 방식으로 쓰는건가?나 이건 재접근 방식인데.  그냥 게시글 목록 가져올 때 한번에 같이 가져오지는 못하나.
                                //3. 아니면 DocumentReference ref = document.getReference()로 참조를 얻고 재접근 하는 방식도 가능하긴 할 것 같은데...
                                //4. 그냥 bookref에 경로를 넣는게 아니라 저장할 때부터 책 정보 하나를 받아서 넣어주면 될거같다. 그게 그냥 편하고 깔끔할 듯..
                                //그리고 count가 2 이상이면 "문화 관광론 외 ~권"으로 표시토록 하게 하고.
                                //두번 접근하는 것보다는 일단 목록 자체에 대해서는 한번만 접근하도록 하는게 낫지 않나??..
                                //근데 게시글 목록에서 조그만하게 ViewPager형식으로 책들 목록 보여주는것도 나쁘진 않은데. Gmail에서 메일목록 중 하나의 메일에 첨부파일 목록 부드럽게 보여주듯이..
                                //다만 문제는 이렇게 게시할 때 책을 여러개 파는 경우 첫번째 책 정보를 가져와서 게시글 목록에서 보여준다는건데, 그러면 그 여러개의 책 중 첫번째 책이 딱 팔린경우
                                //해당 사항을 반영하지 못한다.. DB두번접근이 나을 수도 있고.. 아니면 같이 가져오지는 못하는건가. ㅠㅠ
                                //두번 접근하는게 나을 것 같다. 두번 접근해서 책 이름목록만 받아오고, 해당 이름들을 보여주는 방식으로. 팔린건 안보여주는 것도 좋지만 취소선 그어지게 보여주는것도 좋을 듯.

                                //아 근데 document.getDocumentReference(bookref)가 책 하나에 대해서만 가리킬 수가 있네.. 책 이름 목록을 가져오기가..
                                //이미 가리키는게 books/1이라.. 여러 책 이름목록을 우째 가져오지?
                                //그냥 두번 접근하지 않고 하나의 게시글에서 summary로 책 목록을 따로 보관하도록 두었다.. 물론 이것 또한 팔린 책에 대한 정보를 반영시키기가 어렵다.

                                //방식은 4가지정도인가? bookref를 써서 2번 접근/first_book 방식을 써서 파는 책 중 하나만 일단 표시/String List로 판매여부는 모르지만 일단 책 리스트 표시
                                //Map방식 써서 판매여부까지 표시가능하게 필드값으로서 설정. --> 이걸 쓰자.
                                //다만 그러면 list : [{isSale:true, name:"문화관광론"},{isSale:true,name:"네트워크"}] 방식도 가능하고 - List방식 사용.
                                // list : {1:{isSale:true,name:"문화관광론"}, 2:{isSale:true, name:"문화관광론"}} 방식도 가능하다.
                                //List방식을 이용한 방식으로 작업하자.


                                //Log.d("책 요약정보 : ", bulletin.getSummary().get(0).get("name").toString());    //정상적으로 뜬다.
                                //summary와 실질 책 리스트를 콜렉션으로서 따로 보관하는 방법을 쓰지 않는다.

                                list.add(bulletin);
                            }
                            result = new SellResult.Success<>(list);
                            Log.d("task is Successful?", task.getResult()+"");
                        }else{
                            result = new SellResult.Error(task.getException());
                            Log.d("task is not Successful.", "" + task.getResult() + task.getException());
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = 200; //init 코드
                        msg.obj = result;
                        handler.sendMessage(msg);
                        Log.d("onComplete Ended", msg.what+"");
                    }
                });
        //결과값을 가져오기도 전에 result를 null 상태로 return하는 중? -> 비동기 해결 필요
        //return result;
    }
}
