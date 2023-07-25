package com.example.newsaggregator;

public class News {
    public String author;
    public String title;
    public String descp;
    public String url;
    public String urlToImg;
    public String publishedAt;


    News(String author,String title,String descp,String url,String urlToImg,String publishedAt){
        this.author=author;
        this.title=title;
        this.descp=descp;
        this.url=url;
        this.urlToImg=urlToImg;
        this.publishedAt=publishedAt;

    }


    public String getAuthor(){
        return this.author;

    }

    public String getTitle(){
        return title;
    }

    public String getDescp(){
        return this.descp;
    }

    public String getUrl(){
        return this.url;
    }

    public String getUrlToImg(){
        return this.urlToImg;
    }

    public String getPublishedAt(){
        return this.publishedAt;
    }

}
