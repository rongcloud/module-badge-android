package io.rong.notification.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.notification.shortcutbadger.IBadge;
import io.rong.notification.shortcutbadger.BadgeException;
import io.rong.notification.shortcutbadger.util.BroadcastHelper;

public class OPPOBade implements IBadge {
    private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
    private static final String INTENT_ACTION = "com.oppo.unsettledevent";
    private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
    private static final String INTENT_EXTRA_BADGE_COUNT = "number";
    private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";
    private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
    private int mCurrentTotalCount = -1;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws BadgeException {
        if (mCurrentTotalCount == badgeCount) {
            return;
        }
        mCurrentTotalCount = badgeCount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            ChangeBadgeRunnable runnable = new ChangeBadgeRunnable(context,badgeCount);
            executorService.submit(runnable);
        } else {
            executeBadgeByBroadcast(context, componentName, badgeCount);
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Collections.singletonList("com.oppo.launcher");
    }

    private void executeBadgeByBroadcast(Context context, ComponentName componentName,
                                         int badgeCount) throws BadgeException {
        if (badgeCount == 0) {
            badgeCount = -1;
        }
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, componentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        intent.putExtra(INTENT_EXTRA_BADGE_UPGRADENUMBER, badgeCount);

        BroadcastHelper.sendIntentExplicitly(context, intent);
    }

    private class ChangeBadgeRunnable implements Runnable {
        private Context context;
        private int badgeCount;

        ChangeBadgeRunnable(Context context,int badgeCount) {
            this.context = context;
            this.badgeCount = badgeCount;
        }

        @Override
        public void run() {
            try {
                Bundle extras = new Bundle();
                extras.putInt(INTENT_EXTRA_BADGEUPGRADE_COUNT, badgeCount);
                context.getContentResolver().call(Uri.parse(PROVIDER_CONTENT_URI), "setAppBadgeCount", null, extras);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
