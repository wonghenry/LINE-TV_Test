package com.example.linetv_test;

public class TVBean {

    private String drama_id;
    private String name;
    private String total_views;
    private String created_at;
    private String thumb;
    private String rating;


    public TVBean(String drama_id, String name, String total_views, String created_at, String thumb, String rating) {
        this.drama_id = drama_id;
        this.name = name;
        this.total_views = total_views;
        this.created_at = created_at;
        this.thumb = thumb;
        this.rating = rating;
    }

    public String get_drama_id() {
        return drama_id;
    }

    public void set_drama_id(String drama_id) {
        this.drama_id = drama_id;
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_total_views() {
        return total_views;
    }

    public void set_total_views(String total_views) {
        this.total_views = total_views;
    }

    public String get_created_at() {
        return created_at;
    }

    public void set_created_at(String created_at) {
        this.created_at = created_at;
    }

    public String get_thumb() {
        return thumb;
    }

    public void set_thumb(String thumb) {
        this.thumb = thumb;
    }

    public String get_rating() {
        return rating;
    }

    public void set_rating(String rating) {
        this.rating = rating;
    }


}
