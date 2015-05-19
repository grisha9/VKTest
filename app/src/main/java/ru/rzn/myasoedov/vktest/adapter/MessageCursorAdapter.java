package ru.rzn.myasoedov.vktest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiPhoto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.rzn.myasoedov.vktest.R;
import ru.rzn.myasoedov.vktest.dto.VKMessage;
import ru.rzn.myasoedov.vktest.dto.VKMessageWrapper;

/**
 * Created by grisha on 14.05.15.
 */
public class MessageCursorAdapter extends CursorAdapter {
    private static final int MY_TEXT_MESSAGE = 0;
    private static final int MY_TEXT_MESSAGE_FIRST = 1;
    private static final int MY_PHOTO_MESSAGE = 2;
    private static final int MY_PHOTO_MESSAGE_FIRST = 3;
    private static final int TEXT_MESSAGE = 4;
    private static final int TEXT_MESSAGE_FIRST = 5;
    private static final int PHOTO_MESSAGE = 6;
    private static final int PHOTO_MESSAGE_FIRST = 7;
    private static final int VIEW_HOLDER = 2123456789;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private final LayoutInflater inflater;

    public MessageCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        VKMessage message = getItem(position);
        if (message.out && !message.attachments.isEmpty() && message.isFirst()) {
            return MY_PHOTO_MESSAGE_FIRST;
        } else if (message.out && !message.attachments.isEmpty()) {
            return MY_PHOTO_MESSAGE;
        } else if (message.out && message.isFirst()) {
            return MY_TEXT_MESSAGE_FIRST;
        } else if (message.out) {
            return MY_TEXT_MESSAGE;
        } else if (!message.attachments.isEmpty() && message.isFirst()) {
            return PHOTO_MESSAGE_FIRST;
        } else if (!message.attachments.isEmpty()) {
            return PHOTO_MESSAGE;
        } else if (message.isFirst()) {
            return TEXT_MESSAGE_FIRST;
        } else {
            return TEXT_MESSAGE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        int itemViewType = getItemViewType(cursor.getPosition());
        View view;
        switch (itemViewType) {
            case MY_TEXT_MESSAGE:
                view = inflater.inflate(R.layout.message_item_my, parent, false);
                break;
            case MY_TEXT_MESSAGE_FIRST:
                view = inflater.inflate(R.layout.message_item_my_first, parent, false);
                break;
            case MY_PHOTO_MESSAGE:
                view = inflater.inflate(R.layout.message_item_my_photo, parent, false);
                initViewHolderImages(viewHolder, view);
                break;
            case MY_PHOTO_MESSAGE_FIRST:
                view = inflater.inflate(R.layout.message_item_my_photo_first, parent, false);
                initViewHolderImages(viewHolder, view);
                break;
            case TEXT_MESSAGE:
                view = inflater.inflate(R.layout.message_item, parent, false);
                viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
                break;
            case TEXT_MESSAGE_FIRST:
                view = inflater.inflate(R.layout.message_item_first, parent, false);
                viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
                break;
            case PHOTO_MESSAGE_FIRST:
                view = inflater.inflate(R.layout.message_item_photo_first, parent, false);
                viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
                initViewHolderImages(viewHolder, view);
                break;
            default:
                view = inflater.inflate(R.layout.message_item_photo, parent, false);
                viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
                initViewHolderImages(viewHolder, view);
                break;
        }

        viewHolder.message = (TextView) view.findViewById(R.id.message);
        viewHolder.date = (TextView) view.findViewById(R.id.date);
        view.setTag(VIEW_HOLDER, viewHolder);
        return view;
    }

    private void initViewHolderImages(ViewHolder viewHolder, View view) {
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image1));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image2));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image3));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image4));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image5));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image6));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image7));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image8));
        viewHolder.imageViews.add((ImageView) view.findViewById(R.id.image9));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(VIEW_HOLDER);
        VKMessage message = VKMessageWrapper.getMessageFromCursor(cursor);
        viewHolder.message.setText(message.body);
        viewHolder.date.setText(dateFormat.format(new Date(message.date * 1000)));
        if (viewHolder.avatar != null) {
            try {
                ImageLoader.getInstance().displayImage(message.getUserPhoto(), viewHolder.avatar);
            } catch (Exception e) {
                Log.e("!!!!" + message.getUserPhoto(), e.toString());
            }
        }

        bindImages(viewHolder, message);

    }

    private void bindImages(ViewHolder viewHolder, VKMessage message) {
        if (!message.attachments.isEmpty()) {
            for (int i = 0; i < message.attachments.size(); i++) {
                VKApiPhoto attachment = (VKApiPhoto) message.attachments.get(i);
                viewHolder.imageViews.get(i).setVisibility(View.VISIBLE);
                try {
                    ImageLoader.getInstance().displayImage(attachment.photo_130,
                            viewHolder.imageViews.get(i));
                } catch (Exception e) {
                    Log.e("!!!!" + message.getUserPhoto(), e.toString());
                }
            }
            for (int i = message.attachments.size(); i < viewHolder.imageViews.size(); i++) {
                viewHolder.imageViews.get(i).setVisibility(View.GONE);
            }

            viewHolder.message.setVisibility(TextUtils.isEmpty(message.body) ? View.GONE
                    : View.VISIBLE);
        }
    }

    @Override
    public VKMessage getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return VKMessageWrapper.getMessageFromCursor(cursor);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private class ViewHolder {
        TextView date;
        TextView message;
        ImageView avatar;
        List<ImageView> imageViews = new LinkedList<>();
    }
}
