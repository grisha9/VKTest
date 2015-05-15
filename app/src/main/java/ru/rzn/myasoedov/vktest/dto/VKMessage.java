package ru.rzn.myasoedov.vktest.dto;

import com.vk.sdk.api.model.VKApiMessage;

/**
 * Created by grisha on 14.05.15.
 */
public class VKMessage extends VKApiMessage{
    private String userPhoto;
    private boolean isFirst;

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
}
