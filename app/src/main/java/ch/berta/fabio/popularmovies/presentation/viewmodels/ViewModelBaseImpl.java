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

package ch.berta.fabio.popularmovies.presentation.viewmodels;

import android.databinding.BaseObservable;
import android.support.annotation.CallSuper;

/**
 * Provides an abstract base implementation of the {@link ViewModel} interface.
 */
public abstract class ViewModelBaseImpl<T> extends BaseObservable implements ViewModel<T> {

    T mView;

    @Override
    @CallSuper
    public void attachView(T view) {
        mView = view;
    }

    @Override
    @CallSuper
    public void detachView() {
        mView = null;
    }
}