package se.jbnu.yangdoplatform.model;

public class BoardModel {
    String title;
    String content;

    BoardModel(){}

    public BoardModel(String title, String content)
    {
        this.title=title;
        this.content=content;
    }
    public String getTitle(){ return title;}
    public String getContent(){ return content;}
}
