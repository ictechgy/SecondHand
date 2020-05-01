package org.jh.secondhand.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;


import org.jh.secondhand.data.LoginDataSource;
import org.jh.secondhand.data.LoginRepository;
import org.jh.secondhand.data.Result;
import org.jh.secondhand.data.model.LoggedInUser;
import org.jh.secondhand.R;

public class LoginViewModel extends ViewModel implements Handler.Callback {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private Handler handler;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        handler = new Handler(this);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password, handler);

        /*
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
         */
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        if(msg.what == LoginDataSource.AFTERLOGIN){
            Result result = (Result)msg.obj;
            if(result instanceof Result.Success){
                LoggedInUser loggedInUser = ((Result.Success<LoggedInUser>) result).getData();
                loginRepository.setLoggedInUser(loggedInUser);
                loginResult.setValue(new LoginResult(new LoggedInUserView(loggedInUser.getDisplayName())));
            }else{      //result instanceof Result.Error
                loginResult.setValue(new LoginResult(R.string.login_failed));
                Exception e = ((Result.Error)result).getError();
                Log.e("error", e.toString());
            }

        }
        return false;
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public boolean isUserLoggedIn(){
        return loginRepository.isLoggedIn();
    }

}
