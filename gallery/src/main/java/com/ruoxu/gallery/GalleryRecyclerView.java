package com.ruoxu.gallery;


import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class GalleryRecyclerView extends RecyclerView {
    
    private final static int MINIMUM_SCROLL_EVENT_OFFSET_MS = 20;
    
    private boolean userScrolling = false;
    private boolean mScrolling = false;

    private boolean scrolling;
    private int scrollState = SCROLL_STATE_IDLE;

    //最后滑动时间
    private long lastScrollTime = 0;

    //方向  默认水平
    private Orientation orientation = Orientation.HORIZONTAL;

    private ChildViewMetrics childViewMetrics;


    // 选中的位置position
    private int selectedPosition;

    private LinearLayoutManager mLinearLayoutManager;

    private TouchDownListem touchDownListener;
    //选中回调
    private OnViewSelectedListener listener;


    public enum Orientation {
        HORIZONTAL(LinearLayout.HORIZONTAL),
        VERTICAL(LinearLayout.VERTICAL);

        int value;

        Orientation(int value) {
            this.value = value;
        }

        public int intValue() {
            return value;
        }
    }

    /**
     * 中间view选中接口
     */
    public interface OnViewSelectedListener {
        void onSelected(View view, int position);
    }

    public interface TouchDownListem{
        void onTouchDown();
    }

    public void setTouchDownlistem(TouchDownListem touchDownListener){
        this.touchDownListener = touchDownListener;
    }


    public GalleryRecyclerView(Context context) {
        this(context, null);
    }

    public GalleryRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHasFixedSize(true);

        this.orientation = Orientation.HORIZONTAL;

        childViewMetrics = new ChildViewMetrics(orientation);
        mLinearLayoutManager=new LinearLayoutManager(getContext(), orientation.intValue(), false);
        setLayoutManager(mLinearLayoutManager);

        enableSnapping();// 捕捉
    }


    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);

        if (!scrolling && scrollState == SCROLL_STATE_IDLE) {
            scrolling = true;
            scrollToView(getCenterView());
            updateViews();
        }
    }

    private void enableSnapping() {

        //此方法也是判断RecycleView加载更多的重要方法和 listView.setOnScrollListener();类似
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateViews();//滚动的时候改变 view的alpha和scale等等
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                /** if scroll is caused by a touch (scroll touch, not any touch) **/
                if (newState == SCROLL_STATE_TOUCH_SCROLL) {
                    /** if scroll was initiated already, it would probably be a tap **/
                    /** if scroll was not initiated before, this is probably a user scrolling **/
                    if (!mScrolling) {
                        userScrolling = true;
                    }
                } else if (newState == SCROLL_STATE_IDLE) {
                    /** if user is the one scrolling, snap to the view closest to center **/
                    if (userScrolling) {
                        scrollToView(getCenterView());
                    }

                    userScrolling = false;
                    mScrolling = false;

                    /** if idle, always check location and correct it if necessary, this is just an extra check **/
                    if (getCenterView() != null && getPercentageFromCenter(getCenterView()) > 0) {
                        scrollToView(getCenterView());
                    }

                    /** if idle, notify listeners of new selected view **/
                    notifyListener();
                } else if (newState == SCROLL_STATE_FLING) {
                    mScrolling = true;
                }

                scrollState = newState;
            }
        });
    }

    /**
     * 通知回调并设置当前选中位置
     */
    private void notifyListener() {
        View view = getCenterView();
        int position = getChildAdapterPosition(view);
        /** if there is a listener and the index is not the same as the currently selected position, notify listener **/
        if (listener != null && position != selectedPosition) {
            listener.onSelected(view, position);
        }
        selectedPosition = position;
    }



    /**
     * 设置选择position TV 合适此方法
     * @param position
     */
    public void setSelectPosition(int position){
        mLinearLayoutManager.scrollToPositionWithOffset(position,0);
    }

    /**
     * 设置选中回调接口
     * @param listener the OnViewSelectedListener
     */
    public void setOnViewSelectedListener(OnViewSelectedListener listener) {
        this.listener = listener;
    }


    /**
     * 更新views
     */
    private void updateViews() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            setMarginsForChild(child);
            float percentage = getPercentageFromCenter(child);//百分比
            float scale = 1f - (0.5f * percentage);
            float alpha = 1f - (0.95f * percentage);

            //设置缩放
            child.setScaleX(scale);
            child.setScaleY(scale);

            //设置透明度
            child.setAlpha(alpha);

            child.requestLayout();
        }
    }

    /**
     *  Adds the margins to a childView so a view will still center even if it's only a single child
     * @param child childView to set margins for
     */
    private void setMarginsForChild(View child) {
        int lastItemIndex = getLayoutManager().getItemCount() - 1;
        int childIndex = getChildAdapterPosition(child);

        int startMargin = 0;
        int endMargin = 0;
        int topMargin = 0;
        int bottomMargin = 0;

        switch (orientation) {
            case VERTICAL:
                topMargin = childIndex == 0 ? getCenterLocation() : 0;
                bottomMargin = childIndex == lastItemIndex ? getCenterLocation() : 0;
                break;
            case HORIZONTAL:
                startMargin = childIndex == 0 ? getCenterLocation() : 0;
                endMargin = childIndex == lastItemIndex ? getCenterLocation() : 0;
                break;
            }

        ((MarginLayoutParams) child.getLayoutParams()).setMargins(startMargin, topMargin, endMargin, bottomMargin);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        long currentTime = System.currentTimeMillis();

        /** if touch events are being spammed, this is due to user scrolling right after a tap,
         * so set userScrolling to true **/
        if (mScrolling && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if ((currentTime - lastScrollTime) < MINIMUM_SCROLL_EVENT_OFFSET_MS) {
                userScrolling = true;
            }
        }

        lastScrollTime = currentTime;

        int location = orientation == Orientation.VERTICAL ? (int)event.getY() : (int)event.getX();

        View targetView = getChildClosestToLocation(location);

        if (!userScrolling) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (targetView != getCenterView()) {
                    scrollToView(targetView);
                    return true;
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            if(touchDownListener!=null)
                touchDownListener.onTouchDown();
        }

        int location = orientation == Orientation.VERTICAL ? (int)event.getY() : (int)event.getX();
        View targetView = getChildClosestToLocation(location);
        if (targetView != getCenterView()) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction()== MotionEvent.ACTION_DOWN){
            if(touchDownListener!=null)
                touchDownListener.onTouchDown();
        }
        return super.onTouchEvent(e);
    }



    @Override
    public void scrollToPosition(int position) {
        childViewMetrics.size(getChildAt(0));
        smoothScrollBy(childViewMetrics.size(getChildAt(0)) * position);
    }

    /**
     * 获取中间的view
     * @return
     */
    public View getCenterView() {
        return getChildClosestToLocation(getCenterLocation());
    }


    private View getChildClosestToLocation(int location) {
        if (getChildCount() <= 0)
            return null;

        int closestPos = 9999;
        View closestChild = null;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childCenterLocation = (int) childViewMetrics.center(child);
            int distance = childCenterLocation - location;

            /** if child center is closer than previous closest, set it as closest child  **/
            if (Math.abs(distance) < Math.abs(closestPos)) {
                closestPos = distance;
                closestChild = child;
            }
        }

        return closestChild;
    }



    /**
     * 滚动指定view
     * @param child
     */
    private void scrollToView(View child) {
        if (child == null)
            return;

        stopScroll();

        int scrollDistance = getScrollDistance(child);

        if (scrollDistance != 0)
            smoothScrollBy(scrollDistance);
    }

    private int getScrollDistance(View child) {
        int childCenterLocation = (int) childViewMetrics.center(child);
        return childCenterLocation - getCenterLocation();
    }

    private float getPercentageFromCenter(View child) {
        float center = getCenterLocation();
        float childCenter = childViewMetrics.center(child);

        float offSet = Math.max(center, childCenter) - Math.min(center, childCenter);
        float maxOffset = (center + childViewMetrics.size(child));

        return (offSet / maxOffset);
    }

    private int getCenterLocation() {
        int location = 0; // 中心点坐标
        switch (orientation) {
            case HORIZONTAL:
                location =  getMeasuredWidth() / 2;
                break;
            case VERTICAL:
                location = getMeasuredHeight() / 2;
                break;
        }
        return location;

    }


    public void smoothScrollBy(int distance) {
        if (orientation == Orientation.VERTICAL) {
            super.smoothScrollBy(0, distance);    //or scrollBy
            return;
        }

        super.smoothScrollBy(distance, 0);
    }


    private static class ChildViewMetrics {
        private Orientation orientation;

        public ChildViewMetrics(Orientation orientation) {
            this.orientation = orientation;
        }

        public int size(View view) {
            if (orientation == Orientation.VERTICAL)
                return view.getHeight();

            return view.getWidth();
        }

        public float location(View view) {
            if (orientation == Orientation.VERTICAL)
                return view.getY();

            return view.getX();
        }

        public float center(View view) {
            return location(view) + (size(view) / 2);
        }
    }


}
