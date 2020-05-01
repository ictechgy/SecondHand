package org.jh.secondhand.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 * 인증 결과에 대한 클래스
 */
class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
