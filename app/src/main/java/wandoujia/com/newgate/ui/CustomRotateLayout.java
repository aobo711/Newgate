package wandoujia.com.newgate.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

import wandoujia.com.newgate.R;

/**
 * Created by jintian on 15/1/16.
 */
public final class CustomRotateLayout extends LoadingLayout {

    static final int ROTATION_ANIMATION_DURATION = 1200;
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    private final Animation mRotateAnimation;
    private final Matrix mHeaderImageMatrix;

    private float mRotationPivotX, mRotationPivotY;

    private final boolean mRotateDrawableWhilePulling;

    public CustomRotateLayout(Context context, Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

        mRotateDrawableWhilePulling = attrs.getBoolean(com.handmark.pulltorefresh.library.R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

        mHeaderImage.setScaleType(ImageView.ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        mHeaderImage.setImageMatrix(mHeaderImageMatrix);

        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
            mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
        }
    }

    @Override
    protected String loadPullLabel(Context context, TypedArray attrs, Mode mode) {
        return context.getString(R.string.pull_label);
    }

    @Override
    protected String loadRefreshingLabel(Context context, TypedArray attrs, Mode mode) {
        return context.getString(R.string.refresh_label);
    }

    @Override
    protected String loadReleaseLabel(Context context, TypedArray attrs, Mode mode) {
        return context.getString(R.string.release_label);
    }

    protected void onPullImpl(float scaleOfLayout) {
        float angle;
        if (mRotateDrawableWhilePulling) {
            angle = scaleOfLayout * 90f;
        } else {
            angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
        }

        mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
        mHeaderImage.setImageMatrix(mHeaderImageMatrix);
    }

    @Override
    protected void refreshingImpl() {
        mHeaderImage.startAnimation(mRotateAnimation);
    }

    @Override
    protected void resetImpl() {
        mHeaderImage.clearAnimation();
        resetImageRotation();
    }

    private void resetImageRotation() {
        if (null != mHeaderImageMatrix) {
            mHeaderImageMatrix.reset();
            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        }
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

@Override
    protected Integer getLayoutForVertical(){
        return R.layout.ptr_header_vertical_custom;
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.ic_refresh_grey600;
    }
}