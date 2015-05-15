package ru.rzn.myasoedov.vktest.dto;

import com.vk.sdk.api.model.VKApiMessage;

/**
 * Created by grisha on 14.05.15.
 */
public class VKMessage extends VKApiMessage{
    private String userPhoto;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
