package ru.rzn.myasoedov.vktest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import ru.rzn.myasoedov.vktest.R;
import ru.rzn.myasoedov.vktest.adapter.DialogCursorAdapter;
import ru.rzn.myasoedov.vktest.db.DialogProvider;
import ru.rzn.myasoedov.vktest.dto.VKChat;
import ru.rzn.myasoedov.vktest.service.VKService;

/**
 * Created by grisha on 11.05.15.
 */
public class DialogsFragment extends SwipeRefreshListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DialogsFragment.class.getSimpleName();
    private BroadcastReceiver receiver;

    public static DialogsFragment newInstance() {
        Bundle bundle = new Bundle();
        DialogsFragment dialogsFragment = new DialogsFragment();
        dialogsFragment.setArguments(bundle);
        return dialogsFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.no_dialogs));
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        getActivity().getWindow().setBackgroundDrawableResource(R.color.background);
        VKService.getDialogs();
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VKService.getDialogs();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        prepareActionBar();
        getLoaderManager().initLoader(DialogProvider.URI_ALL_DIALOGS, null, this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setRefreshing(false);
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(VKService.ACTION_DIALOG_SYNC_FINISH));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    private void prepareActionBar() {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.dialogs_title);
            actionBar.setSubtitle("");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        VKChat chat = ((DialogCursorAdapter) getListAdapter()).getItem(position);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, MessageFragment.newInstance(chat))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case DialogProvider.URI_ALL_DIALOGS:
                return new CursorLoader(
                        getActivity(), DialogProvider.DIALOG_CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (getListAdapter() == null) {
            setListAdapter(new DialogCursorAdapter(getActivity(), cursor, 0));
        } else {
            ((CursorAdapter) getListAdapter()).swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
