package com.letstellastory.android.letstellastory.Holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dozie on 2017-07-16.
 */

public class QBStoryMessageHolder {
    private static QBStoryMessageHolder instance;
    private HashMap<String, ArrayList<QBChatMessage>> qbChatMessageArray;

    public static  synchronized QBStoryMessageHolder getInstance(){
        QBStoryMessageHolder qbStoryMessageHolder;
        synchronized (QBStoryMessageHolder.class){
            if(instance == null){
                instance = new QBStoryMessageHolder();
            }
            qbStoryMessageHolder = instance;
        }
        return  qbStoryMessageHolder;
    }

    private QBStoryMessageHolder(){
        this.qbChatMessageArray = new HashMap<>();
    }

    public void putStories(String dialogId, ArrayList<QBChatMessage> qbStoryMessages){
        this.qbChatMessageArray.put(dialogId, qbStoryMessages);
    }

    public void putStory(String dialogId, QBChatMessage qbStoryMessage){
        List<QBChatMessage> lstResult = (List)this.qbChatMessageArray.get(dialogId);
        lstResult.add(qbStoryMessage);
        ArrayList<QBChatMessage> lstAdded = new ArrayList(lstResult.size());
        lstAdded.addAll(lstResult);
        putStories(dialogId, lstAdded);
    }

    public ArrayList<QBChatMessage> getStoryMessageByDialogId(String dialogId){

        return (ArrayList<QBChatMessage>)this.qbChatMessageArray.get(dialogId);

    }
}
