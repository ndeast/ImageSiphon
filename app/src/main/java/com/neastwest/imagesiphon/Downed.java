package com.neastwest.imagesiphon;

class Downed {

    //private variables
    private int _id;
    private String _url;
    //private String _fullSize;
    private String _thumbnail;

    // constructor
    Downed(int id, String _url, String _thumbnail) {
        this._id = id;
        this._url = _url;
        this._thumbnail = _thumbnail;
    }

    // constructor
    Downed(String _url, String _thumbnail){
        this._url = _url;
        this._thumbnail = _thumbnail;
    }

    // getting url
    String getURL(){
        return this._url;
    }

    // getting thumbnail
    String getThumbnail(){
        return this._thumbnail;
    }

}
