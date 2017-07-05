package com.letstellastory.android.letstellastory;

/**
 * Created by dozie on 2017-06-30.
 */

public class ItemObject
{
    private String _name;
    private String _genre;

    public ItemObject(String name, String genre)
    {
        this._name = name;
        this._genre = genre;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        this._name = name;
    }

    public String getGenre()
    {
        return _genre;
    }

    public void setGenre(String genre)
    {
        this._genre = genre;
    }
}