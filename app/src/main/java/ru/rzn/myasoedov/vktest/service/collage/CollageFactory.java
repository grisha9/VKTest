package ru.rzn.myasoedov.vktest.service.collage;

import java.util.List;

import ru.rzn.myasoedov.vktest.dto.VKUser;

/**
 * Created by grisha on 16.05.15.
 */
public class CollageFactory {

    public static Collage getInstance(List<VKUser> users) {
        if (users.isEmpty() || users.size() > 4) {
            throw new UnsupportedOperationException("Invalid users count. collage support for 1-4 users");
        }
        switch (users.size()) {
            case 2:
                return new CollageTwoImages(users);
            case 3:
                return new CollageThreeImages(users);
            case 4:
                return new CollageFourImages(users);
            default:
                return new CollageOneImage(users.get(0));
        }
    }
}
