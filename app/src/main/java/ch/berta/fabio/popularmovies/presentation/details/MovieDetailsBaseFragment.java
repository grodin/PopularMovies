/*
 * Copyright (c) 2015 Fabio Berta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.berta.fabio.popularmovies.presentation.details;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import ch.berta.fabio.popularmovies.R;
import ch.berta.fabio.popularmovies.data.repositories.MovieRepository;
import ch.berta.fabio.popularmovies.presentation.common.BaseFragment;

/**
 * Provides a base class for the display of detail information about a movie, including poster
 * image, release date, rating, an overview of the plot, reviews and trailers.
 */
public abstract class MovieDetailsBaseFragment<T extends MovieDetailsViewModel>
        extends BaseFragment<T>
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieDetailsViewModel.ViewInteractionListener {

    private static final String LOG_TAG = MovieDetailsBaseFragment.class.getSimpleName();
    protected ActivityListener activity;
    protected boolean useTwoPane;
    protected RecyclerView recyclerView;
    @Inject
    protected MovieRepository movieRepo;
    private MovieDetailsRecyclerAdapter recyclerAdapter;
    private Intent shareYoutubeIntent;

    public MovieDetailsBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            activity = (ActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActivityListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        useTwoPane = getResources().getBoolean(R.bool.use_two_pane_layout);

        shareYoutubeIntent = new Intent(Intent.ACTION_SEND);
        shareYoutubeIntent.setType("text/plain");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getRecyclerView();
        recyclerAdapter = new MovieDetailsRecyclerAdapter(getActivity(), viewModel);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);
    }

    protected abstract RecyclerView getRecyclerView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity.setViewModel(viewModel);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_movie_details, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_details_action_share);
        shareItem.setVisible(viewModel.hasMovieVideos());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.menu_details_action_share:
                shareYoutubeUrl();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareYoutubeUrl() {
        startActivity(Intent.createChooser(shareYoutubeIntent, getString(R.string.action_share)));
    }

    @Override
    protected View getSnackbarView() {
        return recyclerView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity = null;
    }

    @Override
    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        recyclerAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void notifyDataChanged() {
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void startPostponedEnterTransition() {
        ActivityCompat.startPostponedEnterTransition(getActivity());
    }

    @Override
    public void invalidateOptionsMenu() {
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void setYoutubeShareUrl(@NonNull String url) {
        shareYoutubeIntent.putExtra(Intent.EXTRA_TEXT, url);
    }

    @Override
    public void startVideoActivity(@NonNull Uri videoUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
        startActivity(intent);
    }

    /**
     * Defines the interaction with the hosting activity.
     */
    public interface ActivityListener {

        void setViewModel(@NonNull MovieDetailsViewModel viewModel);

        /**
         * Hides the details fragment in a two pane layout (on tablets).
         */
        void hideDetailsFragment();
    }
}
