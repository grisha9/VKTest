package ru.rzn.myasoedov.vktest.service;

import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

import ru.rzn.myasoedov.vktest.VKTest;
import ru.rzn.myasoedov.vktest.dto.VKChat;
import ru.rzn.myasoedov.vktest.dto.VKChatArray;
import ru.rzn.myasoedov.vktest.dto.VKMessageArray;
import ru.rzn.myasoedov.vktest.dto.VKUser;

/**
 * Created by grisha on 12.05.15.
 */
public class VKService {
    public static final String ACTION_DIALOG_SYNC_FINISH = "ru.rzn.myasoedov.vktest.ACTION_DIALOG_SYNC_FINISH";
    public static final String ACTION_MESSAGE_SYNC_FINISH = "ru.rzn.myasoedov.vktest.ACTION_MESSAGE_SYNC_FINISH";
    public static final String ACTION_MESSAGE_SYNC_ERROR = "ru.rzn.myasoedov.vktest.ACTION_MESSAGE_SYNC_ERROR";
    private static final String SYNC_TAG = VKService.class.getName();
    public static final int MESSAGE_COUNT_FIRST_UPDATE = 100;

    /**
     * show only group dialogs from 20 latest conversations
     */
    public static void getDialogs() {
        VKRequest vkRequest = new VKRequest("messages.getDialogs");
        vkRequest.setModelClass(VKChatArray.class);
        vkRequest.addExtraParameter("preview_length", 100);
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (response.parsedModel instanceof VKChatArray
                        && !((VKChatArray) response.parsedModel).isEmpty()) {
                    Intent intent = new Intent(SyncService.ACTION_SYNC_DIALOGS);
                    intent.putExtra(SyncService.MODEL_OBJECT, (Parcelable) response.parsedModel);
                    VKTest.getContext().startService(intent);

                    getParticipantsFromDialogs((VKChatArray) response.parsedModel);
                }

                sendReguestFinishIntent(ACTION_DIALOG_SYNC_FINISH);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendReguestFinishIntent(ACTION_DIALOG_SYNC_FINISH);
                Log.e(SYNC_TAG, error.errorMessage);
            }
        });
    }

    private static void sendReguestFinishIntent(String action) {
        Intent dataIntent = new Intent(action);
        VKTest.getContext().sendBroadcast(dataIntent);
    }

    public static void getParticipantsFromDialogs(VKChatArray chats) {
        VKRequest vkRequest = new VKRequest("messages.getChatUsers");
        final Set<Integer> chatIds = new HashSet<>();
        for (VKChat chat : chats) {
            chatIds.add(chat.getId());
        }
        vkRequest.addExtraParameters(VKParameters.from(VKApiConst.FIELDS, "id, photo_50"));
        vkRequest.addExtraParameter("chat_ids", TextUtils.join(",", chatIds));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKList<VKUser> vkApiUsers = new VKList<VKUser>();
                Intent syncDialogAvatarIntent = new Intent(SyncService.ACTION_SYNC_DIALOG_AVATAR);
                for (Integer id : chatIds) {
                    try {
                        VKList<VKUser> vkChatUsers = new VKList<>();
                        vkChatUsers.fill(response.json.getJSONObject("response")
                                .getJSONArray(id.toString()), VKUser.class);
                        vkApiUsers.addAll(vkChatUsers);

                        syncDialogAvatarIntent.putExtra(id.toString(), vkApiUsers);
                    } catch (JSONException e) {
                        Log.e(SYNC_TAG, e.getMessage());
                    }
                }
                if (!vkApiUsers.isEmpty()) {
                    Intent intent = new Intent(SyncService.ACTION_SYNC_PARTICIPANT);
                    intent.putExtra(SyncService.MODEL_OBJECT, vkApiUsers);
                    VKTest.getContext().startService(intent);

                    VKTest.getContext().startService(syncDialogAvatarIntent);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.e(SYNC_TAG, error.errorMessage);
            }
        });
    }

    public static void getMessageByChatId(int chatId, boolean isDeleteOld) {
        getMessageByChatId(chatId, isDeleteOld, MESSAGE_COUNT_FIRST_UPDATE, null);
    }

    public static void getMessageByChatId(final int chatId, final boolean isDeleteOld, int count,
                                          Integer startMessageId) {
        VKRequest vkRequest = new VKRequest("messages.getHistory");
        vkRequest.setModelClass(VKMessageArray.class);
        vkRequest.addExtraParameter("chat_id", chatId);
        vkRequest.addExtraParameter("count", count);
        if (startMessageId != null) {
            vkRequest.addExtraParameter("start_message_id", startMessageId);
        }
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (response.parsedModel instanceof VKMessageArray
                        && !((VKMessageArray) response.parsedModel).isEmpty()) {
                    Intent intent = new Intent(SyncService.ACTION_SYNC_MESSAGE);
                    intent.putExtra(SyncService.CHAT_ID, chatId);
                    intent.putExtra(SyncService.DELETE_OLD, isDeleteOld);
                    intent.putExtra(SyncService.MODEL_OBJECT, (Parcelable) response.parsedModel);
                    VKTest.getContext().startService(intent);

                    sendReguestFinishIntent(ACTION_MESSAGE_SYNC_FINISH);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendReguestFinishIntent(ACTION_MESSAGE_SYNC_ERROR);
            }
        });
    }
}
