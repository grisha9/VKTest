package ru.rzn.myasoedov.vktest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.rzn.myasoedov.vktest.R;
import ru.rzn.myasoedov.vktest.dto.VKMessage;
import ru.rzn.myasoedov.vktest.dto.VKMessageWrapper;

/**
 * Created by grisha on 14.05.15.
 */
public class MessageCursorAdapter extends CursorAdapter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private final LayoutInflater inflater;

    public MessageCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.message = (TextView) view.findViewById(R.id.title);
        viewHolder.date = (TextView) view.findViewById(R.id.date);
        viewHolder.image = (ImageView) view.findViewById(R.id.image);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        VKMessage message = VKMessageWrapper.getMessageFromCursor(cursor);
        viewHolder.message.setText(message.body);
        viewHolder.date.setText(dateFormat.format(new Date(message.date * 1000)));
        if (message.getUserPhoto() != null) {
            try {
                Picasso.with(context)
                        .load(message.getUserPhoto())
                        .into(viewHolder.image);
            } catch (Exception e) {
                Log.e("!!!!" + message.getUserPhoto(), "hgfghfh");
            }
        }
    }

    @Override
    public VKApiMessage getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return VKMessageWrapper.getMessageFromCursor(cursor);
    }


    private class ViewHolder {
        TextView date;
        TextView message;
        ImageView image;
    }
}
