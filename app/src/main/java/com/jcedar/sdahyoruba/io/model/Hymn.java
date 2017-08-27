package com.jcedar.sdahyoruba.io.model;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Afolayan Oluwaseyi on 29/05/2016.
 */
public class Hymn {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("song_id")
    private String songId;

    @Expose
    @SerializedName("song_text")
    private String songText;

    @Expose
    @SerializedName("song_title")
    private String songTitle;

    @Expose
    @SerializedName("english_version")
    private String englishVersion;

    public Hymn() {
    }

    public Hymn(String songId, String songTitle, String englishVersion) {
        this.songTitle = songTitle;
        this.songId = songId;
        this.englishVersion = englishVersion;
    }

    public String getId() {
        return id;
    }

    public String getSongId() {
        return songId;
    }

    public String getSongText() {
        return songText;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getEnglishVersion() {
        return englishVersion;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public void setSongText(String songText) {
        this.songText = songText;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setEnglishVersion(String englishVersion) {
        this.englishVersion = englishVersion;
    }

    public static Hymn[] fromJson(String json){
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create().fromJson(json,  Hymn[].class);
    }

}
