package org.jh.secondhand.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 * 인증된 유저에 대한 정보를 UI에 표시하기 위한 클래스
 */
class LoggedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}
