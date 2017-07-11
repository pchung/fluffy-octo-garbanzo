package io.chung.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import io.chung.popularmovies.constants.IntentExtraKeys;
import io.chung.popularmovies.utilities.NetworkUtils;
import io.chung.popularmovies.utilities.TMDbUtils;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mDetails = (TextView) findViewById(R.id.tv_movie_details);

        Intent incomingIntent = getIntent();

        if (incomingIntent != null) {
            if (incomingIntent.hasExtra(IntentExtraKeys.MOVIE_ID)) {
                int movieId = incomingIntent.getIntExtra(IntentExtraKeys.MOVIE_ID, -1);

                if (movieId != -1) {
                    getMovieInfo(movieId);
                }
            }
        }
    }

    private void getMovieInfo(int movieId) {
        new FetchMovieListTask().execute(movieId);
    }

    private class FetchMovieListTask extends AsyncTask<Integer, Void, TMDbMovie> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // TODO: Show loading indicator
        }

        @Override
        protected TMDbMovie doInBackground(Integer... movieIds) {
            // Only one sort criterion is expected, but if none are passed, return null.
            if (movieIds.length == 0) {
                return null;
            }

            int movieId = movieIds[0];
            String apiKey = getString(R.string.the_movie_db_api_key);
            URL url = NetworkUtils.buildMovieDetailsUrl(movieId, apiKey);

            TMDbMovie movie = null;

            try {
                String response = NetworkUtils.getResponseFromUrl(url);
                movie = TMDbUtils.parseMovieDetailsResponse(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return movie;
        }

        @Override
        protected void onPostExecute(TMDbMovie movie) {
            if (movie != null) {
                String detailsText = "Title: " + movie.title + "\nRelease Date: " + movie.releaseDate +
                        "\nPoster URL: " + movie.posterPath + "\nVote Average: " + movie.voteAverage +
                        "\nSynopsis: " + movie.overview;

                mDetails.setText(detailsText);
            }
        }
    }
}
