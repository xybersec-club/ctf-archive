package androidx.window.embedding;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.WindowMetrics;
import androidx.window.extensions.embedding.ActivityRule;
import androidx.window.extensions.embedding.SplitPairRule;
import androidx.window.extensions.embedding.SplitPlaceholderRule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: EmbeddingAdapter.kt */
@Metadata(d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0001\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u001a\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00040\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0007J\u001a\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\tJ(\u0010\r\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000f0\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\tH\u0007J(\u0010\u0014\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100\u000f0\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\tH\u0007J\u001c\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u000e2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\tH\u0007J\u001c\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00110\u000e2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\tH\u0007J\u0016\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000e2\u0006\u0010\u001b\u001a\u00020\u001cH\u0007J*\u0010\u001d\u001a\u0002H\u001e\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010\u001f*\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002H\u001f0\u000fH\u0082\u0002¢\u0006\u0002\u0010 J*\u0010!\u001a\u0002H\u001f\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010\u001f*\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002H\u001f0\u000fH\u0082\u0002¢\u0006\u0002\u0010 ¨\u0006\""}, d2 = {"Landroidx/window/embedding/EmbeddingAdapter;", "", "()V", "translate", "Landroidx/window/embedding/SplitInfo;", "splitInfo", "Landroidx/window/extensions/embedding/SplitInfo;", "", "splitInfoList", "", "Landroidx/window/extensions/embedding/EmbeddingRule;", "rules", "Landroidx/window/embedding/EmbeddingRule;", "translateActivityIntentPredicates", "Ljava/util/function/Predicate;", "Landroid/util/Pair;", "Landroid/app/Activity;", "Landroid/content/Intent;", "splitPairFilters", "Landroidx/window/embedding/SplitPairFilter;", "translateActivityPairPredicates", "translateActivityPredicates", "activityFilters", "Landroidx/window/embedding/ActivityFilter;", "translateIntentPredicates", "translateParentMetricsPredicate", "Landroid/view/WindowMetrics;", "splitRule", "Landroidx/window/embedding/SplitRule;", "component1", "F", "S", "(Landroid/util/Pair;)Ljava/lang/Object;", "component2", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class EmbeddingAdapter {
    public final List<SplitInfo> translate(List<? extends androidx.window.extensions.embedding.SplitInfo> splitInfoList) {
        Intrinsics.checkNotNullParameter(splitInfoList, "splitInfoList");
        List<? extends androidx.window.extensions.embedding.SplitInfo> $this$map$iv = splitInfoList;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            androidx.window.extensions.embedding.SplitInfo p0 = (androidx.window.extensions.embedding.SplitInfo) item$iv$iv;
            destination$iv$iv.add(translate(p0));
        }
        return (List) destination$iv$iv;
    }

    private final SplitInfo translate(androidx.window.extensions.embedding.SplitInfo splitInfo) {
        boolean isPrimaryStackEmpty;
        androidx.window.extensions.embedding.ActivityStack primaryActivityStack = splitInfo.getPrimaryActivityStack();
        Intrinsics.checkNotNullExpressionValue(primaryActivityStack, "splitInfo.primaryActivityStack");
        boolean isSecondaryStackEmpty = false;
        try {
            isPrimaryStackEmpty = primaryActivityStack.isEmpty();
        } catch (NoSuchMethodError e) {
            isPrimaryStackEmpty = false;
        }
        List activities = primaryActivityStack.getActivities();
        Intrinsics.checkNotNullExpressionValue(activities, "primaryActivityStack.activities");
        ActivityStack primaryFragment = new ActivityStack(activities, isPrimaryStackEmpty);
        androidx.window.extensions.embedding.ActivityStack secondaryActivityStack = splitInfo.getSecondaryActivityStack();
        Intrinsics.checkNotNullExpressionValue(secondaryActivityStack, "splitInfo.secondaryActivityStack");
        try {
            isSecondaryStackEmpty = secondaryActivityStack.isEmpty();
        } catch (NoSuchMethodError e2) {
        }
        List activities2 = secondaryActivityStack.getActivities();
        Intrinsics.checkNotNullExpressionValue(activities2, "secondaryActivityStack.activities");
        ActivityStack secondaryFragment = new ActivityStack(activities2, isSecondaryStackEmpty);
        return new SplitInfo(primaryFragment, secondaryFragment, splitInfo.getSplitRatio());
    }

    public final Predicate<Pair<Activity, Activity>> translateActivityPairPredicates(final Set<SplitPairFilter> splitPairFilters) {
        Intrinsics.checkNotNullParameter(splitPairFilters, "splitPairFilters");
        return new Predicate() { // from class: androidx.window.embedding.EmbeddingAdapter$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean m11translateActivityPairPredicates$lambda1;
                m11translateActivityPairPredicates$lambda1 = EmbeddingAdapter.m11translateActivityPairPredicates$lambda1(EmbeddingAdapter.this, splitPairFilters, (Pair) obj);
                return m11translateActivityPairPredicates$lambda1;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: translateActivityPairPredicates$lambda-1  reason: not valid java name */
    public static final boolean m11translateActivityPairPredicates$lambda1(EmbeddingAdapter this$0, Set splitPairFilters, Pair $dstr$first$second) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(splitPairFilters, "$splitPairFilters");
        Intrinsics.checkNotNullExpressionValue($dstr$first$second, "(first, second)");
        Activity first = (Activity) this$0.component1($dstr$first$second);
        Activity second = (Activity) this$0.component2($dstr$first$second);
        Set $this$any$iv = splitPairFilters;
        if (($this$any$iv instanceof Collection) && $this$any$iv.isEmpty()) {
            return false;
        }
        for (Object element$iv : $this$any$iv) {
            SplitPairFilter filter = (SplitPairFilter) element$iv;
            if (filter.matchesActivityPair(first, second)) {
                return true;
            }
        }
        return false;
    }

    public final Predicate<Pair<Activity, Intent>> translateActivityIntentPredicates(final Set<SplitPairFilter> splitPairFilters) {
        Intrinsics.checkNotNullParameter(splitPairFilters, "splitPairFilters");
        return new Predicate() { // from class: androidx.window.embedding.EmbeddingAdapter$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean m10translateActivityIntentPredicates$lambda3;
                m10translateActivityIntentPredicates$lambda3 = EmbeddingAdapter.m10translateActivityIntentPredicates$lambda3(EmbeddingAdapter.this, splitPairFilters, (Pair) obj);
                return m10translateActivityIntentPredicates$lambda3;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: translateActivityIntentPredicates$lambda-3  reason: not valid java name */
    public static final boolean m10translateActivityIntentPredicates$lambda3(EmbeddingAdapter this$0, Set splitPairFilters, Pair $dstr$first$second) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(splitPairFilters, "$splitPairFilters");
        Intrinsics.checkNotNullExpressionValue($dstr$first$second, "(first, second)");
        Activity first = (Activity) this$0.component1($dstr$first$second);
        Intent second = (Intent) this$0.component2($dstr$first$second);
        Set $this$any$iv = splitPairFilters;
        if (($this$any$iv instanceof Collection) && $this$any$iv.isEmpty()) {
            return false;
        }
        for (Object element$iv : $this$any$iv) {
            SplitPairFilter filter = (SplitPairFilter) element$iv;
            if (filter.matchesActivityIntentPair(first, second)) {
                return true;
            }
        }
        return false;
    }

    public final Predicate<WindowMetrics> translateParentMetricsPredicate(final SplitRule splitRule) {
        Intrinsics.checkNotNullParameter(splitRule, "splitRule");
        return new Predicate() { // from class: androidx.window.embedding.EmbeddingAdapter$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean m14translateParentMetricsPredicate$lambda4;
                m14translateParentMetricsPredicate$lambda4 = EmbeddingAdapter.m14translateParentMetricsPredicate$lambda4(SplitRule.this, (WindowMetrics) obj);
                return m14translateParentMetricsPredicate$lambda4;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: translateParentMetricsPredicate$lambda-4  reason: not valid java name */
    public static final boolean m14translateParentMetricsPredicate$lambda4(SplitRule splitRule, WindowMetrics windowMetrics) {
        Intrinsics.checkNotNullParameter(splitRule, "$splitRule");
        Intrinsics.checkNotNullExpressionValue(windowMetrics, "windowMetrics");
        return splitRule.checkParentMetrics(windowMetrics);
    }

    public final Predicate<Activity> translateActivityPredicates(final Set<ActivityFilter> activityFilters) {
        Intrinsics.checkNotNullParameter(activityFilters, "activityFilters");
        return new Predicate() { // from class: androidx.window.embedding.EmbeddingAdapter$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean m12translateActivityPredicates$lambda6;
                m12translateActivityPredicates$lambda6 = EmbeddingAdapter.m12translateActivityPredicates$lambda6(activityFilters, (Activity) obj);
                return m12translateActivityPredicates$lambda6;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: translateActivityPredicates$lambda-6  reason: not valid java name */
    public static final boolean m12translateActivityPredicates$lambda6(Set activityFilters, Activity activity) {
        Intrinsics.checkNotNullParameter(activityFilters, "$activityFilters");
        Set $this$any$iv = activityFilters;
        if (($this$any$iv instanceof Collection) && $this$any$iv.isEmpty()) {
            return false;
        }
        for (Object element$iv : $this$any$iv) {
            ActivityFilter filter = (ActivityFilter) element$iv;
            Intrinsics.checkNotNullExpressionValue(activity, "activity");
            if (filter.matchesActivity(activity)) {
                return true;
            }
        }
        return false;
    }

    public final Predicate<Intent> translateIntentPredicates(final Set<ActivityFilter> activityFilters) {
        Intrinsics.checkNotNullParameter(activityFilters, "activityFilters");
        return new Predicate() { // from class: androidx.window.embedding.EmbeddingAdapter$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean m13translateIntentPredicates$lambda8;
                m13translateIntentPredicates$lambda8 = EmbeddingAdapter.m13translateIntentPredicates$lambda8(activityFilters, (Intent) obj);
                return m13translateIntentPredicates$lambda8;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: translateIntentPredicates$lambda-8  reason: not valid java name */
    public static final boolean m13translateIntentPredicates$lambda8(Set activityFilters, Intent intent) {
        Intrinsics.checkNotNullParameter(activityFilters, "$activityFilters");
        Set $this$any$iv = activityFilters;
        if (($this$any$iv instanceof Collection) && $this$any$iv.isEmpty()) {
            return false;
        }
        for (Object element$iv : $this$any$iv) {
            ActivityFilter filter = (ActivityFilter) element$iv;
            Intrinsics.checkNotNullExpressionValue(intent, "intent");
            if (filter.matchesIntent(intent)) {
                return true;
            }
        }
        return false;
    }

    public final Set<androidx.window.extensions.embedding.EmbeddingRule> translate(Set<? extends EmbeddingRule> rules) {
        androidx.window.extensions.embedding.SplitPairRule build;
        Intrinsics.checkNotNullParameter(rules, "rules");
        Set<? extends EmbeddingRule> $this$map$iv = rules;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            EmbeddingRule rule = (EmbeddingRule) item$iv$iv;
            if (rule instanceof SplitPairRule) {
                build = new SplitPairRule.Builder(translateActivityPairPredicates(((SplitPairRule) rule).getFilters()), translateActivityIntentPredicates(((SplitPairRule) rule).getFilters()), translateParentMetricsPredicate((SplitRule) rule)).setSplitRatio(((SplitPairRule) rule).getSplitRatio()).setLayoutDirection(((SplitPairRule) rule).getLayoutDirection()).setShouldFinishPrimaryWithSecondary(((SplitPairRule) rule).getFinishPrimaryWithSecondary()).setShouldFinishSecondaryWithPrimary(((SplitPairRule) rule).getFinishSecondaryWithPrimary()).setShouldClearTop(((SplitPairRule) rule).getClearTop()).build();
                Intrinsics.checkNotNullExpressionValue(build, "SplitPairRuleBuilder(\n  …                 .build()");
            } else if (rule instanceof SplitPlaceholderRule) {
                build = new SplitPlaceholderRule.Builder(((SplitPlaceholderRule) rule).getPlaceholderIntent(), translateActivityPredicates(((SplitPlaceholderRule) rule).getFilters()), translateIntentPredicates(((SplitPlaceholderRule) rule).getFilters()), translateParentMetricsPredicate((SplitRule) rule)).setSplitRatio(((SplitPlaceholderRule) rule).getSplitRatio()).setLayoutDirection(((SplitPlaceholderRule) rule).getLayoutDirection()).build();
                Intrinsics.checkNotNullExpressionValue(build, "SplitPlaceholderRuleBuil…                 .build()");
            } else if (rule instanceof ActivityRule) {
                build = new ActivityRule.Builder(translateActivityPredicates(((ActivityRule) rule).getFilters()), translateIntentPredicates(((ActivityRule) rule).getFilters())).setShouldAlwaysExpand(((ActivityRule) rule).getAlwaysExpand()).build();
                Intrinsics.checkNotNullExpressionValue(build, "ActivityRuleBuilder(\n   …                 .build()");
            } else {
                throw new IllegalArgumentException("Unsupported rule type");
            }
            destination$iv$iv.add((androidx.window.extensions.embedding.EmbeddingRule) build);
        }
        return CollectionsKt.toSet((List) destination$iv$iv);
    }

    private final <F, S> F component1(Pair<F, S> pair) {
        Intrinsics.checkNotNullParameter(pair, "<this>");
        return (F) pair.first;
    }

    private final <F, S> S component2(Pair<F, S> pair) {
        Intrinsics.checkNotNullParameter(pair, "<this>");
        return (S) pair.second;
    }
}
