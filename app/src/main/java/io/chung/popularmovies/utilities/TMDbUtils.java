package io.chung.popularmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.chung.popularmovies.TMDbMovie;

public final class TMDbUtils {

    private static final String TAG = TMDbUtils.class.getSimpleName();

    /**
     * Parses the JSON response for a movie list request.
     *
     * @param response String response of a movie list request.
     * @return An List of Pairs with the first element as the movie ID and the second element as the poster URL.
     * The order of the the List is in the order of the objects seen in the JSON response.
     */
    public static TMDbMovie[] parseMovieListResponse(String response)
            throws JSONException {

        /* Key for movie list results. */
        final String TMDB_RESULTS = "results";

        /* Key for status code when a request failed. */
        final String TMDB_STATUS_CODE = "status_code";

        /* Key for status message related to the status code. */
        final String TMDB_STATUS_MESSAGE = "status_message";

        // Start parsing the given string as a JSON object.
        JSONObject responseJson = new JSONObject(response);

        // TMDb will return a small JSON with "status_code" and "status_message" keys if the request failed.
        if (responseJson.has(TMDB_STATUS_CODE)) {
            int errorCode = responseJson.getInt(TMDB_STATUS_CODE);
            String errorMessage = responseJson.getString(TMDB_STATUS_MESSAGE);

            Log.e(TAG, "Error (" + errorCode + "parsing JSON: " + errorMessage);
            // TODO: Error code handling.
            // TMDb has there status codes here: https://www.themoviedb.org/documentation/api/status-codes
            // Figure out what to do if/when an error code occurs.
        }

        JSONArray movieResults = responseJson.optJSONArray(TMDB_RESULTS);
        TMDbMovie[] movieList = null;

        // Convert each movie result one-by-one into a TMDbMovie object.
        if (movieResults != null) {
            movieList = new TMDbMovie[movieResults.length()];

            for (int i = 0; i < movieResults.length(); i++) {
                JSONObject movieResult = movieResults.getJSONObject(i);

                movieList[i] = new TMDbMovie(movieResult);
            }
        }

        return movieList;
    }

}
