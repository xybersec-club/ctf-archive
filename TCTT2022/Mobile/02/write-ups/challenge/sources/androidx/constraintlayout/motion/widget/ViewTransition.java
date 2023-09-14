package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.core.motion.utils.KeyCache;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ViewTransition {
    static final int ANTICIPATE = 6;
    static final int BOUNCE = 4;
    public static final String CONSTRAINT_OVERRIDE = "ConstraintOverride";
    public static final String CUSTOM_ATTRIBUTE = "CustomAttribute";
    public static final String CUSTOM_METHOD = "CustomMethod";
    static final int EASE_IN = 1;
    static final int EASE_IN_OUT = 0;
    static final int EASE_OUT = 2;
    private static final int INTERPOLATOR_REFERENCE_ID = -2;
    public static final String KEY_FRAME_SET_TAG = "KeyFrameSet";
    static final int LINEAR = 3;
    public static final int ONSTATE_ACTION_DOWN = 1;
    public static final int ONSTATE_ACTION_DOWN_UP = 3;
    public static final int ONSTATE_ACTION_UP = 2;
    public static final int ONSTATE_SHARED_VALUE_SET = 4;
    public static final int ONSTATE_SHARED_VALUE_UNSET = 5;
    static final int OVERSHOOT = 5;
    private static final int SPLINE_STRING = -1;
    private static String TAG = "ViewTransition";
    private static final int UNSET = -1;
    static final int VIEWTRANSITIONMODE_ALLSTATES = 1;
    static final int VIEWTRANSITIONMODE_CURRENTSTATE = 0;
    static final int VIEWTRANSITIONMODE_NOSTATE = 2;
    public static final String VIEW_TRANSITION_TAG = "ViewTransition";
    ConstraintSet.Constraint mConstraintDelta;
    Context mContext;
    private int mId;
    KeyFrames mKeyFrames;
    private int mTargetId;
    private String mTargetString;
    int mViewTransitionMode;
    ConstraintSet set;
    private int mOnStateTransition = -1;
    private boolean mDisabled = false;
    private int mPathMotionArc = 0;
    private int mDuration = -1;
    private int mUpDuration = -1;
    private int mDefaultInterpolator = 0;
    private String mDefaultInterpolatorString = null;
    private int mDefaultInterpolatorID = -1;
    private int mSetsTag = -1;
    private int mClearsTag = -1;
    private int mIfTagSet = -1;
    private int mIfTagNotSet = -1;
    private int mSharedValueTarget = -1;
    private int mSharedValueID = -1;
    private int mSharedValueCurrent = -1;

    public int getSharedValueCurrent() {
        return this.mSharedValueCurrent;
    }

    public void setSharedValueCurrent(int sharedValueCurrent) {
        this.mSharedValueCurrent = sharedValueCurrent;
    }

    public int getStateTransition() {
        return this.mOnStateTransition;
    }

    public void setStateTransition(int stateTransition) {
        this.mOnStateTransition = stateTransition;
    }

    public int getSharedValue() {
        return this.mSharedValueTarget;
    }

    public void setSharedValue(int sharedValue) {
        this.mSharedValueTarget = sharedValue;
    }

    public int getSharedValueID() {
        return this.mSharedValueID;
    }

    public void setSharedValueID(int sharedValueID) {
        this.mSharedValueID = sharedValueID;
    }

    public String toString() {
        return "ViewTransition(" + Debug.getName(this.mContext, this.mId) + ")";
    }

    Interpolator getInterpolator(Context context) {
        int i = this.mDefaultInterpolator;
        if (i != -2) {
            if (i == -1) {
                final Easing interpolator = Easing.getInterpolator(this.mDefaultInterpolatorString);
                return new Interpolator(this) { // from class: androidx.constraintlayout.motion.widget.ViewTransition.1
                    @Override // android.animation.TimeInterpolator
                    public float getInterpolation(float v) {
                        return (float) interpolator.get(v);
                    }
                };
            } else if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 4) {
                            if (i != 5) {
                                if (i != 6) {
                                    return null;
                                }
                                return new AnticipateInterpolator();
                            }
                            return new OvershootInterpolator();
                        }
                        return new BounceInterpolator();
                    }
                    return new DecelerateInterpolator();
                }
                return new AccelerateInterpolator();
            } else {
                return new AccelerateDecelerateInterpolator();
            }
        }
        return AnimationUtils.loadInterpolator(context, this.mDefaultInterpolatorID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewTransition(Context context, XmlPullParser parser) {
        boolean z;
        this.mContext = context;
        try {
            int eventType = parser.getEventType();
            while (eventType != 1) {
                if (eventType == 2) {
                    String name = parser.getName();
                    switch (name.hashCode()) {
                        case -1962203927:
                            if (name.equals(CONSTRAINT_OVERRIDE)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        case -1239391468:
                            if (name.equals(KEY_FRAME_SET_TAG)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        case 61998586:
                            if (name.equals(VIEW_TRANSITION_TAG)) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        case 366511058:
                            if (name.equals(CUSTOM_METHOD)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        case 1791837707:
                            if (name.equals(CUSTOM_ATTRIBUTE)) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        default:
                            z = true;
                            break;
                    }
                    if (!z) {
                        parseViewTransitionTags(context, parser);
                    } else if (z) {
                        this.mKeyFrames = new KeyFrames(context, parser);
                    } else if (z) {
                        this.mConstraintDelta = ConstraintSet.buildDelta(context, parser);
                    } else if (z || z) {
                        ConstraintAttribute.parse(context, parser, this.mConstraintDelta.mCustomConstraints);
                    } else {
                        Log.e(TAG, Debug.getLoc() + " unknown tag " + name);
                        Log.e(TAG, ".xml:" + parser.getLineNumber());
                    }
                } else if (eventType != 3) {
                    continue;
                } else if (VIEW_TRANSITION_TAG.equals(parser.getName())) {
                    return;
                }
                eventType = parser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e2) {
            e2.printStackTrace();
        }
    }

    private void parseViewTransitionTags(Context context, XmlPullParser parser) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.ViewTransition);
        int indexCount = obtainStyledAttributes.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = obtainStyledAttributes.getIndex(i);
            if (index == R.styleable.ViewTransition_android_id) {
                this.mId = obtainStyledAttributes.getResourceId(index, this.mId);
            } else if (index == R.styleable.ViewTransition_motionTarget) {
                if (MotionLayout.IS_IN_EDIT_MODE) {
                    int resourceId = obtainStyledAttributes.getResourceId(index, this.mTargetId);
                    this.mTargetId = resourceId;
                    if (resourceId == -1) {
                        this.mTargetString = obtainStyledAttributes.getString(index);
                    }
                } else if (obtainStyledAttributes.peekValue(index).type == 3) {
                    this.mTargetString = obtainStyledAttributes.getString(index);
                } else {
                    this.mTargetId = obtainStyledAttributes.getResourceId(index, this.mTargetId);
                }
            } else if (index == R.styleable.ViewTransition_onStateTransition) {
                this.mOnStateTransition = obtainStyledAttributes.getInt(index, this.mOnStateTransition);
            } else if (index == R.styleable.ViewTransition_transitionDisable) {
                this.mDisabled = obtainStyledAttributes.getBoolean(index, this.mDisabled);
            } else if (index == R.styleable.ViewTransition_pathMotionArc) {
                this.mPathMotionArc = obtainStyledAttributes.getInt(index, this.mPathMotionArc);
            } else if (index == R.styleable.ViewTransition_duration) {
                this.mDuration = obtainStyledAttributes.getInt(index, this.mDuration);
            } else if (index == R.styleable.ViewTransition_upDuration) {
                this.mUpDuration = obtainStyledAttributes.getInt(index, this.mUpDuration);
            } else if (index == R.styleable.ViewTransition_viewTransitionMode) {
                this.mViewTransitionMode = obtainStyledAttributes.getInt(index, this.mViewTransitionMode);
            } else if (index == R.styleable.ViewTransition_motionInterpolator) {
                TypedValue peekValue = obtainStyledAttributes.peekValue(index);
                if (peekValue.type == 1) {
                    int resourceId2 = obtainStyledAttributes.getResourceId(index, -1);
                    this.mDefaultInterpolatorID = resourceId2;
                    if (resourceId2 != -1) {
                        this.mDefaultInterpolator = -2;
                    }
                } else if (peekValue.type == 3) {
                    String string = obtainStyledAttributes.getString(index);
                    this.mDefaultInterpolatorString = string;
                    if (string != null && string.indexOf("/") > 0) {
                        this.mDefaultInterpolatorID = obtainStyledAttributes.getResourceId(index, -1);
                        this.mDefaultInterpolator = -2;
                    } else {
                        this.mDefaultInterpolator = -1;
                    }
                } else {
                    this.mDefaultInterpolator = obtainStyledAttributes.getInteger(index, this.mDefaultInterpolator);
                }
            } else if (index == R.styleable.ViewTransition_setsTag) {
                this.mSetsTag = obtainStyledAttributes.getResourceId(index, this.mSetsTag);
            } else if (index == R.styleable.ViewTransition_clearsTag) {
                this.mClearsTag = obtainStyledAttributes.getResourceId(index, this.mClearsTag);
            } else if (index == R.styleable.ViewTransition_ifTagSet) {
                this.mIfTagSet = obtainStyledAttributes.getResourceId(index, this.mIfTagSet);
            } else if (index == R.styleable.ViewTransition_ifTagNotSet) {
                this.mIfTagNotSet = obtainStyledAttributes.getResourceId(index, this.mIfTagNotSet);
            } else if (index == R.styleable.ViewTransition_SharedValueId) {
                this.mSharedValueID = obtainStyledAttributes.getResourceId(index, this.mSharedValueID);
            } else if (index == R.styleable.ViewTransition_SharedValue) {
                this.mSharedValueTarget = obtainStyledAttributes.getInteger(index, this.mSharedValueTarget);
            }
        }
        obtainStyledAttributes.recycle();
    }

    void applyIndependentTransition(ViewTransitionController controller, MotionLayout motionLayout, View view) {
        MotionController motionController = new MotionController(view);
        motionController.setBothStates(view);
        this.mKeyFrames.addAllFrames(motionController);
        motionController.setup(motionLayout.getWidth(), motionLayout.getHeight(), this.mDuration, System.nanoTime());
        new Animate(controller, motionController, this.mDuration, this.mUpDuration, this.mOnStateTransition, getInterpolator(motionLayout.getContext()), this.mSetsTag, this.mClearsTag);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Animate {
        boolean hold_at_100;
        private final int mClearsTag;
        float mDpositionDt;
        int mDuration;
        Interpolator mInterpolator;
        long mLastRender;
        MotionController mMC;
        float mPosition;
        private final int mSetsTag;
        long mStart;
        int mUpDuration;
        ViewTransitionController mVtController;
        KeyCache mCache = new KeyCache();
        boolean reverse = false;
        Rect mTempRec = new Rect();

        Animate(ViewTransitionController controller, MotionController motionController, int duration, int upDuration, int mode, Interpolator interpolator, int setTag, int clearTag) {
            this.hold_at_100 = false;
            this.mVtController = controller;
            this.mMC = motionController;
            this.mDuration = duration;
            this.mUpDuration = upDuration;
            long nanoTime = System.nanoTime();
            this.mStart = nanoTime;
            this.mLastRender = nanoTime;
            this.mVtController.addAnimation(this);
            this.mInterpolator = interpolator;
            this.mSetsTag = setTag;
            this.mClearsTag = clearTag;
            if (mode == 3) {
                this.hold_at_100 = true;
            }
            this.mDpositionDt = duration == 0 ? Float.MAX_VALUE : 1.0f / duration;
            mutate();
        }

        void reverse(boolean dir) {
            int i;
            this.reverse = dir;
            if (dir && (i = this.mUpDuration) != -1) {
                this.mDpositionDt = i == 0 ? Float.MAX_VALUE : 1.0f / i;
            }
            this.mVtController.invalidate();
            this.mLastRender = System.nanoTime();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void mutate() {
            if (this.reverse) {
                mutateReverse();
            } else {
                mutateForward();
            }
        }

        void mutateReverse() {
            long nanoTime = System.nanoTime();
            this.mLastRender = nanoTime;
            float f = this.mPosition - (((float) ((nanoTime - this.mLastRender) * 1.0E-6d)) * this.mDpositionDt);
            this.mPosition = f;
            if (f < 0.0f) {
                this.mPosition = 0.0f;
            }
            Interpolator interpolator = this.mInterpolator;
            float interpolation = interpolator == null ? this.mPosition : interpolator.getInterpolation(this.mPosition);
            MotionController motionController = this.mMC;
            boolean interpolate = motionController.interpolate(motionController.mView, interpolation, nanoTime, this.mCache);
            if (this.mPosition <= 0.0f) {
                if (this.mSetsTag != -1) {
                    this.mMC.getView().setTag(this.mSetsTag, Long.valueOf(System.nanoTime()));
                }
                if (this.mClearsTag != -1) {
                    this.mMC.getView().setTag(this.mClearsTag, null);
                }
                this.mVtController.removeAnimation(this);
            }
            if (this.mPosition > 0.0f || interpolate) {
                this.mVtController.invalidate();
            }
        }

        void mutateForward() {
            long nanoTime = System.nanoTime();
            this.mLastRender = nanoTime;
            float f = this.mPosition + (((float) ((nanoTime - this.mLastRender) * 1.0E-6d)) * this.mDpositionDt);
            this.mPosition = f;
            if (f >= 1.0f) {
                this.mPosition = 1.0f;
            }
            Interpolator interpolator = this.mInterpolator;
            float interpolation = interpolator == null ? this.mPosition : interpolator.getInterpolation(this.mPosition);
            MotionController motionController = this.mMC;
            boolean interpolate = motionController.interpolate(motionController.mView, interpolation, nanoTime, this.mCache);
            if (this.mPosition >= 1.0f) {
                if (this.mSetsTag != -1) {
                    this.mMC.getView().setTag(this.mSetsTag, Long.valueOf(System.nanoTime()));
                }
                if (this.mClearsTag != -1) {
                    this.mMC.getView().setTag(this.mClearsTag, null);
                }
                if (!this.hold_at_100) {
                    this.mVtController.removeAnimation(this);
                }
            }
            if (this.mPosition < 1.0f || interpolate) {
                this.mVtController.invalidate();
            }
        }

        public void reactTo(int action, float x, float y) {
            if (action == 1) {
                if (this.reverse) {
                    return;
                }
                reverse(true);
            } else if (action != 2) {
            } else {
                this.mMC.getView().getHitRect(this.mTempRec);
                if (this.mTempRec.contains((int) x, (int) y) || this.reverse) {
                    return;
                }
                reverse(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyTransition(ViewTransitionController controller, MotionLayout layout, int fromId, ConstraintSet current, final View... views) {
        int[] constraintSetIds;
        if (this.mDisabled) {
            return;
        }
        int i = this.mViewTransitionMode;
        if (i == 2) {
            applyIndependentTransition(controller, layout, views[0]);
            return;
        }
        if (i == 1) {
            for (int i2 : layout.getConstraintSetIds()) {
                if (i2 != fromId) {
                    ConstraintSet constraintSet = layout.getConstraintSet(i2);
                    for (View view : views) {
                        ConstraintSet.Constraint constraint = constraintSet.getConstraint(view.getId());
                        ConstraintSet.Constraint constraint2 = this.mConstraintDelta;
                        if (constraint2 != null) {
                            constraint2.applyDelta(constraint);
                            constraint.mCustomConstraints.putAll(this.mConstraintDelta.mCustomConstraints);
                        }
                    }
                }
            }
        }
        ConstraintSet constraintSet2 = new ConstraintSet();
        constraintSet2.clone(current);
        for (View view2 : views) {
            ConstraintSet.Constraint constraint3 = constraintSet2.getConstraint(view2.getId());
            ConstraintSet.Constraint constraint4 = this.mConstraintDelta;
            if (constraint4 != null) {
                constraint4.applyDelta(constraint3);
                constraint3.mCustomConstraints.putAll(this.mConstraintDelta.mCustomConstraints);
            }
        }
        layout.updateState(fromId, constraintSet2);
        layout.updateState(R.id.view_transition, current);
        layout.setState(R.id.view_transition, -1, -1);
        MotionScene.Transition transition = new MotionScene.Transition(-1, layout.mScene, R.id.view_transition, fromId);
        for (View view3 : views) {
            updateTransition(transition, view3);
        }
        layout.setTransition(transition);
        layout.transitionToEnd(new Runnable() { // from class: androidx.constraintlayout.motion.widget.ViewTransition$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ViewTransition.this.m13x14d7500(views);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$applyTransition$0$androidx-constraintlayout-motion-widget-ViewTransition  reason: not valid java name */
    public /* synthetic */ void m13x14d7500(View[] viewArr) {
        if (this.mSetsTag != -1) {
            for (View view : viewArr) {
                view.setTag(this.mSetsTag, Long.valueOf(System.nanoTime()));
            }
        }
        if (this.mClearsTag != -1) {
            for (View view2 : viewArr) {
                view2.setTag(this.mClearsTag, null);
            }
        }
    }

    private void updateTransition(MotionScene.Transition transition, View view) {
        int i = this.mDuration;
        if (i != -1) {
            transition.setDuration(i);
        }
        transition.setPathMotionArc(this.mPathMotionArc);
        transition.setInterpolatorInfo(this.mDefaultInterpolator, this.mDefaultInterpolatorString, this.mDefaultInterpolatorID);
        int id = view.getId();
        KeyFrames keyFrames = this.mKeyFrames;
        if (keyFrames != null) {
            ArrayList<Key> keyFramesForView = keyFrames.getKeyFramesForView(-1);
            KeyFrames keyFrames2 = new KeyFrames();
            Iterator<Key> it = keyFramesForView.iterator();
            while (it.hasNext()) {
                keyFrames2.addKey(it.next().mo12clone().setViewId(id));
            }
            transition.addKeyFrame(keyFrames2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getId() {
        return this.mId;
    }

    void setId(int id) {
        this.mId = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchesView(View view) {
        String str;
        if (view == null) {
            return false;
        }
        if (!(this.mTargetId == -1 && this.mTargetString == null) && checkTags(view)) {
            if (view.getId() == this.mTargetId) {
                return true;
            }
            return this.mTargetString != null && (view.getLayoutParams() instanceof ConstraintLayout.LayoutParams) && (str = ((ConstraintLayout.LayoutParams) view.getLayoutParams()).constraintTag) != null && str.matches(this.mTargetString);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean supports(int action) {
        int i = this.mOnStateTransition;
        return i == 1 ? action == 0 : i == 2 ? action == 1 : i == 3 && action == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEnabled() {
        return !this.mDisabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnabled(boolean enable) {
        this.mDisabled = !enable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean checkTags(View view) {
        int i = this.mIfTagSet;
        boolean z = i == -1 || view.getTag(i) != null;
        int i2 = this.mIfTagNotSet;
        return z && (i2 == -1 || view.getTag(i2) == null);
    }
}
