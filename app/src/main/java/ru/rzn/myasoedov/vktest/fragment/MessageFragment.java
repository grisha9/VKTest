package ru.rzn.myasoedov.vktest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiMessage;

import ru.rzn.myasoedov.vktest.R;
import ru.rzn.myasoedov.vktest.adapter.EndlessScrollListener;
import ru.rzn.myasoedov.vktest.adapter.MessageCursorAdapter;
import ru.rzn.myasoedov.vktest.db.MessageProvider;
import ru.rzn.myasoedov.vktest.dto.VKChat;
import ru.rzn.myasoedov.vktest.service.VKService;

/**
 * Created by grisha on 13.05.15.
 */
public class MessageFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String CHAT = "chat";
    public static final String CHAT_ID = "chatId";
    public static final String CUSTOM_PHOTO_URL = "customPhotoUrl";
    public static final int ITEM_ON_PAGE = 50;
    private VKChat chat;
    private BroadcastReceiver receiver;
    private EndlessScrollListener scrollListener;

    public static MessageFragment newInstance(VKChat chat) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHAT, chat);

        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        chat = getArguments().getParcelable(CHAT);
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.chat_gradient);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareActionBar();
        getLoaderManager().initLoader(MessageProvider.URI_ALL_MESSAGE, null, this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case VKService.ACTION_MESSAGE_SYNC_ERROR:
                        scrollListener.setLoading(false);
                        break;
                    case VKService.ACTION_DIALOG_AVATAR_SYNC:
                        if (intent.getIntExtra(CHAT_ID, -1) == chat.getId()) {
                            chat.setCustomPhotoUrl(intent.getStringExtra(CUSTOM_PHOTO_URL));
                            setChatAvatar();
                        }
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(VKService.ACTION_MESSAGE_SYNC_ERROR);
        filter.addAction(VKService.ACTION_DIALOG_AVATAR_SYNC);
        getActivity().registerReceiver(receiver, filter);
    }

    private void prepareActionBar() {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(chat.title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle((chat.getUsers().size() + 1) + " "
                    + getString(R.string.participants));
            setChatAvatar();
        }
    }

    private void setChatAvatar() {
        ImageView chatAvatar = (ImageView) getActivity().findViewById(R.id.chat_avatar);
        chatAvatar.setVisibility(View.VISIBLE);
        String url = TextUtils.isEmpty(chat.getPhotoUrl()) ? chat.getCustomPhotoUrl()
                : chat.getPhotoUrl();
        ImageLoader.getInstance().displayImage(url, chatAvatar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setStackFromBottom(true);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        if (scrollListener == null) {
            scrollListener = new EndlessScrollListener() {
                @Override
                public void onLoadMore(int totalItemsCount) {
                    VKApiMessage message = ((MessageCursorAdapter) getListAdapter()).getItem(0);
                    VKService.getMessageByChatId(chat.getId(), false, ITEM_ON_PAGE, message.getId());
                }
            };
        }
        getListView().setOnScrollListener(scrollListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
        View chatAvatar = getActivity().findViewById(R.id.chat_avatar);
        chatAvatar.setVisibility(View.GONE);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case MessageProvider.URI_ALL_MESSAGE:
                VKService.getMessageByChatId(chat.getId(), true, ITEM_ON_PAGE, null);
                return new CursorLoader(getActivity(), MessageProvider.MESSAGE_CONTENT_URI, null,
                        MessageProvider.QUERY_SELECTION, new String[]{String.valueOf(chat.getId())}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (getListAdapter() == null) {
            setListAdapter(new MessageCursorAdapter(getActivity(), cursor, 0));
        } else {
            saveScrollPositionOnCursorChange(cursor.getCount(), getListAdapter().getCount());
            ((CursorAdapter) getListAdapter()).swapCursor(cursor);
            scrollListener.setLoading(false);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private void saveScrollPositionOnCursorChange(int newCursorItems, int oldCursorItems) {
        final int offset = newCursorItems - oldCursorItems;
        if (offset != 0) {
            getListView().post(new Runnable() {
                @Override
                public void run() {
                    int index = getListView().getFirstVisiblePosition();
                    View v = getListView().getChildAt(0);
                    int top = (v == null) ? 0 : (v.getTop() - getListView().getPaddingTop());
                    getListView().setSelectionFromTop(index + offset, top);

                    scrollListener.setLoading(false);
                }
            });
        }
    }
}
