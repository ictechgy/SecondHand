package org.jh.secondhand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private EditText verifyPassword;

    private EditText userName;      //사용자 실명
    private EditText classNumber;   //학번
    private Spinner collegeName;    //학과이름.

    private Button registerButton;

    ProgressBar progressBar;

    /*
    private TextView alertText;     //양식에 맞지 않는 내용이 있을 시 메시지를 띄움.
    private String message;     //에러메시지용.
    -> setError로 대체.
     */

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.emailField);
        userPassword = findViewById(R.id.createPassword);
        verifyPassword = findViewById(R.id.verifyPassword);

        userName = findViewById(R.id.nameField);
        classNumber = findViewById(R.id.classNumber);
        collegeName = findViewById(R.id.collegeName);

        registerButton = findViewById(R.id.join);
        registerButton.setEnabled(true);

        //alertText = findViewById(R.id.alertView);
        progressBar = findViewById(R.id.signUpProgressBar);

        mAuth = FirebaseAuth.getInstance();

        /*
        TextWatcher checkTextListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                int id = ((EditText)s).getId();     //java.lang.ClassCastException: android.text.SpannableStringBuilder cannot be cast to android.widget.EditText
                                                    //따라서 어떤 EditText가 편집되었는지 알기 위해서는 전체적으로 코드 재수정 필요. -> listener 추가? hashCode()이용? @BindingAdapter?  양방향 데이터 바인딩?
                                                    //아니면 LoginActivity처럼 모든 필드의 값을 다 가져오는 방법도 가능(하나의 필드 값만 바뀌어도)
                boolean flag = checkText(id);

                if (flag){
                    registerButton.setEnabled(true);
                }else{
                    registerButton.setEnabled(false);
                    alertText.setText(message);         //또는 각각의 MaterialEditText.setError 방식 가능.
                }
            }
        };
        -> 실시간으로 데이터 값을 추적하는 바인딩방식 사용하지 않음. (@BindingAdapter)


        userEmail.addTextChangedListener(checkTextListener);
        userPassword.addTextChangedListener(checkTextListener);
        verifyPassword.addTextChangedListener(checkTextListener);
        userName.addTextChangedListener(checkTextListener);
        classNumber.addTextChangedListener(checkTextListener);

         */


        final ArrayList<String> colleges = new ArrayList<>();     //임시 생성. 추후에 xml로 관리하도록 하자.
        colleges.add("컴퓨터공학과");
        colleges.add("정보보안학과");
        colleges.add("시각디자인학과");
        colleges.add("사회복지학과");
        colleges.add("멀티미디어학과");
        colleges.add("정보통신학과");
        colleges.add("게임학과");
        //-> collegeName에 사용할 학과명 용도의 리스트.

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colleges);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        collegeName.setAdapter(arrayAdapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createAccount();
                validityCheck(); //각각의 필드 값 유효성 검사 먼저. -> 실시간 데이터 바인딩을 통해 변경되는 값을 추적하여 바로바로 setError도 가능하긴 할거같은데...
            }
        });

        /*
        DO NOT USE THIS.
        collegeName.setOnItemClickListener(new AdapterView.OnItemClickListener() {      //can not use setOnItemClickListener at Spinner
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String collName = colleges.get(position);       //회원가입시 이용할 정보.
            }
        });

        Use Below Method.
        collegeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        -> Spinner에 대해서는 아이템 값 선택 변경시 뭘 해줄 필요가 없음. 다른 변수에 저장시켜놓는다던가 유효성 체크를 해줄 필요가 없음.
        가입 시 (Spinner에서) 해당 데이터를 가져오기만 하면 됨.
         */

    }

    /*
    public boolean checkText(int id){

        switch (id){
            case (R.id.emailField):
                String uEmail = userEmail.getText().toString();
                if(!uEmail.contains("@") || !Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()){
                    message = "이메일 형식이 잘못되어있습니다!";

                    return false;
                }
                break;
            case (R.id.createPassword):
                String password = userPassword.getText().toString();
                if(password.length()<8){
                    message = "패스워드의 길이는 8자가 넘어야 합니다.";

                    return false;
                }
                break;
            case (R.id.verifyPassword):
                if(!userPassword.getText().toString().equals(verifyPassword.getText().toString())){
                    message = "비밀번호와 비밀번호 확인이 일치하지 않습니다!";

                    return false;
                }
            case (R.id.username):
                String name = userName.getText().toString();
                if(!name.matches("[가-힣]{2,4}")) {
                    message = "올바른 이름을 입력해주세요!";

                    return false;
                }
                break;
            case (R.id.classNumber):
                String number = classNumber.getText().toString();
                int iNum = Integer.parseInt(number.substring(0,4));
                if(!number.matches("[0-9]{8}")){
                    message = "학번은 숫자 8자이어야합니다!";

                    return false;
                }else if(!(iNum>=2000 && iNum<=2050)){
                    message = "올바른 학번을 입력하십시오.";    //학번의 연도 유효범위를 2000~2050으로 설정

                    return false;
                }
                break;
                default:
                    return false;
        }
        return true;        //작동에 있어 결함이 있어보임. 하나의 입력창만 조건에 부합해도 return true가 되어버려서 회원가입 버튼이 살아날 것으로 보임.
        //  + 회원가입에 있어 DisplayName도 설정해주기
    }
    이 메소드는 validityCheck()메소드에서 다른 방식으로 사용 할 것임.
     */
    private void validityCheck(){
        String signUpEmail = userEmail.getText().toString();
        String signUpPassword = userPassword.getText().toString();
        String verificationPwd = verifyPassword.getText().toString();
        String signUpName = userName.getText().toString();
        int signUpClassNumber; //별도 처리 필요. Integer.parseInt NumberFormatException 처리
        String signUpCollegeName = collegeName.getSelectedItem().toString();    //유효성 검증 필요 x

        if(!signUpEmail.contains("@") || !Patterns.EMAIL_ADDRESS.matcher(signUpEmail).matches()){
            userEmail.setError("이메일 형식이 올바르지 않습니다.");
            userEmail.setFocusable(true);
            return;
        }else if(signUpPassword.length() < 8){
            userPassword.setError("비밀번호는 최소 8자 이상이어야 합니다.");
            userPassword.setFocusable(true);
            return;
        }else if(!signUpPassword.equals(verificationPwd)){
            verifyPassword.setError("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            verifyPassword.setFocusable(true);
            return;
        }/*else if(!signUpName.matches("[가-힣]{2,4}")){                          //에뮬레이터에서 한글을 입력 불가하여 임시 주석처리.
            userName.setError("성명을 올바르게 입력하여 주십시오.");
            userName.setFocusable(true);
            return;
        }*/else if(classNumber.getText().length()==0){
            classNumber.setError("학번을 입력하여 주십시오.");
            classNumber.setFocusable(true);
            return;
        }else if(!classNumber.getText().toString().matches("[0-9]{8}")){
            classNumber.setError("학번은 8자리 숫자로 구성되어야 합니다.");
            classNumber.setFocusable(true);
            return;
        }

        int yearClassNum = Integer.parseInt(classNumber.getText().toString().substring(0,4));   //학번에서 연도부분 앞 4자리를 추출
        if(yearClassNum < 2000 || yearClassNum > Calendar.getInstance().get(Calendar.YEAR)){    //유효범위는 2000 ~ 현재년도까지.
            classNumber.setError("유효하지 않은 학번입니다.");
            classNumber.setFocusable(true);
            return;
        }

        //유효성검증 완료.
        signUpClassNumber = Integer.parseInt(classNumber.getText().toString());

        //createAccount(signUpEmail, signUpPassword, signUpName, signUpClassNumber, signUpCollegeName);  //use below func.
        //createAccountInBackGround(signUpEmail, signUpPassword, signUpName, signUpClassNumber, signUpCollegeName);
        //param - 각각 사용자 Email, 패스워드, 실명, 학번, 학과.
        createAccountByChaining(signUpEmail, signUpPassword, signUpName, signUpClassNumber, signUpCollegeName);
    }

    private void createAccountInBackGround(String userEmail, String userPassword, String userName, int userClassNumber, String userCollegeName){
        //do sign_up tasks in multiple thread env.

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "처리중입니다. 잠시만 기다려주세요.", Toast.LENGTH_LONG).show();

        //ExecutorService executorService = Executors.newFixedThreadPool(2);  //use another Thread
        /*기존 createAccount() - 멀티쓰레드를 쓰지 않고 UI쓰레드에서 서버 통신 처리. or 별도의 쓰레드를 쓰더라도 handler를 통한 message 처리방식 이용.
        //물론 이 상태에서 addOnCompleteListener를 썼기 때문에 UIThread가 서버 통신 요청 후에 정지된 상태는 아니었음. 이후 처리를 계속 진행해 나가다가 통신이 끝나면 Listener콜백되는 방식.(async)

        //1단계. Executor를 쓰지 않고 기존의 멀티쓰레드 사용방식을 이용.
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d("등록 성공", "-");
                                }else{
                                    Log.d("등록 실패", "-");
                                }
                            }
                        });     -> 다만 이 Listener에서 UI는 직접 조작 불가했을 것(Listener 자체가 별도 Thread상에 존재. ).. Handler 이용하여 결과값 UIThread로 전파 필요.
            }                       아니면 리스너를 메인쓰레드에 두고 그걸로 설정해주던가..
        }).start();

        //2단계. Executor를 쓰되 방식은 1단계와 비슷.
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d("등록 성공", "-");
                                }else{
                                    Log.d("등록 실패", "-");
                                }
                            }
                        });
            }
        });

        //3단계. Executor를 쓰는데 이번에는 Runnable을 쓰지 않고 반환값을 받는 Callable 사용.
        Future<Task<AuthResult>> result =  executorService.submit(new Callable<Task<AuthResult>>() {
            @Override
            public Task<AuthResult> call() throws Exception {
                return mAuth.createUserWithEmailAndPassword(userEmail, userPassword);       //addOnCompleteListener를 두지 않고 반환값을 이용하여 작성
            }
        });
        //반환값을 이용하고자 할 때 BlockingQueue나 ExecutorCompletionService를 이용 할 수도 있음.(poll, take)
        다만 실행은 별도 쓰레드에서 해도 메인쓰레드에서 result.get()하면 메인쓰레드의 블로킹이 발생(결과값이 돌아올때까지)
         */
        //4단계. lamda식 사용.
        /*
        Future<Task<AuthResult>> taskFuture  =  executorService.submit(() -> mAuth.createUserWithEmailAndPassword(userEmail, userPassword));
        executorService.submit(()->{
            try{
                Task<AuthResult> task = taskFuture.get();
                if(task.isSuccessful()){

                }else{

                }
            }catch (ExecutionException | InterruptedException e){

            }
        });
        //서버와 통신하는 쓰레드를 만들고 결과값을 기다리는 별도의 쓰레드 생성?
         */
        //방법은 몇가지가 있을 것 같다.
        // 1. 위와같이 쓰레드 2개 풀을 만들고 하나는 서버로 통신요청을 보내고 하나는 결과값을 기다리도록 한 뒤에 그 결과값을 UIThread에 보내는 방식
        // 2. 쓰레드는 하나를 만들고 서버에 통신을 보내도록 하고 응답이 올 때까지 Future.get()으로 해당 별도 쓰레드가 블로킹상태에 있도록 두는 방식 -> 별도 쓰레드가? 메인쓰레드가 블로킹될거같은데.
        // 3. 2번과 같이 하나의 별도 쓰레드를 사용하되, 결과 콜백 Listener를 UIThread쪽에 설정해두고 실행을 별도쓰레드에서 하되 결과는 UIThread에서 받은 뒤 처리(콜백) 되도록 하는 방식 -> 이렇게 해도 괜찮을까. 별도 쓰레드에서 리스너 설정 시 메인쓰레드의 리스너를 참조해도?
        // 4. 2번과 동일하게 하나의 별도 쓰레드를 사용하도록 하고, 결과값에 대한 부분을 Handler를 통해 UIThread에 전파하는 방식(Message처리) 등..
        // etc.... 뭐.. 기존과 같이 UIThread에서 모든 일을 다 하도록(서버통신, 콜백처리) 하던지.. Future.get()으로 블로킹 처리하던지.. 방법이 정말 많은 것 같다.
        // new Thread(), AsyncTask(), Executor, Handler.....  사용하는 것에 있어서도 Runnable을 쓸지 Callable을 쓸지.. lamda도 들어가면 보기 좋고..
        //외부에 콜백 리스너를 두고 그게 별도 쓰레드에서 발동되도록 할 것인지 (리스너도 Activity에 걸건지, 별도 리스너를 추가 생성할건지 리스너를 상속받아 확장된 리스너를 쓸건지) -> 3번방식.
        //Callable을 이용해 Future.get()을 해두고 결과값을 받으면 해당 별도 쓰레드에서 추후 처리를 하도록 만들 것인지(?) -> 별도의 쓰레드 내에서 또 별도의 쓰레드를 불러
        //2번째 쓰레드가 3번째 쓰레드에게 서버통신을 걸고 2번째 쓰레드 본인은 Future.get()으로 기다린 후 UIThread에 어떤 처리 결과를 넘겨주고.. 등 -> 1번 방식등과 비슷.
        //mAuth.createUserWithEmailAndPassword()에서 return해주는 Task값은 즉시반환인건지, 아니면 서버로부터 값이 돌아오면 반환해주는건지.. -> 공식 API를 보니 완료 뒤 반환.
        //React Native에서도 비동기 처리가 어려운 부분 중 하나였다.. async await

        //원래는 Handler를 쓰려고 했다. SingleThread를 만들고 해당 쓰레드에 세개의 잡을 순차적으로 Queue 한 뒤 메인 쓰레드에 Message를 보내 메인 Handler에서 이를 분류하여 처리하는 방식
        //addOnCompleteListener의 첫번째 인자에 Executor를 두는 방식도 있었다. listener를 예약하는데 사용되는 executor를 지정하는 것이라는데 무슨 말인지 정확히는 모르겠다.
        // Thread에 부착된 리스너는 기본적으로 메인쓰레드에서 실행된다는데, 이 말은 무슨의미일까. 위에 작성한 1단계방식도 가능하다는 의미인건가.
        //가장 중요한건, Chaining이 가능하다는 것. 서버통신시 여러개 통신 체이닝 가능!
        /** @see <a href="https://developers.google.com/android/guides/tasks">Task API</a> */

        /* 기본 형식
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .continueWith(new Continuation<AuthResult, Object>() {
                    @Override
                    public Object then(@NonNull Task<AuthResult> task) throws Exception {
                        return null;
                    }
                });
        lamda식 표기법
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .continueWith(task -> null);
         */
    }

    private void createAccountByChaining(String userEmail, String userPassword, String userName, int userClassNumber, String userCollegeName){
        Log.i("createAccountByChaining", "함수 진입");
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "처리중입니다. 잠시만 기다려주세요.", Toast.LENGTH_LONG).show();
        //ProgressBar가 띄워지면서 뒷 배경이 클릭되지 않도록 하는 것도 있으면 좋을 듯.

        //일단은 별도의 쓰레드에서 처리해야하나 싶다. 일단 체이닝으로 처리하고 추후 필요시 수정하자. (필요한 경우 멀티쓰레드로)
        //- 또는 그냥 OnSuccessListener에서 다음 단계 실행 함수를 호출하는 방식으로 Chaining 할 수도 있을 것 같다..
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)   //firebase authentication에 유저 정보 추가
                .continueWithTask(task -> {
                    Toast.makeText(this, "이름 설정 중...", Toast.LENGTH_LONG).show();
                    Log.d("@##@#@#@#@#", "@#@#@#@#@#"); //로그가 안보이는게 아니라 로그기록이 초기화됨.(특정 오류 등에 의해서)
                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                    builder.setDisplayName(userName);
                    UserProfileChangeRequest request = builder.build();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d("함수진입 확인1111", mAuth.getCurrentUser().getUid());

                    if(user!=null){
                        return user.updateProfile(request);         //유저 정보에 setDisplayName 설정.
                    }else{
                        throw new NullPointerException("Unable to get user information. Failed to Update UserDisplayName");
                        //회원가입은 성공했는데 setDisplayName설정 실패시 throw. 모든 실패는 OnFailure에서 처리.
                    }
                })
                .continueWithTask(task -> {     //Cloud FireStore User 콜렉션에 나머지 정보 추가. (이메일, 이름, 학번, 학과 등...)  <- 권한부분 수정 완료.
                    Toast.makeText(this, "유저 정보 설정 중.. ", Toast.LENGTH_LONG).show();
                    Log.d("함수진입 확인2222", mAuth.getCurrentUser().getUid());        //로그 확인 - 유저 로그인되어있는지 확인.
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null){
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("classNumber", userClassNumber);
                        userInfo.put("college", userCollegeName);
                        userInfo.put("email", userEmail);
                        userInfo.put("name", userName);

                        Log.d("db유저정보 추가 진입 확인", "확인 @@@@@@@");

                        return db.collection("Users").document(user.getUid())               /**현재 유저 정보가 Users 에 등록되지 않는 문제 발생. -> 해결.*/
                                .set(userInfo);
                    }else{
                        throw new NullPointerException("Unable to get user information. Failed to add UserInformation at db");
                    }
                })
                .addOnSuccessListener(aVoid -> {         //모든 통신 성공 후 마지막에 호출. return되어 들어오는 값 없음.
                    //Exception은 발생하는데 프로세스 중단은 발생 안함. Task is not yet complete 오류 발생
                    progressBar.setVisibility(View.GONE);
                    //if(mAuth.getCurrentUser() != null) mAuth.signOut();        //가입 성공 시 바로 로그인이 완료되므로 우선 로그아웃 처리.
                    //-> 로그아웃 처리는 되는거 같긴 한데 LoginActivity 스킵되고 메인Activity으로 넘어감.
                    //이후 판매글 보려 할 시 오류 발생 (비 로그인에 의한 Permission 오류)
                    //-> 아니면 로그아웃 처리는 안됐고 글 보려 할 시 DB상의 Users콜렉션 정보에 사용자 정보(UID)가 올라가지 않음으로 인한 접근 권한 오류일 수도 있다.(읽기권한 없음)
                    //후자의 가능성 높아보임..
                    //다른 문제를 다 처리하고, 로그아웃 처리를 다시 시도해보려고 mAuth.signOut을 넣었더니 이제는 LoginActivity로 회원가입을 취소했다는 메시지와 함께 회귀함..
                    //LoginActivity의 LifeCycle 운용방식에 좀 변화를 줘야 하나..
                    new MaterialAlertDialogBuilder(this).setCancelable(false)
                            .setTitle("회원 가입 성공!").setMessage("회원가입에 성공하였습니다!. 가입한 계정으로 로그인 해 주세요!")
                            .setPositiveButton("확인", (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.putExtra("user_email", userEmail);
                                finish();
                            }).show();
                })
                .addOnFailureListener(e -> {      //체이닝 과정 도중 한번이라도 Exception 오류 발생시 이곳으로. (Exception e)
                    progressBar.setVisibility(View.GONE);
                    Log.e("Error Occurred", e.getMessage());
                    new MaterialAlertDialogBuilder(this).setCancelable(false)
                            .setTitle("오류가 발생하였습니다.").setMessage("회원가입 도중 오류가 발생하였습니다.\n지속적으로 오류 발생시 오류메시지를 보내주세요." +
                            "\n에러 내용 : " + e.getMessage())
                            .setPositiveButton("확인", (dialog, which) -> {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setDataAndType(Uri.parse("mailto:"), "plain/Text");
                                String[] addresses = {"ictechgy@gmail.com"};
                                String subject = "Error message From application 'Second_Hand' when Creating Account";
                                String content = "회원가입 도중 에러가 발생하였습니다. 에러 내용은 아래와 같습니다.\n"+ "Exception 내용 : " + e.getMessage();
                                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                intent.putExtra(Intent.EXTRA_TEXT, content);

                                if(intent.resolveActivity(getPackageManager())!=null){
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                            .show();
                });     //내가 원했던 것처럼 하나의 task가 완료되면 그 다음 task가 차례대로 작동하는 방식이 아니다.. 처음 continueWith만 createAccount 이후 작동되고..
                        //이외의 것들은 다 처리결과가 서버로부터 반환되어 돌아오기 전에 비동기식으로 작동되어버린다.  -> continueWith여서 그랬다.
                        //continueWithTask 를 쓰니 해결됨.

        //이후에 수정해야 할 사항 -> ID 중복검사.

    }


    private void createAccount(final String userEmail, String userPassword, String userName, int userClassNumber, String userCollegeName){
        //final String email = userEmail.getText().toString();
        //String password = userPassword.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "회원가입 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_LONG).show();

        //below job can be started at different Thread (in async)
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setCancelable(false);
                            builder.setTitle("회원가입 성공!");
                            builder.setMessage("회원가입에 성공하였습니다! 가입한 계정으로 로그인해주세요!");
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();

                                    intent.putExtra("user_email", userEmail);
                                    setResult(Activity.RESULT_OK, intent);

                                    finish();       //액티비티를 종료하고 사용자가 등록한 이메일을 자동으로 로그인 창(LoginActivity)에 기입시켜준다.
                                }
                            }).show();
                            //DisplayName 설정 및 fireStore에 User정보 추가 필요.
                            //따라서 3단계의 진행과정이 필요하다. 가입 -> DisplayName 세팅 -> User 정보 firestore 추가. 이를.. AsyncTask로? 별도의 Thread로? UI Thread에서 다 하는건 아닌거 같은데.
                            // -> AsyncTask는 deprecated 되었다고 하므로 java.util.concurrent를 써보자. Executor. 아니면 아예 로그인 방식을 Firebase UI로 대체할 수도 있다.
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setCancelable(false);
                            builder.setTitle("회원가입에 실패하였습니다.");
                            builder.setMessage("관리자에게 문의하십시오.");
                            builder.setPositiveButton("문의하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    //관리자에게 이메일 보내기.
                                    intent.setDataAndType(Uri.parse("mailto:"),"plain/text");
                                    String[] addresses = {"ictechgy@gmail.com"};
                                    String subject = "Error message From application 'Second_Hand' when Create Account";
                                    String content = "회원가입 도중 에러가 발생하였습니다. 에러 내용은 아래와 같습니다.\n"+ "Exception 내용 : " + task.getException()+"\ntask 결과값 : " + task.getResult();
                                    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    intent.putExtra(Intent.EXTRA_TEXT, content);

                                    if(intent.resolveActivity(getPackageManager())!=null){
                                        startActivity(intent);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //오류창만 없애기.
                                }
                            }).show();
                        }

                    }
                });
    }

}
