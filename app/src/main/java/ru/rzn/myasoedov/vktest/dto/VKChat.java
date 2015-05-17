//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

/**
 * Chat.java
 * vk-android-sdk
 *
 * Created by Babichev Vitaly on 19.01.14.
 * Copyright (c) 2014 VK. All rights reserved.
 */
package ru.rzn.myasoedov.vktest.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.VKApiChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Chat object describes a user's chat.
 */
@SuppressWarnings("unused")
public class VKChat extends VKApiChat implements Parcelable {
    protected String preview;
    protected String photoUrl;
    protected String customPhotoUrl;
    protected long date;
    protected List<Integer> collageUsers;

    public VKChat() {
        collageUsers = new LinkedList<>();
    }

    @Override
    public VKChat parse(JSONObject source) {
        try {
            JSONObject message = source.getJSONObject("message");
            int chatId = message.getInt("chat_id");
            super.parse(message);
            id = chatId;
            preview = message.optString("body");
            date = message.optLong("date");
            photoUrl = message.optString("photo_50");
            JSONArray users = message.optJSONArray("chat_active");
            if(users != null) {
                this.users = new int[users.length()];
                for(int i = 0; i < this.users.length; i++) {
                    this.users[i] = users.optInt(i);
                }
            }

        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCustomPhotoUrl() {
        return customPhotoUrl;
    }

    public void setCustomPhotoUrl(String customPhotoUrl) {
        this.customPhotoUrl = customPhotoUrl;
    }

    public List<Integer> getCollageUsers() {
        return collageUsers;
    }

    public void setCollageUsers(List<Integer> collageUsers) {
        this.collageUsers = collageUsers;
    }

    public List<Integer> getUsers() {
        ArrayList<Integer> result = new ArrayList<>();
        if (users != null) {
            for(Integer id : users) {
                result.add(id);
            }
        }
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.preview);
        dest.writeString(this.photoUrl);
        dest.writeString(this.customPhotoUrl);
        dest.writeLong(this.date);
    }

    public VKChat(Parcel in) {
        super(in);
        this.preview = in.readString();
        this.photoUrl = in.readString();
        this.customPhotoUrl = in.readString();
        this.date = in.readLong();
    }

    public static Creator<VKApiChat> CREATOR = new Creator<VKApiChat>() {
        public VKApiChat createFromParcel(Parcel source) {
            return new VKChat(source);
        }

        public VKChat[] newArray(int size) {
            return new VKChat[size];
        }
    };
}
