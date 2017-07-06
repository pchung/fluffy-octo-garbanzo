package io.chung.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.chung.popularmovies.utilities.NetworkUtils;

public class MovieItemAdapter extends RecyclerView.Adapter<MovieItemAdapter.PosterViewHolder> {

    private static final String TAG = MovieItemAdapter.class.getSimpleName();

    private final ListItemClickHandler mClickHandler;

    private TMDbMovie[] mMovieList;

    /**
     * Interface that receives onClick messages.
     */
    public interface ListItemClickHandler {
        void onListItemClick(TMDbMovie movie);
    }

    /**
     * Creates a MovieItemAdapter.
     * @param clickHandler The on-click handler for this adapter.
     *                     This single handler is called when an item is clicked.
     */
    public MovieItemAdapter(ListItemClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PosterViewHolder viewHolder = new PosterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        String posterPath = mMovieList[position].posterPath;
        Uri posterUri = NetworkUtils.buildPosterUri(posterPath);

        Context context = holder.mMoviePoster.getContext();
        Picasso.with(context).load(posterUri).into(holder.mMoviePoster);

        Log.d(TAG, "Binding image URI: " + posterUri.toString());
    }

    @Override
    public int getItemCount() {
        if (mMovieList != null) {
            return mMovieList.length;
        } else {
            return 0;
        }
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        ImageView mMoviePoster;

        public PosterViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TMDbMovie movie = mMovieList[position];

            mClickHandler.onListItemClick(movie);
        }
    }

    public void setMovieData(TMDbMovie[] movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }
}
