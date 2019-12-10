package io.rong.notification.shortcutbadger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;

import io.rong.notification.shortcutbadger.impl.DefaultBadge;
import io.rong.notification.shortcutbadger.impl.HuaweiBadge;
import io.rong.notification.shortcutbadger.impl.MIBadge;
import io.rong.notification.shortcutbadger.impl.OPPOBade;
import io.rong.notification.shortcutbadger.impl.SamsungBadge;
import io.rong.notification.shortcutbadger.impl.VivoBadge;

public class Badge {
    private static final String LOG_TAG = "Badge";

    private static final List<Class<? extends IBadge>> BADGES = new LinkedList<>();
    private static final String TOTAL_UNREAD_COUNT = "total_unread_count";
    private static final String UNREAD_COUNT = "unread_count";
    private static final String MANUFACTURER_OF_HARDWARE_SAMSUNG = MobileBrand.SAMSUNG;
    private static final String MANUFACTURER_OF_HARDWARE_OPPO = MobileBrand.OPPO;
    private static final String MANUFACTURER_OF_HARDWARE_VIVO = MobileBrand.VIVO;
    private static final String MANUFACTURER_OF_HARDWARE_HUAWEI = MobileBrand.HUAWEI;
    private static final String MANUFACTURER_OF_HARDWARE_MI = MobileBrand.XIAOMI;


    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static Badge mBadge;

    static {
        BADGES.add(DefaultBadge.class);
        BADGES.add(HuaweiBadge.class);
        BADGES.add(OPPOBade.class);
        BADGES.add(SamsungBadge.class);
        BADGES.add(VivoBadge.class);
        BADGES.add(MIBadge.class);
    }

    private static IBadge iBadge;
    private static ComponentName sComponentName;


    public static Badge getInstance() {
        if (mBadge == null) {
            synchronized (Badge.class) {
                if (mBadge == null) {
                    mBadge = new Badge();
                }
            }
        }
        return mBadge;
    }

    /**
     * 更新角标的数量
     *
     * @param context    上下文
     * @param badgeCount 角标数量
     * @return true 成功, false 失败
     */
    public boolean applyCount(Context context, int badgeCount) {
        try {
            applyCountOrThrow(context, badgeCount);
            return true;
        } catch (BadgeException e) {
            RLog.d(LOG_TAG, "Unable to execute badge");
            return false;
        }
    }

    /**
     * 更新角标的数量,如果失败，抛出 {@link BadgeException}
     *
     * @param context    上下文
     * @param badgeCount 角标数量
     */
    public void applyCountOrThrow(Context context, int badgeCount) throws BadgeException {
        if (iBadge == null) {
            boolean launcherReady = initBadger(context);

            if (!launcherReady)
                throw new BadgeException("No default launcher available");
        }

        try {
            iBadge.executeBadge(context, sComponentName, badgeCount);
        } catch (Exception e) {
            throw new BadgeException("Unable to execute badge", e);
        }

        if (editor != null) {
            editor.putInt(UNREAD_COUNT, badgeCount);
            editor.commit();
        }
    }


    public void applyPushCount(Context context) {
        if (editor != null) {
            int badgeCount = sp.getInt(UNREAD_COUNT, 0) + 1;
            editor.putInt(UNREAD_COUNT, badgeCount);
            editor.commit();
            applyCount(context, badgeCount);
        }
    }

    /**
     * 角标数量清0
     *
     * @param context 上下文
     * @return true 成功, false 失败
     */
    public boolean removeCount(Context context) {
        return applyCount(context, 0);
    }

    /**
     * 角标数量清0, 如果失败，抛出 {@link BadgeException}
     *
     * @param context 上下文
     */
    public void removeCountOrThrow(Context context) throws BadgeException {
        applyCountOrThrow(context, 0);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public boolean initBadger(Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            RLog.e(LOG_TAG, "Unable to find launch intent for package " + context.getPackageName());
            return false;
        }

        sp = context.getSharedPreferences(TOTAL_UNREAD_COUNT, Context.MODE_PRIVATE);
        editor = sp.edit();

        sComponentName = launchIntent.getComponent();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resolveInfos) {
            String currentHomePackage = resolveInfo.activityInfo.packageName;

            for (Class<? extends IBadge> badger : BADGES) {
                IBadge shortcutBadger = null;
                try {
                    shortcutBadger = badger.newInstance();
                } catch (Exception ignored) {
                }
                if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains(currentHomePackage)) {
                    iBadge = shortcutBadger;
                    break;
                }
            }
            if (iBadge != null) {
                break;
            }
        }

        if (iBadge == null) {
            if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_SAMSUNG)) {
                iBadge = new SamsungBadge();
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_OPPO)) {
                iBadge = new OPPOBade();
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_VIVO)) {
                iBadge = new VivoBadge();
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_HUAWEI)) {
                iBadge = new HuaweiBadge();
            } else if (Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_OF_HARDWARE_MI)) {
                iBadge = new MIBadge();
            } else {
                iBadge = new DefaultBadge();
            }
        }
        RLog.i(LOG_TAG, "sShortcutBadger = " + iBadge);

        return true;
    }

    public Badge() {

    }
}
