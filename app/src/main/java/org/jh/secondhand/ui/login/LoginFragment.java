package org.jh.secondhand.ui.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jh.secondhand.R;

/**
 * deprecated Fragment Class. Do not use this and fragment_login.xml.
 * Alternatively use LoginActivity and other sub Classes
 */
public class LoginFragment extends Fragment {       //deprecated Fragment Class. Do Not use

    private EditText email;
    private EditText password;

    private Button loginButton;
    private Button signInButton;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        email = root.findViewById(R.id.editEmail);
        password = root.findViewById(R.id.editPassword);

        loginButton = root.findViewById(R.id.loginButton);
        signInButton = root.findViewById(R.id.signInButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLogin();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked();
            }
        });

        return root;
    }

    private void verifyLogin(){
        String userEmail = email.getText().toString();
        String userPassword = email.getText().toString();
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            //로그인 성공 시 프래그먼트 전환

                        }else{
                            Toast.makeText(getContext(), "일치하는 계정이 없습니다. 이메일 또는 패스워드를 확인하십시오.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInClicked(){

    }

    @Override
    public void onStart() {
        super.onStart();
        //로그인 프래그먼트 시작시 사용자가 로그인 되어있는지 검증
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //사용자가 로그인 되어 있다면 홈화면으로

        }
    }
}
