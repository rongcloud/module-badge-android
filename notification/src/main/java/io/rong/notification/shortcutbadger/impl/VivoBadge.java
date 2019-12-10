package io.rong.notification.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Collections;
import java.util.List;

import io.rong.notification.shortcutbadger.IBadge;
import io.rong.notification.shortcutbadger.BadgeException;

public class VivoBadge implements IBadge {
    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws BadgeException {
        Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("className", componentName.getClassName());
        intent.putExtra("notificationNum", badgeCount);
        context.sendBroadcast(intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Collections.singletonList("com.vivo.launcher");
    }
}
