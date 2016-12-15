package yaoxin.example.com.popularmoves.fragment.bean;

import java.io.Serializable;

/**
 * Created by yaoxinxin on 2016/11/25.
 */

/**
 * id
 * originTitle
 * title
 * adult
 * postUrl
 * backDropUrl
 * overview
 * voteAverage
 * voteCount
 * releaseDate
 * popularity
 * prevueUrl
 * isCollected
 * Duration
 * comments
 */
public class Move extends Object implements Serializable {

    private int id;

    private String MoveId;

    private boolean Adult;

    /**
     * 名称
     */
    private String originTitle;
    private String title;

    /**
     * 海报
     */
    private String posterUrl;

    /**
     * 海报缩略图
     */
    private String backDropUrl;

    /**
     * 简介
     */
    private String overView;

    /**
     * 得分
     */
    private double voteAverage;

    private int voteCount;

    /**
     * 上映日期
     */
    private String releaseDate;

    /**
     * 欢迎程度
     */
    private double popularity;

    /**
     * 评论
     */
    private String comment;

    /**
     * 收藏
     */
    private String collected;

    private String genres;


    public Move() {
    }


    public Move(String id, boolean adult, String originTitle, String title, String posterUrl, String backDropUrl, String overView, float voteAverage, int voteCount, String releaseDate, float popularity) {
        this.MoveId = id;
        Adult = adult;
        this.originTitle = originTitle;
        this.title = title;
        this.posterUrl = posterUrl;
        this.backDropUrl = backDropUrl;
        this.overView = overView;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoveId() {
        return MoveId;
    }

    public void setMoveId(String id) {
        this.MoveId = id;
    }

    public boolean isAdult() {
        return Adult;
    }

    public void setAdult(boolean adult) {
        Adult = adult;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getBackDropUrl() {
        return backDropUrl;
    }

    public void setBackDropUrl(String backDropUrl) {
        this.backDropUrl = backDropUrl;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCollected() {
        return collected;
    }

    public void setCollected(String collected) {
        this.collected = collected;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Move{" +
                "id=" + id +
                ", Adult=" + Adult +
                ", originTitle='" + originTitle + '\'' +
                ", title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", backDropUrl='" + backDropUrl + '\'' +
                ", overView='" + overView + '\'' +
                ", voteAverage=" + voteAverage +
                ", voteCount=" + voteCount +
                ", releaseDate='" + releaseDate + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
