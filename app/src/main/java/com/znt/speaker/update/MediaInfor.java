package com.znt.speaker.update;


import android.os.Parcel;
import android.os.Parcelable;

public class MediaInfor implements Parcelable
{
    public static final String MEDIA_TYPE_MEDIA = "0";
    public static final String MEDIA_TYPE_ADV = "1";
    public static final String MEDIA_TYPE_PUSH = "2";
    public boolean isPlayMeida()
    {
        return media_type.equals(MEDIA_TYPE_MEDIA);
    }

    public boolean isPlayAdv()
    {
        return media_type.equals(MEDIA_TYPE_ADV);
    }

    public boolean isPlayPush()
    {
        return media_type.equals(MEDIA_TYPE_PUSH);
    }


    private String media_name = "";
    private String media_id = "";
    private String media_url = "";
    private long media_size = 0;
    private String media_type = "";//0,media   1, adv   2, push
    private int duration = 0;
    private String album_name = "";
    private String artist_name = "";
    private int reload_count = 0;

    private int cur_seek = 0;

    private boolean isPausePushMedia = false;

    private long modify_time = 0;

    private String start_play_time = "";
    private String end_play_time = "";
    private String start_play_date = "";
    private String end_play_date = "";
    private String play_week = "";

    public MediaInfor()
    {

    }

    public MediaInfor(Parcel source)
    {
        this.media_name = source.readString();
        this.media_url = source.readString();
        this.media_size = source.readLong();
        this.media_type = source.readString();
        this.duration = source.readInt();
        this.album_name = source.readString();
        this.artist_name = source.readString();
        this.reload_count = source.readInt();
        this.modify_time = source.readLong();
        this.cur_seek = source.readInt();
    }

    public int getCurSeek() {
        return cur_seek;
    }

    public void setCurSeek(int curSeek) {
        this.cur_seek = curSeek;
    }

    public long getModifyTime() {
        return modify_time;
    }

    public void setModifyTime(long modifyTime) {
        this.modify_time = modifyTime;
    }

    public void setMediaId(String mediaId)
    {
        this.media_id = mediaId;
    }
    public String getMediaId()
    {
        return media_id;
    }

    public String getStartPlayTime() {
        return start_play_time;
    }

    public void setStartPlayTime(String startPlayTime) {
        this.start_play_time = startPlayTime;
    }

    public String getEndPlayTime() {
        return end_play_time;
    }

    public void setEndPlayTime(String endPlayTime) {
        this.end_play_time = endPlayTime;
    }

    public String getStartPlayDate() {
        return start_play_date;
    }

    public void setStartPlayDate(String startPlayDate) {
        this.start_play_date = startPlayDate;
    }

    public String getEndPlayDate() {
        return end_play_date;
    }

    public void setEndPlayDate(String endPlayDate) {
        this.end_play_date = endPlayDate;
    }

    public String getPlayWeek() {
        return play_week;
    }

    public void setPlayWeek(String playWeek) {
        this.play_week = playWeek;
    }
    public String getMediaName() {
        return media_name;
    }

    public void setMediaName(String mediaName) {
        this.media_name = mediaName;
    }

    public String getMediaUrl() {
        return media_url;
    }

    public void setMediaUrl(String mediaUrl) {
        this.media_url = mediaUrl;
    }

    public long getMediaSize() {
        return media_size;
    }

    public void setMediaSize(long mediaSize) {
        this.media_size = mediaSize;
    }

    public String getMediaType() {
        return media_type;
    }

    public void setMediaType(String mediaType) {
        this.media_type = mediaType;
    }

    public boolean isMedia()
    {
        return media_type.equals(MEDIA_TYPE_MEDIA);
    }
    public boolean isAdv()
    {
        return media_type.equals(MEDIA_TYPE_ADV);
    }
    public boolean isPush()
    {
        return media_type.equals(MEDIA_TYPE_PUSH);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbumName() {
        return album_name;
    }

    public void setAlbumName(String albumName) {
        this.album_name = albumName;
    }

    public String getArtistName() {
        return artist_name;
    }

    public void setArtistName(String artistName) {
        this.artist_name = artistName;
    }

    public void setReloadCount(int reloadCount)
    {
        this.reload_count = reloadCount;
    }
    public int getReloadCount()
    {
        return reload_count;
    }
    public void increaseReloadCount()
    {
        reload_count ++;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(media_name);
        parcel.writeString(media_url);
        parcel.writeLong(media_size);
        parcel.writeString(media_type);
        parcel.writeInt(duration);
        parcel.writeString(album_name);
        parcel.writeString(artist_name);
        parcel.writeInt(reload_count);
        parcel.writeLong(modify_time);
        parcel.writeInt(cur_seek);
    }


    public static final Creator<MediaInfor> CREATOR = new Creator<MediaInfor>()
    {
        @Override
        public MediaInfor createFromParcel(Parcel source) {
            return new MediaInfor(source);
        }

        @Override
        public MediaInfor[] newArray(int size) {
            return new MediaInfor[0];
        }
    };
}
