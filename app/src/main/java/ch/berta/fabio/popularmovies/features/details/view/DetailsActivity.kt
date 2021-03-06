/*
 * Copyright (c) 2017 Fabio Berta
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

package ch.berta.fabio.popularmovies.features.details.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import ch.berta.fabio.popularmovies.*
import ch.berta.fabio.popularmovies.data.MovieStorage
import ch.berta.fabio.popularmovies.databinding.ActivityMovieDetailsBinding
import ch.berta.fabio.popularmovies.di.ApplicationComponent
import ch.berta.fabio.popularmovies.features.base.BaseActivity
import ch.berta.fabio.popularmovies.features.base.BaseFragment
import ch.berta.fabio.popularmovies.features.details.component.DetailsState
import ch.berta.fabio.popularmovies.features.details.vdos.DetailsHeaderViewData
import ch.berta.fabio.popularmovies.features.details.viewmodel.DetailsViewModel
import ch.berta.fabio.popularmovies.features.details.viewmodel.DetailsViewModelFactory
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import javax.inject.Inject

@PaperParcel
data class DetailsArgs(
        val id: Int,
        val title: String,
        val releaseDate: String,
        val overview: String,
        val voteAverage: Double,
        val poster: String?,
        val backdrop: String?,
        val fromFavList: Boolean
) : PaperParcelable {
    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR = PaperParcelDetailsArgs.CREATOR
    }
}

/**
 * Presents the backdrop image of a selected movie in a collapsing toolbar.
 */
class DetailsActivity : BaseActivity(), BaseFragment.ActivityListener {

    @Inject
    lateinit var movieStorage: MovieStorage
    private val viewModel by lazy {
        val args = intent.getParcelableExtra<DetailsArgs>(KEY_ACTIVITY_ARGS)
        val factory = DetailsViewModelFactory(movieStorage, args)
        ViewModelProviders.of(this, factory).get(DetailsViewModel::class.java)
    }
    private val component: ApplicationComponent by lazy { PopularMovies.getAppComponent(this) }
    private val viewData = DetailsHeaderViewData()
    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMovieDetailsBinding>(this, R.layout.activity_movie_details)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // enter transition will start when movie poster is loaded
        supportPostponeEnterTransition()

        component.inject(this)
        binding.viewData = viewData

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        initViewModel()
        binding.fabDetailsFavorite.setOnClickListener { viewModel.uiEvents.favClicks.accept(Unit) }

        if (savedInstanceState == null) {
            addFragment()
        }
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer<DetailsState> {
            it?.let { render(it) }
        })
        viewModel.navigation
                .bindTo(lifecycle)
                .subscribe { navigateTo(this, it) }
    }

    private fun render(state: DetailsState) {
        viewData.title = state.title
        viewData.backdrop = state.backdrop
        viewData.favoured = state.favoured
        if (state.snackbar.show) {
            Snackbar.make(binding.container, state.snackbar.message, Snackbar.LENGTH_LONG).show()
            viewModel.uiEvents.snackbarShown.accept(Unit)
        }
    }

    private fun addFragment() {
        val args = intent.getParcelableExtra<DetailsArgs>(KEY_ACTIVITY_ARGS)
        val fragment = DetailsFragment.newInstance(args)
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment, fragment.javaClass.canonicalName)
                .commit()
    }
}
