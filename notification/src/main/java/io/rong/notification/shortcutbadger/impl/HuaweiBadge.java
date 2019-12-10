package io.rong.notification.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.notification.shortcutbadger.IBadge;
import io.rong.notification.shortcutbadger.BadgeException;

public class HuaweiBadge implements IBadge {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws
            BadgeException {
            ChangeBadgeRunnable runnable = new ChangeBadgeRunnable(context,componentName,badgeCount);
            executorService.submit(runnable);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.huawei.android.launcher"
        );
    }

    private class ChangeBadgeRunnable implements Runnable {
        private Context context;
        private String packageName;
        private String launchClassName;
        private int badgeCount;

        ChangeBadgeRunnable(Context context, ComponentName componentName, int badgeCount) {
            this.context = context;
            this.packageName = context.getPackageName();
            this.launchClassName = componentName.getClassName();
            this.badgeCount = badgeCount;
        }

        @Override
        public void run() {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("package", packageName);
                bundle.putString("class", launchClassName);
                bundle.putInt("badgenumber", badgeCount);
                context.getContentResolver().call(
                        Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                        "change_badge", null, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
