package io.chung.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import io.chung.popularmovies.constants.IntentExtraKeys;
import io.chung.popularmovies.utilities.NetworkUtils;
import io.chung.popularmovies.utilities.TMDbException;
import io.chung.popularmovies.utilities.TMDbUtils;

public class MovieDetailActivity extends AppCompatActivity {

    private ScrollView mMovieDetails;

    private TextView mTitle;
    private ImageView mPoster;
    private TextView mReleaseYear;
    private TextView mRuntime;
    private TextView mVoteAverage;
    private TextView mOverview;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Show the up button in the action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get references to all views in layout
        mMovieDetails = (ScrollView) findViewById(R.id.sv_movie_details);

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mPoster = (ImageView) findViewById(R.id.iv_movie_poster_details);
        mReleaseYear = (TextView) findViewById(R.id.tv_release_year);
        mRuntime = (TextView) findViewById(R.id.tv_runtime);
        mVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        mOverview = (TextView) findViewById(R.id.tv_overview);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_details_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pg_details_loading_indicator);

        // Process the incoming intent
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Executes an asynchronous request for movie details data.
     * @param movieId The TMDb movie ID to request.
     */
    private void getMovieInfo(int movieId) {
        new FetchMovieListTask().execute(movieId);
    }

    /**
     * Shows the movie list and hides and error message text view.
     */
    private void showMovieDetails() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetails.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the error message text view and hids the movie list.
     */
    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mMovieDetails.setVisibility(View.INVISIBLE);
    }

    private class FetchMovieListTask extends AsyncTask<Integer, Void, TMDbMovie> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            } catch (IOException | JSONException | TMDbException e) {
                e.printStackTrace();
            }

            return movie;
        }

        @Override
        protected void onPostExecute(TMDbMovie movie) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movie != null) {
                // Use Picasso to load the poster image.
                Uri posterUri = NetworkUtils.buildPosterUri(movie.posterPath);
                Picasso.with(mPoster.getContext()).load(posterUri).into(mPoster);

                // Year needs to be extracted from the string.
                String year = movie.releaseDate.split("-")[0];
                mReleaseYear.setText(year);

                // Append "min" to runtime
                String runtime = Integer.toString(movie.runtime) + "min";
                mRuntime.setText(runtime);

                // Add denominator for voteAverage
                String voteAverage = Double.toString(movie.voteAverage) + "/10";
                mVoteAverage.setText(voteAverage);

                // Title and overview are fine by themselves
                mTitle.setText(movie.title);
                mOverview.setText(movie.overview);

                showMovieDetails();
            } else {
                showErrorMessage();
            }
        }
    }
}
