package org.jh.secondhand;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Bulletin { //게시글 클래스

    private String title;    //게시글 제목
    private Date date; //작성날짜
    private String writer; //작성자
    private boolean isSale; //판매 완료여부
    private String writerUID;   //작성자 UID
    private String content; //글 내용
    private int count; //팔고자 하는 책 권수
    private int view_count;

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getView_count() {
        return view_count;
    }

    private ArrayList<Book> books = new ArrayList<>();
    //게시글 하나에는 여러개의 판매 책이 있을 수 있게 해야겠지.
    //즉 게시글 객체 하나에 리스트형태로 Book객체를 내부에 두도록 할 것이다.

    //게시글 하나에 여러개의 판매 책이 있을 수 있는건 맞는데.. 그걸 게시판 글 목록 보여줄때부터 다 가지고 와야하나?
    //게시글 목록에도 일부 책 정보를 가져와줘야 하니 필요하려나? 뭐 파는지 간단하게 보여주려면.
    //아니면 책목록은 빼고 게시글 정보만 가져온 다음에.. 사용자가 하나의 아이템을 선택하면 그 때 책 목록을 따로 추가적으로 가져와서 같이 보여주면 안되나?
    //현재 게시글 데이터를 가져올 때 내부 컬렉션인 books가 ArrayList인 books에 제대로 할당이 되질 않는 것 같다.

    //게시글을 불러올 때 내부 콜랙션도 가져오는 방법이 없다면 하나의 게시글 목록 아이템을 보여주기 위해 두번의 DB접근을 해야 할 수도 있다. 하지만 해당 방법은 별로일 것 같음
    //그렇다면 차라리 ref형식으로 게시글에서 책 항목 콜렉션 중 하나를 무조건 가져오도록 해주자.(참조) 그리고 각각의 게시글들은 최소 하나의 책은 입력하도록 만들면 되지.
    //그렇게 하면 여기에서는 게시글 목록만 불러와주면 끝이다. 그러면 bookref는 자동으로 딸려오는거 아닌가..

    //아니면 그냥 게시글 목록 불러올 때 내부 Collection 도 불러오는 방법이 있는건가.

    //그리고 어떤 변화에 대한 리스너도 둘 수 있는 듯 하다. 필드값 변경이라던지.. 등 -> 댓글부분 구현할 때 쓰면 되나?

    //현재 bookref가 제대로 가져와지지 않는다.. 그냥 내부 books를 따로 가져와야하나.. 아니면 가져오지 말아야하나. 또는 게시글 document가져올 때 자동으로 가져오는 방법이 있는걸까.

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public boolean getIsSale() {
        return isSale;
    }

    public void setIsSale(boolean isSale) {
        this.isSale = isSale;
    }

    public String getWriterUID() {
        return writerUID;
    }

    public void setWriterUID(String writerUID) {
        this.writerUID = writerUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }

    public void addBooks(Book book){
        this.books.add(book);
    }

    public Bulletin(){};

    public Bulletin(String title, Date date, String writer, boolean isSale, String writerUID, String content, int count, ArrayList<Book> books) {
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.isSale = isSale;
        this.writerUID = writerUID;
        this.content = content;
        this.count = count;
        this.books = books;
    }

}
