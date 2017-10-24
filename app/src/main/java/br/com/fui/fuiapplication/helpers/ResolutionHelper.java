package br.com.fui.fuiapplication.helpers;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by guilherme on 19/10/17.
 */

public class ResolutionHelper {

    private static int deviceWidth = 720;
    private static int deviceHeight = 1280;
    //ASUS Zenfone 2 width and height (which is the one being use for tests)
    private static final int relativeWidth = 720;
    private static final int relativeHeight = 1280;
    public static final int WIDTH = 0;
    public static final int HEIGHT = 1;

    public static void start(Context c) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        ResolutionHelper.deviceWidth = metrics.widthPixels;
        ResolutionHelper.deviceHeight = metrics.heightPixels;
    }

    public static int getAdjustedPixels(int value, int reference) {
        int device, relative;
        if (reference == WIDTH) {
            device = deviceWidth;
            relative = ResolutionHelper.relativeWidth;
        } else {
            device = deviceHeight;
            relative = ResolutionHelper.relativeHeight;
        }

        float percentage = ((float) value / relative) * 100;
        return (int) ((percentage * device) / 100);
    }
}
