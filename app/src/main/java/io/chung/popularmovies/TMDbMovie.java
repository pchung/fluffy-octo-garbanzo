package io.chung.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import io.chung.popularmovies.utilities.NetworkUtils;

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
    public final Review[] reviews;
    public final int runtime;
    public final boolean video;
    public final Video[] videos;
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
        final String RESULTS_KEY = "results";
        final String REVIEWS_KEY = "reviews";
        final String RUNTIME_KEY = "runtime";
        final String VIDEO_KEY = "video";
        final String VIDEOS_KEY = "videos";
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
        runtime = movieJson.optInt(RUNTIME_KEY);
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

        // Parse videos if available
        Video parsed_videos[];
        try {
            JSONObject videosJson = movieJson.getJSONObject(VIDEOS_KEY);
            parsed_videos = parseVideos(videosJson.getJSONArray(RESULTS_KEY));
        } catch (JSONException e) {
            parsed_videos = new Video[0];
        }
        videos = parsed_videos;

        // Parse reviews if available
        Review parsed_reviews[];
        try {
            JSONObject reviewsJson = movieJson.getJSONObject(REVIEWS_KEY);
            parsed_reviews = parseReviews(reviewsJson.getJSONArray(RESULTS_KEY));
        } catch (JSONException e){
            parsed_reviews = new Review[0];
        }
        reviews = parsed_reviews;
    }

    private Video[] parseVideos(JSONArray videosJson)
            throws JSONException {

        Video videos[] = new Video[videosJson.length()];

        for (int i = 0; i < videosJson.length(); i++) {
            JSONObject videoJson = videosJson.getJSONObject(i);
            videos[i] = new Video(videoJson);
        }

        return videos;
    }

    private Review[] parseReviews(JSONArray reviewsJson)
            throws JSONException {
        Review reviews[] = new Review[reviewsJson.length()];

        for (int i = 0; i < reviewsJson.length(); i++) {
            JSONObject reviewJson = reviewsJson.getJSONObject(i);
            reviews[i] = new Review(reviewJson);
        }

        return reviews;
    }

    private class Review {
        public final String author;
        public final String content;
        public final URL url;

        public Review(JSONObject reviewJson) {
            final String AUTHOR_KEY = "author";
            final String CONTENT_KEY = "content";
            final String URL_KEY = "url";

            author = reviewJson.optString(AUTHOR_KEY);
            content = reviewJson.optString(CONTENT_KEY);

            URL _url;  // Can't do this with url directly since it's possible to be assigned twice.
            try {
                _url = new URL(reviewJson.optString(URL_KEY));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                _url = null;
            }

            url = _url;
        }
    }

    private class Video {
        public final String key;
        public final String name;
        public final String site;
        public final URL url;

        public Video(JSONObject videoJson) {
            final String KEY_KEY = "key";
            final String NAME_KEY = "name";
            final String SITE_KEY = "site";
            final String YOUTUBE_SITE_VALUE = "YouTube";

            key = videoJson.optString(KEY_KEY);
            name = videoJson.optString(NAME_KEY);
            site = videoJson.optString(SITE_KEY);

            if (site.equals(YOUTUBE_SITE_VALUE)) {
                url = NetworkUtils.buildYoutubeUrl(key);
            } else {
                url = null;
            }
        }
    }
}
