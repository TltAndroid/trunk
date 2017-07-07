package com.sirc.tlt.adapters.toutiao;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sirc.tlt.R;
import com.sirc.tlt.model.toutiao.ChannelItem;
import com.sirc.tlt.model.toutiao.NewsItem;

import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 */

public class ChannelAdapter extends BaseMultiItemQuickAdapter<ChannelItem, BaseViewHolder> {
    private String selectedTitle;
    private Activity activity;
    private BaseViewHolder mEditViewHolder;
    private boolean isEdit;
    private long startTime;
    // touch 间隔时间  用于分辨是否是 "点击"
    private static final long SPACE_TIME = 100;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ChannelAdapter(List<ChannelItem> data) {
        super(data);
        addItemType(ChannelItem.ITEM_TYPE_MY, R.layout.item_channel_title);
        addItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL, R.layout.item_channel);
        addItemType(ChannelItem.ITEM_TYPE_OTHER, R.layout.item_channel_title);
        addItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL, R.layout.item_channel);
    }

    @Override
    public String toString() {
        return "My ChannelAdapter";
    }

    @Override
    protected void convert(final BaseViewHolder helper, final ChannelItem item) {
        switch (helper.getItemViewType()) {
            case ChannelItem.ITEM_TYPE_MY:
                //我的频道
                //赋值，以便之后修改文字
                mEditViewHolder = helper;
                helper.setText(R.id.tv_title, R.string.my_channel).setOnClickListener(R.id.tv_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEdit) {
                            startEditMode(true);
                            helper.setText(R.id.tv_edit, "完成");
                        } else {
                            startEditMode(false);
                            helper.setText(R.id.tv_edit, "编辑");
                        }
                    }
                });
                break;
            case ChannelItem.ITEM_TYPE_MY_CHANNEL:
                helper.setText(R.id.tv_title, item.getTitle());
                TextView tvTitle = helper.getView(R.id.tv_title);
                if (isToutiaoChannel(tvTitle)) {
                    tvTitle.setTextColor(mContext.getResources().getColor(R.color.channel_gray));
                }
                if (!isEdit && selectedTitle.equals(tvTitle.getText())) {
                    tvTitle.setTextColor(mContext.getResources().getColor(R.color.channel_red));
                }
                helper.setVisible(R.id.iv_delete, isEdit)//编辑模式就显示删除按钮
                .setOnLongClickListener(R.id.tv_title, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!isEdit) {
                            //开启编辑模式
                            startEditMode(true);
                            mEditViewHolder.setText(R.id.tv_edit, "完成");
                        }
                        if (isToutiaoChannel((TextView)v)) {
                            return false;
                        }
                        if (onChannelDragListener != null)
                            onChannelDragListener.onStarDrag(helper);
                        return true;
                    }
                })
                .setOnTouchListener(R.id.tv_title, new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (isToutiaoChannel((TextView)v)) {
                            return false;
                        }
                        if (!isEdit) return false;//正常模式无需监听触摸
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startTime = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (System.currentTimeMillis() - startTime > SPACE_TIME) {
                                    //当MOVE事件与DOWN事件的触发的间隔时间大于100ms时，则认为是拖拽starDrag
                                    if (onChannelDragListener != null)
                                        onChannelDragListener.onStarDrag(helper);
                                }
                                break;
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                startTime = 0;
                                break;
                        }
                        return false;
                    }
                })
                .getView(R.id.iv_delete).setTag(true);//在我的频道里面设置true标示，之后会根据这个标示来判断编辑模式是否显示
                helper.setOnClickListener(R.id.tv_title, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //执行删除，移动到推荐频道列表
                        if (isEdit) {
                            if (isToutiaoChannel((TextView)v)) {
                                return;
                            }
                            int otherFirstPosition = getOtherFirstPosition();
                            int currentPosition = getViewHolderPosition(helper);
                            //获取到目标View
                            View targetView = getRecyclerView().getLayoutManager().findViewByPosition(otherFirstPosition);
                            //获取当前需要移动的View
                            View currentView = getRecyclerView().getLayoutManager().findViewByPosition(currentPosition);
                            // 如果targetView不在屏幕内,则indexOfChild为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                            // 如果在屏幕内,则添加一个位移动画
                            if (getRecyclerView().indexOfChild(targetView) >= 0 && otherFirstPosition != -1) {
                                RecyclerView.LayoutManager manager = getRecyclerView().getLayoutManager();
                                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                                int targetX = targetView.getLeft();
                                int targetY = targetView.getTop();
                                int myChannelSize = getMyChannelSize();//这里我是为了偷懒 ，算出来我的频道的大小
                                if (myChannelSize % spanCount == 1) {
                                    //我的频道最后一行 之后一个，移动后
                                    targetY -= targetView.getHeight();
                                }

                                //我的频道 移动到 推荐频道的第一个
                                item.setItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL);//改为推荐频道类型
                                if (onChannelDragListener != null)
                                    onChannelDragListener.onItemMove(currentPosition, otherFirstPosition - 1);
                                startAnimation(currentView, targetX, targetY);
                            } else {
                                item.setItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL);//改为推荐频道类型
                                if (otherFirstPosition == -1) otherFirstPosition = mData.size();
                                if (onChannelDragListener != null)
                                    onChannelDragListener.onItemMove(currentPosition, otherFirstPosition - 1);
                            }
                        } else {
                            selectedTitle = String.valueOf(((TextView)v).getText());
                            activity.finish();
                        }
                    }
                });
                break;
            case ChannelItem.ITEM_TYPE_OTHER:
                helper.setText(R.id.tv_title, R.string.other_channel).setVisible(R.id.tv_edit, false);
                break;
            case ChannelItem.ITEM_TYPE_OTHER_CHANNEL:
                helper.setText(R.id.tv_title, item.getTitle()).setVisible(R.id.iv_delete, false)
                        .setOnClickListener(R.id.tv_title, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int myLastPosition = getMyLastPosition();
                                int currentPosition = getViewHolderPosition(helper);
                                //获取到目标View
                                View targetView = getRecyclerView().getLayoutManager().findViewByPosition(myLastPosition);
                                //获取当前需要移动的View
                                View currentView = getRecyclerView().getLayoutManager().findViewByPosition(currentPosition);
                                // 如果targetView不在屏幕内,则indexOfChild为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                                // 如果在屏幕内,则添加一个位移动画
                                if (getRecyclerView().indexOfChild(targetView) >= 0 && myLastPosition != -1) {
                                    RecyclerView.LayoutManager manager = getRecyclerView().getLayoutManager();
                                    int spanCount = ((GridLayoutManager) manager).getSpanCount();
                                    int targetX = targetView.getLeft() + targetView.getWidth();
                                    int targetY = targetView.getTop();

                                    int myChannelSize = getMyChannelSize();//这里我是为了偷懒 ，算出来我的频道的大小
                                    if (myChannelSize % spanCount == 0) {
                                        //添加到我的频道后会换行，所以找到倒数第4个的位置

                                        View lastFourthView = getRecyclerView().getLayoutManager().findViewByPosition(getMyLastPosition() - 3);
//                                        View lastFourthView = mRecyclerView.getChildAt(getMyLastPosition() - 3);
                                        targetX = lastFourthView.getLeft();
                                        targetY = lastFourthView.getTop() + lastFourthView.getHeight();
                                    }

                                    //我的频道 移动到 推荐频道的第一个
                                    item.setItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL);//改为推荐频道类型
                                    if (onChannelDragListener != null)
                                        onChannelDragListener.onItemMove(currentPosition, myLastPosition + 1);
                                    startAnimation(currentView, targetX, targetY);
                                } else {
                                    item.setItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL);//改为推荐频道类型
                                    if (myLastPosition == -1) myLastPosition = 0;//我的频道没有了，改成0
                                    if (onChannelDragListener != null)
                                        onChannelDragListener.onItemMove(currentPosition, myLastPosition + 1);
                                }
                            }
                        });
                break;
        }
    }

    /**
     * 开启编辑模式
     */
    private void startEditMode(boolean isEdit) {
        this.isEdit = isEdit;
        int visibleChildCount = getRecyclerView().getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = getRecyclerView().getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.iv_delete);
            if (imgEdit != null) {
                boolean isVis = imgEdit.getTag() == null ? false : (boolean) imgEdit.getTag();
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                if (!isToutiaoChannel(tvTitle)) {
                    imgEdit.setVisibility(isVis && isEdit ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 获取推荐频道列表的第一个position
     *
     * @return
     */
    private int getOtherFirstPosition() {
        //之前找到了第一个pos直接返回
//        if (mOtherFirstPosition != 0) return mOtherFirstPosition;
        for (int i = 0; i < mData.size(); i++) {
            ChannelItem channel = (ChannelItem) mData.get(i);
            if (ChannelItem.ITEM_TYPE_OTHER_CHANNEL == channel.getItemType()) {
                //找到第一个直接返回
                return i;
            }
        }
        return -1;
    }

    /**
     * 我的频道最后一个的position
     *
     * @return
     */
    private int getMyLastPosition() {
        for (int i = mData.size() - 1; i > -1; i--) {
            ChannelItem channel = (ChannelItem) mData.get(i);
            if (ChannelItem.ITEM_TYPE_MY_CHANNEL == channel.getItemType()) {
                //找到第一个直接返回
                return i;
            }
        }
        return -1;
    }

    private int getMyChannelSize() {
        int size = 0;
        for (int i = 0; i < mData.size(); i++) {
            ChannelItem channel = (ChannelItem) mData.get(i);
            if (channel.getItemType() == ChannelItem.ITEM_TYPE_MY_CHANNEL) {
                size++;
            }
        }
        return size;
    }

    public int getViewHolderPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() - this.getHeaderViewsCount();
    }

    private void startAnimation(final View currentView, int targetX, int targetY) {
        final ViewGroup parent = (ViewGroup) getRecyclerView().getParent();
        final ImageView mirrorView = addMirrorView(parent, currentView);
        TranslateAnimation animator = getTranslateAnimator(targetX - currentView.getLeft(), targetY - currentView.getTop());
        currentView.setVisibility(View.INVISIBLE);//暂时隐藏
        mirrorView.startAnimation(animator);
        animator.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                parent.removeView(mirrorView);//删除添加的镜像View
                if (currentView.getVisibility() == View.INVISIBLE) {
                    currentView.setVisibility(View.VISIBLE);//显示隐藏的View
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private int ANIM_TIME = 360;

    /**
     * 获取位移动画
     */
    private TranslateAnimation getTranslateAnimator(float targetX, float targetY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY);
        // RecyclerView默认移动动画250ms 这里设置360ms 是为了防止在位移动画结束后 remove(view)过早 导致闪烁
        translateAnimation.setDuration(ANIM_TIME);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    /**
     * 添加需要移动的 镜像View
     */
    private ImageView addMirrorView(ViewGroup parent, View view) {
        view.destroyDrawingCache();
        //首先开启Cache图片 ，然后调用view.getDrawingCache()就可以获取Cache图片
        view.setDrawingCacheEnabled(true);
        ImageView mirrorView = new ImageView(view.getContext());
        //获取该view的Cache图片
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        //销毁掉cache图片
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);//获取当前View的坐标
        int[] parenLocations = new int[2];
        getRecyclerView().getLocationOnScreen(parenLocations);//获取RecyclerView所在坐标
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);//在RecyclerView的Parent添加我们的镜像View，parent要是FrameLayout这样才可以放到那个坐标点
        return mirrorView;
    }

    //判断频道名是否是“头条”，如果是则置灰，不能点击、拖拽，没有编辑状态
    private boolean isToutiaoChannel(TextView tvTitle){
        return mContext.getResources().getText(R.string.title_toutiao).equals(tvTitle.getText());
    }

    public interface OnChannelDragListener {
        void onStarDrag(BaseViewHolder baseViewHolder);
        void onItemMove(int starPos, int endPos);
    }
    private OnChannelDragListener onChannelDragListener;

    public void setOnChannelDragListener(OnChannelDragListener onChannelDragListener) {
        this.onChannelDragListener = onChannelDragListener;
    }

    public String getSelectedTitle() {
        return selectedTitle;
    }

    public void setSelectedTitle(String selectedTitle) {
        this.selectedTitle = selectedTitle;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
