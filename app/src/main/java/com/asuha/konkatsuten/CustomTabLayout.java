package com.asuha.konkatsuten;

import android.support.design.widget.TabLayout;
import android.content.Context;
import android.util.AttributeSet;

import java.lang.reflect.Field;

/**
 * Created by lamyiucho on 13/9/2017.
 */

public class CustomTabLayout extends TabLayout {

//    private static final String TAG = CustomTabLayout.class.getSimpleName();
    private static final int WIDTH_INDEX = 0;
    private static final int DIVIDER_FACTOR = 3;
    private static final String SCROLLABLE_TAB_MIN_WIDTH = "mScrollableTabMinWidth";

    public CustomTabLayout(Context context) {
        super(context);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTabMinWidth();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (getTabCount() == 0) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            return;
//        }
//
//        try {
//            ViewGroup tabLayout = (ViewGroup)getChildAt(0);
//            int widthOfAllTabs = 0;
//            for (int i = 0; i < tabLayout.getChildCount(); i++) {
//                widthOfAllTabs += tabLayout.getChildAt(i).getMeasuredWidth();
//            }
//
//            int measuredWidth = this.getScreenWidth(getContext());
//
//            setTabMode(widthOfAllTabs < measuredWidth ? MODE_FIXED : MODE_SCROLLABLE);
//            setTabGravity(widthOfAllTabs <= measuredWidth ? TabLayout.GRAVITY_FILL : TabLayout.GRAVITY_FILL);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            try {
//                if (getTabCount() == 0)
//                    return;
//                Field field = TabLayout.class.getDeclaredField("mTabMinWidth");
//                field.setAccessible(true);
//                field.set(this, (int) (getMeasuredWidth() / (float) getTabCount()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//}
//
//    public static int getScreenWidth(Context context) {
//        DisplayMetrics metrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        return metrics.widthPixels;
//    }
    private void initTabMinWidth() {
        int[] wh = Utils.getScreenSize(getContext());
        int tabMinWidth = wh[WIDTH_INDEX] / DIVIDER_FACTOR;

        Field field;
        try {
            field = TabLayout.class.getDeclaredField(SCROLLABLE_TAB_MIN_WIDTH);
            field.setAccessible(true);
            field.set(this, tabMinWidth);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
