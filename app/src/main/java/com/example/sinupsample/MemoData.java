package com.example.sinupsample;

public class MemoData {
    public String _uid, _memoTitle, _memoContents, _date, _location, _firstName, _lastName;

    public MemoData(String uid, String date, String memoTitle, String memoContents, String location, String firstName, String lastName) {
        this._uid = uid;
        this._date = date;
        this._memoTitle = memoTitle;
        this._memoContents = memoContents;
        this._location = location;
        this._firstName = firstName;
        this._lastName = lastName;
    }

    public MemoData() {
    }

    public String get_uid() {
        return _uid;
    }

    public String get_date() {
        return _date;
    }

    public String get_memoTitle() {
        return _memoTitle;
    }

    public String get_memoContents() {
        return _memoContents;
    }

    public String get_location() {
        return _location;
    }

    public String get_firstName() {
        return _firstName;
    }

    public String get_lastName() {
        return _lastName;
    }
}
