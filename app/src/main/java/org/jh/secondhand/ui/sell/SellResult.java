package org.jh.secondhand.ui.sell;

public class SellResult {       //model 디렉토리에 있는 Result 클래스와 동일. fetch한 데이터에 대한 결과값을 분류하기 위한 클래스

    private SellResult(){}


    public final static class Success<T> extends SellResult{
        private T data;
        public Success(T data){
            this.data = data;
        }
        public T getData(){
            return data;
        }
    }

    public final static class Error extends SellResult{
        private Exception error;
        public Error(Exception error){
            this.error = error;
        }
        public Exception getError() {
            return error;
        }
    }
}
