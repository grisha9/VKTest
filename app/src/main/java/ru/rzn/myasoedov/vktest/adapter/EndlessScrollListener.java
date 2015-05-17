package ru.rzn.myasoedov.vktest.adapter;

import android.widget.AbsListView;

/**
 * Created by grisha on 03.05.15.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 5;

    private int previousTotalItemCount = 0;

    private boolean loading = false;


    public EndlessScrollListener() {
    }


    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount)
    {
        if (totalItemCount < previousTotalItemCount) {

            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            previousTotalItemCount = totalItemCount;
        }


        if (!loading && totalItemCount > 0 && visibleItemCount > 0
                && (firstVisibleItem - visibleThreshold  <= 0)) {
            onLoadMore(totalItemCount);
            loading = true;
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public abstract void onLoadMore(int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}