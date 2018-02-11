package leboncoin.test.com.myphotos.models;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Muthu on 09/02/2018.
 */

public class LocalAlbum extends SugarRecord implements Serializable {

    private Integer albumId;
    private Integer photoId;
    private String title;
    private String url;
    private String thumbnailUrl;

    //Empty constructor for SugarORM
    public  LocalAlbum(){}

    public LocalAlbum(Integer albumId, Integer photoId, String title, String url, String thumbnailUrl){
        this.albumId = albumId;
        this.photoId = photoId;
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;

    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer id) {
        this.photoId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
