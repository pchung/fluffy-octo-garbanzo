package io.chung.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import io.chung.popularmovies.constants.IntentExtraKeys;
import io.chung.popularmovies.utilities.NetworkUtils;
import io.chung.popularmovies.utilities.TMDbUtils;

public class MainActivity extends AppCompatActivity
        implements MovieItemAdapter.ListItemClickHandler {

     /* Reference to RecyclerView. */
    private RecyclerView mMovieList;

    /* Reference to RecyclerView's adapter. */
    private MovieItemAdapter mMovieItemAdapter;

    @Override
    public void onListItemClick(TMDbMovie movie) {
        Context context = this;
        Class destClass = MovieDetailActivity.class;

        Intent intent = new Intent(context, destClass);
        intent.putExtra(IntentExtraKeys.MOVIE_ID, movie.id);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and its adapter
        GridLayoutManager layoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        mMovieList = (RecyclerView) findViewById(R.id.rv_movie_list);
        mMovieList.setLayoutManager(layoutManager);
        mMovieList.setHasFixedSize(true);

        // TODO: Figure out what to pass to the adapter
        mMovieItemAdapter = new MovieItemAdapter(this);
        mMovieList.setAdapter(mMovieItemAdapter);

        loadMovieData(NetworkUtils.SortCriteria.POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                setTitle(R.string.title_popular);
                item.setChecked(true);
                loadMovieData(NetworkUtils.SortCriteria.POPULAR);
                return true;
            case R.id.action_sort_top_rated:
                setTitle(R.string.title_top_rated);
                item.setChecked(true);
                loadMovieData(NetworkUtils.SortCriteria.TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Executes an asynchronous request for the movie list.
     * @param sortCriteria Indicates which sorting of the movie list to be requested.
     */
    private void loadMovieData(NetworkUtils.SortCriteria sortCriteria) {
        new FetchMovieListTask().execute(sortCriteria);
    }

    private class FetchMovieListTask extends AsyncTask<NetworkUtils.SortCriteria, Void, TMDbMovie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // TODO: Show loading indicator
        }

        @Override
        protected TMDbMovie[] doInBackground(NetworkUtils.SortCriteria... sortCriteria) {
            // Only one sort criterion is expected, but if none are passed, return null.
            if (sortCriteria.length == 0) {
                return null;
            }

            TMDbMovie[] movieList = null;

            NetworkUtils.SortCriteria criterion = sortCriteria[0];
            String apiKey = getString(R.string.the_movie_db_api_key);

            try {
                URL url = NetworkUtils.buildMovieListUrl(criterion, apiKey);
                String response = NetworkUtils.getResponseFromUrl(url);

                movieList = TMDbUtils.parseMovieListResponse(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return movieList;
        }

        @Override
        protected void onPostExecute(TMDbMovie[] movies) {
            if (movies != null) {
                // TODO: Show the recyclerview and hide error
                mMovieItemAdapter.setMovieData(movies);
            }
        }
    }
}
