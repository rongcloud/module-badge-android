package io.rong.notification.shortcutbadger;

import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public interface IBadge {
    /**
     * 更新角标数
     * @param context 上下文
     * @param componentName Component containing package and class name of calling application's
     *                      launcher activity
     * @param badgeCount 角标数量
     * @throws BadgeException
     */
    void executeBadge(Context context, ComponentName componentName, int badgeCount) throws BadgeException;

    /**
     * Called to let {@link Badge} knows which launchers are supported by this badger.
     * @return 支持的 launchers 列表
     */
    List<String> getSupportLaunchers();
}
