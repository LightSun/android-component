package com.heaven7.android.component.search;

import android.content.Context;

import androidx.annotation.WorkerThread;

import com.heaven7.java.base.util.Disposable;
import com.heaven7.java.base.util.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * the search manager.
 *
 * @author heaven7
 */
public final class SearchManager {

    private final AtomicBoolean mSearching = new AtomicBoolean(false);
    private final Context mContext;
    private final Callback mCallback;
    private Scheduler mResultScheduler;
    private String mPendingSearch;

    private final List<Disposable> mTasks = new CopyOnWriteArrayList<>();

    public SearchManager(Context mContext, Callback mCallback) {
        this.mContext = mContext.getApplicationContext();
        this.mCallback = mCallback;
    }

    public Context getContext() {
        return mContext;
    }

    public void setResultScheduler(Scheduler scheduler) {
        this.mResultScheduler = scheduler;
    }

    public boolean doSearch(String text, final SearchCallback cb) {
        if (mSearching.compareAndSet(false, true)) {
            new AllSearcher(new SearchCallback() {
                @Override
                public void onSearchStart() {
                    cb.onSearchStart();
                }

                @Override
                public void addSearchResult(List<?> items) {
                    cb.addSearchResult(items);
                }

                @Override
                public void onSearchEnd() {
                    cb.onSearchEnd();
                    if (mPendingSearch != null) {
                        String text = mPendingSearch;
                        mPendingSearch = null;
                        doSearch(text, cb);
                    }
                }
            }).search(text);
            return true;
        } else {
            onSearching();
            mPendingSearch = text;
            return false;
        }
    }
    protected void onSearching() {

    }

    public void cancel() {
        mSearching.compareAndSet(true, false);
        for (Disposable d : mTasks){
            d.dispose();
        }
        mTasks.clear();
    }

    private class AllSearcher {
        private final SearchCallback cb;
        private final AtomicReference<Disposable> mCurrent = new AtomicReference<>();

        AllSearcher(SearchCallback cb) {
            this.cb = cb;
        }

        public void search(final String searchText) {
            cb.onSearchStart();
            mCallback.addAsyncTask(new Runnable() {
                @Override
                public void run() {
                    for (; mSearching.get(); ) {
                        final ArrayList list = new ArrayList<>();
                        boolean shouldBreak = mCallback.onSearch(searchText, list);

                        dispatchResult(list);

                        if (shouldBreak) {
                            dispatchEnd();
                            break;
                        }
                    }
                }
            });
        }
        private void dispatchResult(List<?> list){
            if (mResultScheduler != null) {
                dispatch0(new Runnable() {
                    @Override
                    public void run() {
                        cb.addSearchResult(list);
                    }
                });
            } else {
                cb.addSearchResult(list);
            }
        }
        private void dispatchEnd(){
            if (mResultScheduler != null) {
                dispatch0(new Runnable() {
                    @Override
                    public void run() {
                        cb.onSearchEnd();
                        mSearching.compareAndSet(true, false);
                    }
                });
            } else {
                cb.onSearchEnd();
            }
        }
        private void dispatch0(Runnable task){
            Disposable t = mResultScheduler.newWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    Disposable t = mCurrent.getAndSet(null);
                    if(t != null){
                        mTasks.remove(t);
                    }
                    task.run();
                }
            });
            do {
                if(mCurrent.compareAndSet(null, t)){
                    break;
                }
            }while (mSearching.get());
            mTasks.add(t);
        }
    }

    public interface Callback {

        void addAsyncTask(Runnable task);
        /**
         * called on search
         * @param text the text
         * @param out the out list
         * @return true if should break search
         */
        @WorkerThread
        boolean onSearch(String text, List<?> out);
    }

}
