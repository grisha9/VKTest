package ru.rzn.myasoedov.vktest.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import java.util.HashSet;
import java.util.LinkedList;

import ru.rzn.myasoedov.vktest.db.DialogProvider;
import ru.rzn.myasoedov.vktest.db.MessageProvider;
import ru.rzn.myasoedov.vktest.db.ParticipantProvider;
import ru.rzn.myasoedov.vktest.dto.VKChat;
import ru.rzn.myasoedov.vktest.dto.VKChatWrapper;
import ru.rzn.myasoedov.vktest.dto.VKMessageWrapper;
import ru.rzn.myasoedov.vktest.dto.VKUser;
import ru.rzn.myasoedov.vktest.dto.VKUserWrapper;


/**
 * Created by grisha on 11.05.15.
 */
public class SyncService extends IntentService {
    public static final String ACTION_SYNC_DIALOGS = "ru.rzn.myasoedov.vktest.service.SYNC_DIALOGS";
    public static final String ACTION_SYNC_PARTICIPANT = "ru.rzn.myasoedov.vktest.service.SYNC_PARTICIPANT";
    public static final String ACTION_SYNC_MESSAGE = "ru.rzn.myasoedov.vktest.service.SYNC_MESSAGE";
    public static final String MODEL_OBJECT = "model-object";
    public static final String DELETE_OLD = "delete-old";
    public static final String CHAT_ID = "chat-id";

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
        HashSet<Integer> ids = new HashSet<>();
        LinkedList<ContentValues> values = new LinkedList<>();
        for(VKApiMessage message : messages) {
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

}
