package io.chung.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.chung.popularmovies.TMDbMovie;

public final class TMDbUtils {
    private static final String TAG = TMDbUtils.class.getSimpleName();

    /* Key for status code when a request failed. */
    private static final String TMDB_STATUS_CODE = "status_code";

    /* Key for status message related to the status code. */
    private static final String TMDB_STATUS_MESSAGE = "status_message";

    /**
     * Checks to see if the response contains an error. If an error is
     * @param responseJson JSONObject of the response.
     */
    private static void checkResponseError(JSONObject responseJson)
            throws JSONException, TMDbException {

        // TMDb will return a small JSON with "status_code" and "status_message" keys if the request failed.
        if (responseJson.has(TMDB_STATUS_CODE)) {
            final int SUCCESS_STATUS_CODE = 1;
            int statusCode = responseJson.getInt(TMDB_STATUS_CODE);
            String statusMessage = responseJson.getString(TMDB_STATUS_MESSAGE);

            if (statusCode != SUCCESS_STATUS_CODE) {
                String errorMessage = "Error (" + statusCode + "): " + statusMessage;
                throw new TMDbException(errorMessage);
            }
        }
    }

    /**
     * Parses a movie details response from a movie details request.
     * @param response String response of the movie details request.
     * @return A TMDbMovie object.
     * @throws JSONException Thrown when JSON parsing fails.
     */
    public static TMDbMovie parseMovieDetailsResponse(String response)
        throws JSONException, TMDbException {

        // Start parsing the given string as a JSON object.
        JSONObject responseJson = new JSONObject(response);
        checkResponseError(responseJson);

        return new TMDbMovie(responseJson);
    }

    /**
     * Parses the JSON response for a movie list request.
     *
     * @param response String response of a movie list request.
     * @return An List of Pairs with the first element as the movie ID and the second element as the poster URL.
     * The order of the the List is in the order of the objects seen in the JSON response.
     */
    public static TMDbMovie[] parseMovieListResponse(String response)
            throws JSONException, TMDbException {

        /* Key for movie list results. */
        final String TMDB_RESULTS = "results";

        // Start parsing the given string as a JSON object.
        JSONObject responseJson = new JSONObject(response);
        checkResponseError(responseJson);

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
