package io.rong.notification.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import io.rong.notification.shortcutbadger.IBadge;
import io.rong.notification.shortcutbadger.BadgeException;
import io.rong.notification.shortcutbadger.util.IntentConstants;

public class SamsungBadge implements IBadge {

    private static final String INTENT_ACTION = IntentConstants.DEFAULT_INTENT_ACTION;

    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws BadgeException {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, componentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, componentName.getClassName());
        context.sendBroadcast(intent);
    }


    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.sec.android.app.launcher",
                "com.sec.android.app.twlauncher"
        );
    }
}
