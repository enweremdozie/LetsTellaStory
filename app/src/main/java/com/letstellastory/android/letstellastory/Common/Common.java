package com.letstellastory.android.letstellastory.Common;

import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by dozie on 2017-07-13.
 */

public class Common {

    public static final String DIALOG_EXTRA = "Dialogs";
    public static String createChatDialogName(List<Integer> qbUsers){

        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUserByIds(qbUsers);
        StringBuilder name = new StringBuilder();
        for(QBUser user:qbUsers1){
            name.append(user.getFullName()).append(" ");
        }
        if(name.length() > 30){
            name = name.replace(30, name.length() - 1, "...");
        }
        return name.toString();
    }

}
