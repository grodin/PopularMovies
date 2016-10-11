/*
 * Copyright (c) 2016 Fabio Berta
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

package ch.berta.fabio.popularmovies.presentation.details.onl;

import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.berta.fabio.popularmovies.R;
import ch.berta.fabio.popularmovies.data.repositories.MovieRepository;
import ch.berta.fabio.popularmovies.domain.models.Movie;
import ch.berta.fabio.popularmovies.domain.models.MovieDetails;
import ch.berta.fabio.popularmovies.presentation.details.MovieDetailsViewModelBaseImpl;
import ch.berta.fabio.popularmovies.presentation.details.fav.MovieDetailsViewModelFavImpl;
import rx.Observable;
import rx.Subscriber;

/**
 * Provides an implementation of the {@link MovieDetailsViewModelOnl} interface.
 * <p/>
 * Subclass of {@link MovieDetailsViewModelBaseImpl}.
 */
public class MovieDetailsViewModelOnlImpl extends
        MovieDetailsViewModelBaseImpl<MovieDetailsViewModelOnl.ViewInteractionListener>
        implements MovieDetailsViewModelOnl {

    /**
     * Constructs a new {@link MovieDetailsViewModelFavImpl}.
     *
     * @param savedState      the bundle to recover state from
     * @param movieRepository the movie repository for local inserts and deletes
     * @param movie           the movie to use
     * @param useTwoPane      whether the view uses two panes or not
     */
    public MovieDetailsViewModelOnlImpl(@Nullable Bundle savedState,
                                        @NonNull MovieRepository movieRepository,
                                        @NonNull Movie movie, boolean useTwoPane) {
        super(savedState, movieRepository, useTwoPane);

        this.movie = movie;
    }

    @Override
    @Bindable
    public boolean isMovieFavoured() {
        return movie != null && movie.isFavoured();
    }

    @Override
    public void loadMovieDetails() {
        if (!movie.areReviewsAndVideosSet()) {
            view.loadQueryMovieDetailsWorker(getMovieDbId());
        }
    }

    @Override
    public void onMenuInflation() {
        setYoutubeShareUrl();
    }

    @Override
    public int getMovieDbId() {
        return movie.getDbId();
    }

    @Override
    public void onMovieDeleted() {
        super.onMovieDeleted();

        view.restartLoader();
    }

    @Override
    protected void onMovieDeletedOnePane() {
        view.showMessage(R.string.snackbar_movie_removed_from_favorites, null);
    }

    @Override
    public void setQueryMovieDetailsStream(@NonNull Observable<MovieDetails> observable, @NonNull final String workerTag) {
        subscriptions.add(observable.subscribe(new Subscriber<MovieDetails>() {
            @Override
            public void onCompleted() {
                view.removeWorker(workerTag);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWorker(workerTag);
                view.showMessage(R.string.snackbar_movie_load_reviews_videos_failed, null);
            }

            @Override
            public void onNext(MovieDetails movieDetails) {
                movie.setReviews(movieDetails.getReviewsPage().getReviews());
                movie.setVideos(movieDetails.getVideosPage().getVideos());
                movie.setReviewsAndVideosSet(true);

                setYoutubeShareUrl();
                view.invalidateOptionsMenu();

                setReviewsAndVideosCount();
                view.notifyItemRangeInserted(adjustPosForTwoPane(1),
                        getNumberOfHeaderRows() + reviewsCount + videosCount);
            }
        }));
    }

    @Override
    public void onWorkerError(@NonNull String workerTag) {
        super.onWorkerError(workerTag);

        view.showMessage(R.string.snackbar_movie_load_reviews_videos_failed, null);
    }
}
