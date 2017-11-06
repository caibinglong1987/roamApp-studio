package com.roamtech.telephony.roamapp.helper;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenblue23 on 2016/9/1.
 */
public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishToFirstView() {
        for (int i = 1; i < activities.size(); i++)	{
            Activity activity = activities.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static Activity getLastActivity() {
        if (activities.size() > 0) {
            return activities.get(activities.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * 打电话 界面
     * @return
     */
    public static Activity getCallActivity() {
        if (activities.size() > 1) {
            return activities.get(activities.size() - 2);
        } else {
            return null;
        }
    }
}
