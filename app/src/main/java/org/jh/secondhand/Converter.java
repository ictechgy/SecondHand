package org.jh.secondhand;

import androidx.databinding.InverseMethod;

public class Converter {    //양방향 데이터바인딩을 위한 Class. 일단은 deprecated

    @InverseMethod("stringToInt")
    public static String intToString(int value){
        return String.valueOf(value);
    }

    public static int stringToInt(String string){
        return Integer.parseInt(string);
    }

}
