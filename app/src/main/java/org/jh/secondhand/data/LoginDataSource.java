package org.jh.secondhand.data;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jh.secondhand.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public static final int AFTERLOGIN = 500;
    private FirebaseAuth mAuth;
    private LoggedInUser loggedInUser;
    private Result result;

    public void login(final String username, final String password, final Handler handler){
        mAuth = FirebaseAuth.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    loggedInUser = new LoggedInUser(user.getUid(), user.getDisplayName());
                                    result = new Result.Success<>(loggedInUser);
                                }else{
                                    result = new Result.Error(new IOException("Error logging in", task.getException()));
                                }
                                Message message = handler.obtainMessage();
                                message.what = AFTERLOGIN;
                                message.obj = result;
                                handler.sendMessage(message);
                            }
                        });
            }
        }).start();
    }

    /*
    public Result<LoggedInUser> login(String username, String password) {

        mAuth = FirebaseAuth.getInstance();

        Thread networkThread = new Thread(){
            @Override
            public void run(){

            }
        };

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            loggedInUser = new LoggedInUser(user.getUid(), user.getDisplayName());

                            result = new Result.Success<>(loggedInUser);
                            Log.d("로그인 시점 테스트용", "111111111");
                        }else{
                            result = new Result.Error(new IOException("Error logging in", task.getException()));
                        }
                    }
                });
        Log.d("로그인 시점 테스트용", "2222222222");
        return result;  //여기서도 result를 제대로 반환하지 못하는 비동기 문제가 발생하지 않을까.  -> 발생 중.  2222 가 먼저 나오고 그 다음 1111 이 나옴.


        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

    }
    */


    public void logout() {
        // TODO: revoke authentication
    }
}
