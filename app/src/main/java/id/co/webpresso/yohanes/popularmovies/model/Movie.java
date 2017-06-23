package id.co.webpresso.yohanes.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that holds fetched Movie
 */

public class Movie implements Parcelable {
    public Integer id;
    public String posterPath;
    public String overview;
    public String title;
    public Double voteAverage;
    public Integer voteCount;
    public Double popularity;
    public String releaseDate;
    public Integer runtime;

    /**
     * Get posterPath with size parameter
     * @param size
     * @return
     */
    public String getPosterPath(String size) {
        return "http://image.tmdb.org/t/p/" + size + posterPath;
    }

    /**
     * Get releaseDate as Date object
     * @return
     */
    public Date getReleaseDate() {
        // Convert release date to Date object
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Private constructor function to use with Parcelable
     * @param parcel
     */
    private Movie(Parcel parcel) {
        id = parcel.readInt();
        posterPath = parcel.readString();
        overview = parcel.readString();
        title = parcel.readString();
        voteAverage = parcel.readDouble();
        voteCount = parcel.readInt();
        popularity = parcel.readDouble();
        releaseDate = parcel.readString();
        runtime = parcel.readInt();
    }

    /**
     * Public constructor
     */
    public Movie() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(title);
        parcel.writeDouble(voteAverage);
        parcel.writeInt(voteCount);
        parcel.writeDouble(popularity);
        parcel.writeString(releaseDate);

        // Has to check because runtime is not initially available on results
        if (runtime != null) {
            parcel.writeInt(runtime);
        }
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
