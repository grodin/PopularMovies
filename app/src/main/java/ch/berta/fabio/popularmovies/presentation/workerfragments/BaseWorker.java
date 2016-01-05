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

package ch.berta.fabio.popularmovies.presentation.workerfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import rx.subscriptions.CompositeSubscription;

/**
 * Provides an abstract base class for worker fragments that are retained across configuration
 * changes in order to to async tasks.
 */
public abstract class BaseWorker<T> extends Fragment {

    private static final String LOG_TAG = BaseWorker.class.getSimpleName();
    final CompositeSubscription mSubscriptions = new CompositeSubscription();
    @Nullable
    T mActivity;

    public BaseWorker() {
        // required empty constructor
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mActivity = (T) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement WorkerInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        final Bundle args = getArguments();
        if (args == null) {
            onWorkerError();
            return;
        }

        startWork(args);
    }

    protected abstract void onWorkerError();

    protected abstract void startWork(@NonNull Bundle args);

    @Override
    public void onDetach() {
        super.onDetach();

        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSubscriptions.hasSubscriptions()) {
            mSubscriptions.unsubscribe();
        }
    }
}