package com.huahua.weread.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义behavior
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    // 构造器 为了能够通过XML创建这个View，我们需要添加一个包含Context和AttributeSet参数的构造函数。
    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    // 重写两个方法  这方法是父类 CoordinatorLayout.Behavior的
    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        // 根据coordinatorLayout的行为作出不同的动作，（xml文件没有设置锚点，应该是说根据锚点的行为来动作的吧）
        // 向上滑，隐藏FloatingActionButton
        // 向下滑，显示FloatingActionButton
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }

}
