##  角标显示

### 接口说明：
1: 

```java
Badge.getInstance().initBadger(this);
```

在 Application onCreate()中调用 initBadger 方法进行初始化。

2:
 
```java
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
```
通过该接口可以更新角标的数量。

3:

```java
/**
 * 角标数量清 0
 *
 * @param context 上下文
 * @return true 成功, false 失败
 */
 public boolean removeCount(Context context) {
    return applyCount(context, 0);
 }
```


通过该接口可以清除角标的数量。

### 集成步骤：
Android Studio 导入 notification module 

配置：

在 settings.gradle 中添加 'notification' 模块，如：

``` java
include ': notification'
```

在应用的 build.gradle 中添加依赖, 如：

``` java
api project(':notification')
```
