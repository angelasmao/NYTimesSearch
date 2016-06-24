package com.codepath.nytimessearch;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by amao on 6/24/16.
 */
@Parcel
public class SearchFilters {

    int beginDate = 0;
    int endDate = 0;
    String sort = "";
    ArrayList<String> newsDesks = new ArrayList<>();
    boolean updated = false;



    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }


    public void setBeginDate(int beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setNewsDesks(ArrayList<String> newsDesks) {
        this.newsDesks = newsDesks;
    }





    public ArrayList<String> getNewsDesks() {
        return newsDesks;
    }

    public int getBeginDate() {
        return beginDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public String getSort() {
        return sort;
    }


    public SearchFilters() {
    }


}
