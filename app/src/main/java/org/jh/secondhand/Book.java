package org.jh.secondhand;

import android.database.Observable;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.InverseBindingAdapter;

import java.util.Calendar;
import java.util.Date;

public class Book { //사거나 팔 책 하나의 정보를 가지고 있을 클래스. Sell 과 Buy 둘다 공용으로 쓰고자 하는 클래스이다.


    private int position;   //RecyclerView에서 특정 Book Item이 몇번째 AdapterPosition을 갖는지 알게 하기 위한 변수. 서버에 올라갈때는 ArrayList<Book>으로 올라가므로 서버상 필요한 변수는 아님.
    private int number; //해당 책 보유 개수
    private String title;   //책 제목
    private String author;  //저자
    private String publishing_house;   //출판사
    private Date publishing_date; //출판연도 published date
    private int used_year; //사용한 학번연도
    private int cost_price; //원가 -> 팔고자 하는 가격은 게시글에 작성하도록 만들기?
    private int selling_price; //팔고자 하는 가격
    private String ISDN; //책 분류번호
    private boolean isSale;

    //이외에 추가적으로 필요한 정보가 있나? 책 상태? 책 사진을 이 클래스에 담는 것이 유효할까?

    public Book(){
        //기본값 세팅
        isSale = true;
        number = 1;
        title = "";
        author = "";
        publishing_house = "";
        publishing_date = Calendar.getInstance().getTime();
        used_year = Calendar.getInstance().get(Calendar.YEAR);      //Calendar클래스? Calendar 와 Date클래스, 그리고 사용자에게서 입력받는 것을 어찌 할 것인지 문제와 서버에 저장하는 방식의 문제.
        cost_price = 0;
        selling_price = 0;
        ISDN = "";

    }

    public Book(int number, String title, String author, String publishing_house, Date publishing_date, int used_year, int cost_price, int selling_price, String ISDN, boolean isSale) {
        this.number = number;
        this.title = title;
        this.author = author;
        this.publishing_house = publishing_house;
        this.publishing_date = publishing_date;
        this.used_year = used_year;
        this.cost_price = cost_price;
        this.selling_price = selling_price;
        this.ISDN = ISDN;
        this.isSale = isSale;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getIsSale(){
        return isSale;
    }

    public void setIsSale(boolean isSale){
        this.isSale = isSale;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if(this.number!=number) this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(!this.title.equals(title)) this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if(!this.author.equals(author)) this.author = author;
    }

    public String getPublishing_house() {
        return publishing_house;
    }

    public void setPublishing_house(String publishing_house) {
        if(!this.publishing_house.equals(publishing_house)) this.publishing_house = publishing_house;
    }

    public Date getPublishing_date() {
        return publishing_date;
    }

    public void setPublishing_date(Date publishing_date) {
        if(this.publishing_date != publishing_date) this.publishing_date = publishing_date;
    }

    public int getUsed_year() {
        return used_year;
    }

    public void setUsed_year(int used_year) {
        if(this.used_year != used_year) this.used_year = used_year;
    }

    public int getCost_price() {
        return cost_price;
    }

    public void setCost_price(int cost_price) {
        if(this.cost_price != cost_price) this.cost_price = cost_price;
    }

    public int getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(int selling_price) {
        if(this.selling_price != selling_price) this.selling_price = selling_price;
    }

    public String getISDN() {
        return ISDN;
    }

    public void setISDN(String ISDN) {
        if(!this.ISDN.equals(ISDN)) this.ISDN = ISDN;
    }

    @BindingConversion
    public static String convertDateToString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (calendar.get(Calendar.YEAR)+"년 "+(calendar.get(Calendar.MONTH)+1)+"월 "+calendar.get(Calendar.DAY_OF_MONTH)+"일");
    }

}

//DataBinding에 있어서 나머지는 상관 없으나 컴파일 오류, publishing_date에 대해서 단방향 업데이트 (book -> xml) 방향으로만 진행. 값에 대한 업데이트는 setter로만.
// publishing_date에 있어서 xml -> book 으로 데이터 바인딩은 어렵고 복잡, 불필요
