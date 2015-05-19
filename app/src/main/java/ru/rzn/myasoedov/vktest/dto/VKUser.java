package ru.rzn.myasoedov.vktest.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.VKApiUser;

/**
 * Created by grisha on 13.05.15.
 */
public class VKUser extends VKApiUser implements Parcelable{

    public VKUser() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VKUser vkUser = (VKUser) o;

        return id == vkUser.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public VKUser(Parcel in) {
        super(in);
    }

    public static Creator<VKUser> CREATOR = new Creator<VKUser>() {
        public VKUser createFromParcel(Parcel source) {
            return new VKUser(source);
        }

        public VKUser[] newArray(int size) {
            return new VKUser[size];
        }
    };
}
