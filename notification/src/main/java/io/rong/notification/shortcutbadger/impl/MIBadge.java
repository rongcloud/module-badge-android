package io.rong.notification.shortcutbadger.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import io.rong.notification.R;
import io.rong.notification.shortcutbadger.IBadge;
import io.rong.notification.shortcutbadger.BadgeException;

public class MIBadge implements IBadge {

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws BadgeException {
        {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);

            final NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            String contentTitle = getAppName(context);
            String contentText = getAppName(context) + "  " + context.getString(R.string.application_running);
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setSmallIcon(context.getApplicationInfo().icon);
            Notification notification = builder.build();

            if (badgeCount == 0) {
                return;
            }

            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, badgeCount - 1);
                if (mNotificationManager != null) {
                    mNotificationManager.notify(0, notification);
                }
            } catch (Exception e) {
                throw new BadgeException("not able to set badge", e);
            }
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.miui.miuilite",
                "com.miui.home",
                "com.miui.miuihome",
                "com.miui.miuihome2",
                "com.miui.mihome",
                "com.miui.mihome2",
                "com.i.miui.launcher"
        );
    }

    /**
     * 获取应用程序名称
     */
    private static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
