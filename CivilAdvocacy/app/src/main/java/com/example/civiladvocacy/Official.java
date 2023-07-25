package com.example.civiladvocacy;

import java.io.Serializable;

public class Official implements Serializable {
    public String image_url;
    public String official_party;
    public String official_position;
    public String official_address;
    public String offical_name;
    public String official_email;
    public String official_phonenumber;
    public String official_fblink;
    public String official_twlink;
    public String official_ytlink;
    public String official_partylogo;
    public String user_present_location;
    public String official_weburl;



    Official(String image_url,String official_party,String official_positon,String official_phonenumber,String offical_name,String offical_address,String official_email,String user_present_location,String fb_link,String yt_link,String tw_link, String webURL){
        this.image_url=image_url;
        this.official_party=official_party;
        this.official_position=official_positon;
        this.offical_name=offical_name;
        this.official_phonenumber=official_phonenumber;
        this.official_address=offical_address;
        this.official_email=official_email;
        this.user_present_location=user_present_location;
        this.official_fblink=fb_link;
        this.official_twlink=tw_link;
        this.official_ytlink=yt_link;
        this.official_weburl=webURL;
    }

    public String getImage_url(){return this.image_url;}
    public String getOfficial_party(){return this.official_party;}
    public String getOfficial_email(){return this.official_email;}
    public String getOfficial_phonenumber(){return this.official_phonenumber;}
    public String getOffical_name(){return this.offical_name;}
    public String getOfficial_address(){return this.official_address;}
    public String getOfficial_position(){return this.official_position;}
    public String getUser_present_location(){return this.user_present_location;}
    public String getOfficial_fblink(){ return this.official_fblink;}
    public String getOfficial_ytlink(){return this.official_ytlink;}
    public String getOfficial_twlink(){return this.official_twlink;}
    public String getOfficial_weburl(){return this.official_weburl;}
}


