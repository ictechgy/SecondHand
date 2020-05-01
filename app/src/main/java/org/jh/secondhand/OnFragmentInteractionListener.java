package org.jh.secondhand;

import android.os.Bundle;

public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Bundle bundle);
    //단순 자료형(primitive) -> Object -> Bundle -> Serializable -> 별도 직렬화객체(큰 내용) Parcelable
    //이 중 일단은 Bundle정도로 설정해둠.
}
