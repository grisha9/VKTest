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

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.rzn.myasoedov.vktest.R;
import ru.rzn.myasoedov.vktest.dto.VKChat;
import ru.rzn.myasoedov.vktest.dto.VKChatWrapper;

/**
 * Created by grisha on 12.05.15.
 */
public class DialogCursorAdapter extends CursorAdapter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private LayoutInflater inflater;
    public DialogCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.preview = (TextView) view.findViewById(R.id.preview);
        viewHolder.date = (TextView) view.findViewById(R.id.date);
        viewHolder.image = (ImageView) view.findViewById(R.id.image);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        VKChat chat = VKChatWrapper.getChatFromCursor(cursor);
        viewHolder.title.setText(chat.title);
        viewHolder.preview.setText(chat.getPreview());
        viewHolder.date.setText(dateFormat.format(new Date(chat.getDate() * 1000)));
        String url = TextUtils.isEmpty(chat.getPhotoUrl())
                ? chat.getCustomPhotoUrl() : chat.getPhotoUrl();
        try {
            ImageLoader.getInstance().displayImage(url, viewHolder.image);
        } catch (Exception e) {
            Log.e("!!!!" + url, e.toString());
        }
    }

    @Override
    public VKChat getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return VKChatWrapper.getChatFromCursor(cursor);
    }


    private class ViewHolder {
        TextView date;
        TextView title;
        TextView preview;
        ImageView image;
    }

}
