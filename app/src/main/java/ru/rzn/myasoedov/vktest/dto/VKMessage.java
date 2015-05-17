package ru.rzn.myasoedov.vktest.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.VKApiMessage;

/**
 * Created by grisha on 14.05.15.
 */
public class VKMessage extends VKApiMessage implements Parcelable{
    private String userPhoto;
    private boolean isFirst;

    public VKMessage() {
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(isFirst ? (byte) 1 : (byte) 0);
    }

    public VKMessage(Parcel in) {
        super(in);
        this.isFirst = in.readByte() != 0;
    }

    public static Creator<VKMessage> CREATOR = new Creator<VKMessage>() {
        public VKMessage createFromParcel(Parcel source) {
            return new VKMessage(source);
        }

        public VKMessage[] newArray(int size) {
            return new VKMessage[size];
        }
    };
}
