package io.chung.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String API_KEY_PARAM = "api_key";

    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";

    private static final String APPEND_TO_RESPONSE_QUERY = "append_to_response";
    private static final String APPEND_TO_RESULT_SUB_REQUESTS = "videos,reviews";

    private static final String THEMOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String THEMOVIEDB_POSTER_SIZE = "w780";

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String YOUTUBE_VIDEO_PARAM = "v";

    /**
     * Sort criteria used when requesting movie list.
     */
    public enum SortCriteria {
        POPULAR(POPULAR_PATH), TOP_RATED(TOP_RATED_PATH);

        private final String mValue;

        SortCriteria(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    /**
     * Builds a URL to get a list of movies with the given sortCriteria.
     *
     * @param sortCriteria Sorting used for request.
     * @param apiKey The Movie DB API key used for request.
     * @return The Movie DB URL used for requesting a movie list.
     */
    public static URL buildMovieListUrl(SortCriteria sortCriteria, String apiKey) {
        String subPath = sortCriteria.getValue();

        Uri uri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(subPath)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = uriToUrl(uri);
        Log.v(TAG, "Movie List URL Built: " + url);

        return url;
    }

    /**
     * Builds a Uri for a movie poster with the given image path from TMDb movie list response.
     * @param imagePath Relative path to poster image (e.g. /tWqifoYuwLETmmasnGHO7xBjEtt.jpg).
     * @return Returns a Uri object to the image path.
     */
    public static Uri buildPosterUri(String imagePath) {
        return Uri.parse(THEMOVIEDB_POSTER_BASE_URL).buildUpon()
                .appendPath(THEMOVIEDB_POSTER_SIZE)
                .appendEncodedPath(imagePath)
                .build();
    }

    /**
     * Builds a URL for a movie poster with the given image path from TMDb movie list response.
     * @param imagePath Relative path to poster image (e.g. /tWqifoYuwLETmmasnGHO7xBjEtt.jpg).
     * @return Returns a URL object to the image path.
     */
    public static URL buildPosterUrl(String imagePath) {
        Uri uri = buildPosterUri(imagePath);

        URL url = uriToUrl(uri);
        Log.v(TAG, "Poster path built: " + url);

        return url;
    }

    /**
     * Builds a URL to simultaneously get movie details, videos and reviews for a movie ID.
     *
     * @param movieId The Movie DB's movie ID used to get information.
     * @return The Movie DB URL used for requesting information about the given movie ID.
     */
    public static URL buildMovieDetailsUrl(int movieId, String apiKey) {
        Uri uri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(APPEND_TO_RESPONSE_QUERY, APPEND_TO_RESULT_SUB_REQUESTS)
                .build();

        URL url = uriToUrl(uri);
        Log.v(TAG, "Movie info URL for " + movieId + " built: " + url);

        return url;
    }

    /**
     * Builds a YouTube URL for the given video ID.
     * @param videoId ID of the YouTube video.
     * @return The YouTube URL for the given video ID.
     */
    public static URL buildYoutubeUrl(String videoId) {
        Uri uri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_PARAM, videoId)
                .build();

        URL url = uriToUrl(uri);
        Log.v(TAG, "YouTube URL built: " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Helper function to simplify creating a URL from a Uri.
     *
     * @param uri Uri object to change into a URL object.
     * @return URL object of the given Uri
     */
    private static URL uriToUrl(Uri uri) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
