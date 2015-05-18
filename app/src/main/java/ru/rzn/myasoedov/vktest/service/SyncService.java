package ru.rzn.myasoedov.vktest.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.rzn.myasoedov.vktest.VKTest;
import ru.rzn.myasoedov.vktest.db.DialogProvider;
import ru.rzn.myasoedov.vktest.db.MessageProvider;
import ru.rzn.myasoedov.vktest.db.ParticipantProvider;
import ru.rzn.myasoedov.vktest.dto.VKChat;
import ru.rzn.myasoedov.vktest.dto.VKChatWrapper;
import ru.rzn.myasoedov.vktest.dto.VKMessage;
import ru.rzn.myasoedov.vktest.dto.VKMessageWrapper;
import ru.rzn.myasoedov.vktest.dto.VKUser;
import ru.rzn.myasoedov.vktest.dto.VKUserWrapper;
import ru.rzn.myasoedov.vktest.fragment.MessageFragment;
import ru.rzn.myasoedov.vktest.service.collage.CollageFactory;


/**
 * Created by grisha on 11.05.15.
 */
public class SyncService extends IntentService {
    public static final String TAG = SyncService.class.getSimpleName();
    public static final String ACTION_SYNC_DIALOGS = "ru.rzn.myasoedov.vktest.service.SYNC_DIALOGS";
    public static final String ACTION_SYNC_PARTICIPANT = "ru.rzn.myasoedov.vktest.service.SYNC_PARTICIPANT";
    public static final String ACTION_SYNC_MESSAGE = "ru.rzn.myasoedov.vktest.service.SYNC_MESSAGE";
    public static final String ACTION_SYNC_DIALOG_AVATAR = "ru.rzn.myasoedov.vktest.service.SYNC_DIALOG_AVATAR";
    public static final String MODEL_OBJECT = "model-object";
    public static final String DELETE_OLD = "delete-old";
    public static final String CHAT_ID = "chat-id";
    public static final int COLLAGE_MAX_USER = 4;

    private Lock lock = new ReentrantLock();

    public SyncService() {
        super(SyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_SYNC_DIALOGS:
                syncDialog((VKList<VKChat>) intent.getParcelableExtra(MODEL_OBJECT));
                break;
            case ACTION_SYNC_PARTICIPANT:
                syncParticipant((VKList<VKUser>) intent.getParcelableExtra(MODEL_OBJECT));
                break;
            case ACTION_SYNC_MESSAGE:
                int chatId = intent.getIntExtra(CHAT_ID, -1);
                if (chatId > 0) {
                    syncMessage((VKList<VKApiMessage>) intent.getParcelableExtra(MODEL_OBJECT),
                            chatId, intent.getBooleanExtra(DELETE_OLD, false));
                }
                break;
            case ACTION_SYNC_DIALOG_AVATAR:
                syncDialogAvatar(intent);
                break;
        }
    }

    private void syncDialog(VKList<VKChat> vkChats) {
        HashSet<Integer> ids = new HashSet<>();
        LinkedList<ContentValues> values = new LinkedList<>();
        /**
         * update or insert dialogs
         */
        for(VKChat chat : vkChats) {
            ids.add(chat.getId());
            values.add(VKChatWrapper.getContentValues(chat));
        }

        getApplicationContext().getContentResolver().bulkInsert(
                DialogProvider.DIALOG_CONTENT_URI, values.toArray(new ContentValues[values.size()]));

        /**
         * remove old dialogs
         */
        if (!ids.isEmpty()) {
            getApplicationContext().getContentResolver().delete(DialogProvider.DIALOG_CONTENT_URI,
                    DialogProvider.prepareSelectionForDelete(ids), null);
        }
    }

    private void syncParticipant(VKList<VKUser> users) {
        HashSet<VKUser> userSet = new HashSet<>();
        for(VKUser user : users) {
            userSet.add(user);
        }
        for(VKUser user : userSet) {
            getApplicationContext().getContentResolver().insert(
                    ParticipantProvider.PARTICIPANT_CONTENT_URI,
                    VKUserWrapper.getContentValues(user));
        }
    }

    private void syncMessage(VKList<VKApiMessage> messages, int chatId, boolean isDeleteOld) {
        List<VKMessage> vkMessages = markFirstMessages(messages);

        HashSet<Integer> ids = new HashSet<>();
        LinkedList<ContentValues> values = new LinkedList<>();
        for(VKMessage message : vkMessages) {
            values.add(VKMessageWrapper.getContentValues(message, chatId));
            ids.add(message.getId());
        }

        getApplicationContext().getContentResolver().bulkInsert(
                MessageProvider.MESSAGE_CONTENT_URI, values.toArray(new ContentValues[values.size()]));

        /**
         * remove old message
         */
        if (isDeleteOld && !ids.isEmpty()) {
            getApplicationContext().getContentResolver().delete(MessageProvider.MESSAGE_CONTENT_URI,
                    MessageProvider.prepareSelectionForDelete(chatId, ids), null);
        }
    }

    private List<VKMessage> markFirstMessages(VKList<VKApiMessage> messages) {
        LinkedList<VKMessage> vkMessages = new LinkedList<>();
        Collections.reverse(messages);
        VKMessage vkMessage;
        int currentUserId = -1;
        for(VKApiMessage message : messages) {
            vkMessage = new VKMessage();
            vkMessage.id = message.getId();
            vkMessage.date = message.date;
            vkMessage.body = message.body;
            vkMessage.user_id = message.user_id;
            vkMessage.out = message.out;
            vkMessage.attachments = message.attachments;
            if (currentUserId == -1 || currentUserId != message.user_id) {
                vkMessage.setFirst(true);
                currentUserId = message.user_id;
            }

            vkMessages.add(vkMessage);
        }
        return vkMessages;
    }

    private void syncDialogAvatar(Intent intent) {
        Cursor cursor = null;
        if (lock.tryLock()) {
            try {
                cursor = getApplicationContext().getContentResolver().query(DialogProvider
                        .DIALOG_WITHOUT_IMAGE_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    VKChat chat = VKChatWrapper.getChatFromCursor(cursor);
                    VKList<VKUser> users = intent.getParcelableExtra(String.valueOf(chat.id));
                    users = (users != null) ? users : new VKList<VKUser>();

                    if (!users.isEmpty()
                            && collageUserNotActual(chat.getUsers(), chat.getCollageUsers())) {
                        Map<Integer, VKUser> userMap = new HashMap<>();
                        List<VKUser> collageUsers = new LinkedList<>();
                        List<Integer> collageUserIds = new LinkedList<>();
                        for (VKUser user : users) {
                            userMap.put(user.id, user);
                        }
                        for (Integer id : chat.getUsers()) {
                            VKUser user = userMap.get(id);
                            if (user != null && collageUsers.size() <= COLLAGE_MAX_USER) {
                                collageUsers.add(user);
                                collageUserIds.add(id);
                            }
                        }

                        try {
                            String collage = CollageFactory.getInstance(collageUsers).getCollage();
                            chat.setCustomPhotoUrl(collage);
                            chat.setCollageUsers(collageUserIds);
                            getApplicationContext().getContentResolver().update(
                                    DialogProvider.DIALOG_CONTENT_URI,
                                    VKChatWrapper.getContentValuesForUpdateAvatar(chat),
                                    DialogProvider.UPDATE_WHERE_CLAUSE,
                                    new String[] {String.valueOf(chat.getId())});

                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                        sendAvatarSyncIntent(chat);
                    }
                }
            } finally {
                lock.unlock();
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private boolean collageUserNotActual(List<Integer> activeUsers, List<Integer> collageUsers) {
        if (collageUsers.isEmpty() || collageUsers.size() != activeUsers.size()) {
            return true;
        }
        for(Integer userId : collageUsers) {
            if (!activeUsers.contains(userId)) {
                return true;
            }
        }
        return false;
    }

    private void sendAvatarSyncIntent(VKChat chat) {
        Intent dataIntent = new Intent(VKService.ACTION_DIALOG_AVATAR_SYNC);
        dataIntent.putExtra(MessageFragment.CHAT_ID, chat.getId());
        dataIntent.putExtra(MessageFragment.CUSTOM_PHOTO_URL, chat.getCustomPhotoUrl());
        VKTest.getContext().sendBroadcast(dataIntent);
    }
}
