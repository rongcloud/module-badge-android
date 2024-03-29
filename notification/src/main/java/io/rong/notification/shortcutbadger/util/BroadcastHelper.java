package io.rong.notification.shortcutbadger.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.Collections;
import java.util.List;

import io.rong.notification.shortcutbadger.BadgeException;

public class BroadcastHelper {
    private static List<ResolveInfo> resolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);

        return receivers != null ? receivers : Collections.<ResolveInfo>emptyList();
    }

    public static void sendIntentExplicitly(Context context, Intent intent) throws BadgeException {
        List<ResolveInfo> resolveInfos = resolveBroadcast(context, intent);

        if (resolveInfos.size() == 0) {
            throw new BadgeException("unable to resolve intent: " + intent.toString());
        }

        for (ResolveInfo info : resolveInfos) {
            Intent actualIntent = new Intent(intent);

            if (info != null) {
                actualIntent.setPackage(info.resolvePackageName);
                context.sendBroadcast(actualIntent);
            }
        }
    }

    public static void sendDefaultIntentExplicitly(Context context, Intent intent) throws BadgeException {
        boolean oreoIntentSuccess = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent oreoIntent = new Intent(intent);

            oreoIntent.setAction(IntentConstants.DEFAULT_OREO_INTENT_ACTION);

            try {
                sendIntentExplicitly(context, oreoIntent);
                oreoIntentSuccess = true;
            } catch (BadgeException e) {
                oreoIntentSuccess = false;
            }
        }

        if (oreoIntentSuccess) {
            return;
        }

        // try pre-Oreo default intent
        sendIntentExplicitly(context, intent);
    }
}
