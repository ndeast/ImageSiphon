package com.neastwest.imagesiphon;

public class Downed {

    //private variables
    private int _id;
    private String _url;
    private String _fullSize;
    private String _thumbnail;

    // Empty constructor
    public Downed(){

    }
    // constructor
    public Downed(int id, String _url, String _fullSize, String _thumbnail){
        this._id = id;
        this._url = _url;
        this._fullSize = _fullSize;
        this._thumbnail = _thumbnail;
    }

    public Downed(int id, String _url, String _thumbnail) {
        this._id = id;
        this._url = _url;
        this._thumbnail = _thumbnail;
    }

    // constructor
    public Downed(String _url, String _thumbnail){
        this._url = _url;
        this._thumbnail = _thumbnail;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting url
    public String getURL(){
        return this._url;
    }

    // setting url
    public void setURL(String _url){
        this._url = _url;
    }

    // getting thumbnail
    public String getThumbnail(){
        return this._thumbnail;
    }

    // setting thumbnail
    public void setThumbnail(String _thumbnail){
        this._thumbnail = _thumbnail;
    }

    // getting fullSize
    public String getFullSize() {
        return this._fullSize;
    }

    // setting fullSize
    public void set_fullSize(String _fullSize) {
        this._fullSize = _fullSize;
    }
}
