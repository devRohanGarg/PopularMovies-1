package dev.RohanGarg.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CardItemModel implements Parcelable {

    public static final Creator<CardItemModel> CREATOR = new Creator<CardItemModel>() {
        @Override
        public CardItemModel createFromParcel(Parcel in) {
            return new CardItemModel(in);
        }

        @Override
        public CardItemModel[] newArray(int size) {
            return new CardItemModel[size];
        }
    };
    public String posterImgURL, overview, releaseDate, title, backDropImgURL, popularity, voteCount, rating;
    boolean isAdult;

    public CardItemModel(String posterImgURL, boolean isAdult, String overview, String releaseDate, String title, String backDropImgURL, String popularity, String voteCount, String rating) {
        this.posterImgURL = posterImgURL;
        this.isAdult = isAdult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.title = title;
        this.backDropImgURL = backDropImgURL;
        this.popularity = String.format("%.0f", Float.parseFloat(popularity)) + "%";
        this.voteCount = voteCount;
        this.rating = String.format("%.1f", Float.parseFloat(rating));
    }

    protected CardItemModel(Parcel in) {
        posterImgURL = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        backDropImgURL = in.readString();
        popularity = in.readString();
        voteCount = in.readString();
        rating = in.readString();
        isAdult = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterImgURL);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeString(backDropImgURL);
        dest.writeString(popularity);
        dest.writeString(voteCount);
        dest.writeString(rating);
        dest.writeByte((byte) (isAdult ? 1 : 0));
    }
}