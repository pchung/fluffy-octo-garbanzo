package io.chung.popularmovies;

import org.json.JSONArray;
import org.json.JSONObject;

public class TMDbMovie {
    public final boolean adult;
    public final String backdropPath;
    public final int[] genreIds;
    public final int id;
    public final String originalLanguage;
    public final String originalTitle;
    public final String overview;
    public final double popularity;
    public final String posterPath;
    public final String title;
    public final String releaseDate;
    public final boolean video;
    public final double voteAverage;
    public final int voteCount;

    /**
     * Constructs the object with a TMDb movie list result item.
     * @param movieJson A JSONObject of a single movie from a movie list result.
     */
    public TMDbMovie(JSONObject movieJson) {
        final String ADULT_KEY = "adult";
        final String BACKDROP_PATH_KEY = "backdrop_path";
        final String GENRE_IDS_KEY = "genre_ids";
        final String ID_KEY = "id";
        final String ORIGINAL_LANGUAGE_KEY = "original_language";
        final String ORIGINAL_TITLE_KEY = "original_title";
        final String OVERVIEW_KEY = "overview";
        final String POPULARITY_KEY = "popularity";
        final String POSTER_PATH_KEY = "poster_path";
        final String TITLE_KEY = "title";
        final String RELEASE_DATE_KEY = "release_date";
        final String VIDEO_KEY = "video";
        final String VOTE_AVERAGE_KEY = "vote_average";
        final String VOTE_COUNT_KEY = "vote_count";

        adult = movieJson.optBoolean(ADULT_KEY);
        backdropPath = movieJson.optString(BACKDROP_PATH_KEY);
        id = movieJson.optInt(ID_KEY);
        originalLanguage = movieJson.optString(ORIGINAL_LANGUAGE_KEY);
        originalTitle = movieJson.optString(ORIGINAL_TITLE_KEY);
        overview = movieJson.optString(OVERVIEW_KEY);
        popularity = movieJson.optDouble(POPULARITY_KEY);
        posterPath = movieJson.optString(POSTER_PATH_KEY);
        title = movieJson.optString(TITLE_KEY);
        releaseDate = movieJson.optString(RELEASE_DATE_KEY);
        video = movieJson.optBoolean(VIDEO_KEY);
        voteAverage = movieJson.optDouble(VOTE_AVERAGE_KEY);
        voteCount = movieJson.optInt(VOTE_COUNT_KEY);

        JSONArray genreIdsJson = movieJson.optJSONArray(GENRE_IDS_KEY);

        if (genreIdsJson != null && genreIdsJson.length() > 0) {
            genreIds = new int[genreIdsJson.length()];

            for (int i = 0; i < genreIdsJson.length(); i++) {
                genreIds[i] = genreIdsJson.optInt(i);
            }
        } else {
            genreIds = new int[0];
        }
    }
}
