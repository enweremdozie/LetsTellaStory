package com.letstellastory.android.letstellastory.Holder;

import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dozie on 2017-07-18.
 */

public class QBChatDialogHolder {
    private static QBChatDialogHolder instance;
    private HashMap<String, QBChatDialog> qbChatDialogHashMap;

    public static synchronized QBChatDialogHolder getInstance(){
        QBChatDialogHolder qbChatDialogHolder;
        synchronized (QBChatDialogHolder.class){
            if(instance == null){
                instance = new QBChatDialogHolder();
            }
        }
        qbChatDialogHolder = instance;
        return qbChatDialogHolder;
    }

    public QBChatDialogHolder() {
        this.qbChatDialogHashMap = new HashMap<>();
    }

    public void putDialogs(List<QBChatDialog> dialogs){
        for(QBChatDialog qbChatDialog:dialogs){
            putDialog(qbChatDialog);
        }
    }

    public void putDialog(QBChatDialog qbChatDialog) {
        this.qbChatDialogHashMap.put(qbChatDialog.getDialogId(), qbChatDialog);
    }

    public QBChatDialog getChatDialogById(String dialogId){
        return (QBChatDialog)qbChatDialogHashMap.get(dialogId);
    }

    public List<QBChatDialog> getChatDialogsByids(List<String> dialogIds){
        List<QBChatDialog> chatDialogs = new ArrayList<>();
        for(String id:dialogIds){
            QBChatDialog chatDialog = getChatDialogById(id);
            if(chatDialog != null){
                chatDialogs.add(chatDialog);
            }
        }
        return chatDialogs;
    }

    public  ArrayList<QBChatDialog> getAllChatDialogs(){
        ArrayList<QBChatDialog> qbChat = new ArrayList<>();
        for(String key:qbChatDialogHashMap.keySet())
            qbChat.add(qbChatDialogHashMap.get(key));
        return qbChat;
    }

    public  ArrayList<QBChatDialog> getAllGroupChatDialogs(){
        ArrayList<QBChatDialog> qbChat = new ArrayList<>();
        for(String key:qbChatDialogHashMap.keySet())
            if(qbChatDialogHashMap.get(key).getType().toString().equals("GROUP")) {
                qbChat.add(qbChatDialogHashMap.get(key));
            }
        return qbChat;
    }

    public  ArrayList<QBChatDialog> getAllLocalGroupChatDialogs(){
        ArrayList<QBChatDialog> qbChat = new ArrayList<>();
        for(String key:qbChatDialogHashMap.keySet())
            if(qbChatDialogHashMap.get(key).getType().toString().equals("PUBLIC_GROUP")) {
                qbChat.add(qbChatDialogHashMap.get(key));
            }
        return qbChat;
    }

    public void removeDialog(String id){
        qbChatDialogHashMap.remove(id);
    }
}
