package com.google.android.material.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.util.Pair;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.dialog.InsetDialogOnTouchListener;
import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.internal.EdgeToEdgeUtils;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.resources.MaterialAttributes;
import com.google.android.material.shape.MaterialShapeDrawable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;
import java.util.LinkedHashSet;
/* loaded from: classes.dex */
public final class MaterialDatePicker<S> extends DialogFragment {
    private static final String CALENDAR_CONSTRAINTS_KEY = "CALENDAR_CONSTRAINTS_KEY";
    private static final String DATE_SELECTOR_KEY = "DATE_SELECTOR_KEY";
    public static final int INPUT_MODE_CALENDAR = 0;
    private static final String INPUT_MODE_KEY = "INPUT_MODE_KEY";
    public static final int INPUT_MODE_TEXT = 1;
    private static final String NEGATIVE_BUTTON_TEXT_KEY = "NEGATIVE_BUTTON_TEXT_KEY";
    private static final String NEGATIVE_BUTTON_TEXT_RES_ID_KEY = "NEGATIVE_BUTTON_TEXT_RES_ID_KEY";
    private static final String OVERRIDE_THEME_RES_ID = "OVERRIDE_THEME_RES_ID";
    private static final String POSITIVE_BUTTON_TEXT_KEY = "POSITIVE_BUTTON_TEXT_KEY";
    private static final String POSITIVE_BUTTON_TEXT_RES_ID_KEY = "POSITIVE_BUTTON_TEXT_RES_ID_KEY";
    private static final String TITLE_TEXT_KEY = "TITLE_TEXT_KEY";
    private static final String TITLE_TEXT_RES_ID_KEY = "TITLE_TEXT_RES_ID_KEY";
    private MaterialShapeDrawable background;
    private MaterialCalendar<S> calendar;
    private CalendarConstraints calendarConstraints;
    private Button confirmButton;
    private DateSelector<S> dateSelector;
    private boolean edgeToEdgeEnabled;
    private boolean fullscreen;
    private TextView headerSelectionText;
    private CheckableImageButton headerToggleButton;
    private int inputMode;
    private CharSequence negativeButtonText;
    private int negativeButtonTextResId;
    private int overrideThemeResId;
    private PickerFragment<S> pickerFragment;
    private CharSequence positiveButtonText;
    private int positiveButtonTextResId;
    private CharSequence titleText;
    private int titleTextResId;
    static final Object CONFIRM_BUTTON_TAG = "CONFIRM_BUTTON_TAG";
    static final Object CANCEL_BUTTON_TAG = "CANCEL_BUTTON_TAG";
    static final Object TOGGLE_BUTTON_TAG = "TOGGLE_BUTTON_TAG";
    private final LinkedHashSet<MaterialPickerOnPositiveButtonClickListener<? super S>> onPositiveButtonClickListeners = new LinkedHashSet<>();
    private final LinkedHashSet<View.OnClickListener> onNegativeButtonClickListeners = new LinkedHashSet<>();
    private final LinkedHashSet<DialogInterface.OnCancelListener> onCancelListeners = new LinkedHashSet<>();
    private final LinkedHashSet<DialogInterface.OnDismissListener> onDismissListeners = new LinkedHashSet<>();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface InputMode {
    }

    public static long todayInUtcMilliseconds() {
        return UtcDates.getTodayCalendar().getTimeInMillis();
    }

    public static long thisMonthInUtcMilliseconds() {
        return Month.current().timeInMillis;
    }

    public String getHeaderText() {
        return getDateSelector().getSelectionDisplayString(getContext());
    }

    static <S> MaterialDatePicker<S> newInstance(Builder<S> builder) {
        MaterialDatePicker<S> materialDatePicker = new MaterialDatePicker<>();
        Bundle bundle = new Bundle();
        bundle.putInt(OVERRIDE_THEME_RES_ID, builder.overrideThemeResId);
        bundle.putParcelable(DATE_SELECTOR_KEY, builder.dateSelector);
        bundle.putParcelable(CALENDAR_CONSTRAINTS_KEY, builder.calendarConstraints);
        bundle.putInt(TITLE_TEXT_RES_ID_KEY, builder.titleTextResId);
        bundle.putCharSequence(TITLE_TEXT_KEY, builder.titleText);
        bundle.putInt(INPUT_MODE_KEY, builder.inputMode);
        bundle.putInt(POSITIVE_BUTTON_TEXT_RES_ID_KEY, builder.positiveButtonTextResId);
        bundle.putCharSequence(POSITIVE_BUTTON_TEXT_KEY, builder.positiveButtonText);
        bundle.putInt(NEGATIVE_BUTTON_TEXT_RES_ID_KEY, builder.negativeButtonTextResId);
        bundle.putCharSequence(NEGATIVE_BUTTON_TEXT_KEY, builder.negativeButtonText);
        materialDatePicker.setArguments(bundle);
        return materialDatePicker;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(OVERRIDE_THEME_RES_ID, this.overrideThemeResId);
        bundle.putParcelable(DATE_SELECTOR_KEY, this.dateSelector);
        CalendarConstraints.Builder builder = new CalendarConstraints.Builder(this.calendarConstraints);
        if (this.calendar.getCurrentMonth() != null) {
            builder.setOpenAt(this.calendar.getCurrentMonth().timeInMillis);
        }
        bundle.putParcelable(CALENDAR_CONSTRAINTS_KEY, builder.build());
        bundle.putInt(TITLE_TEXT_RES_ID_KEY, this.titleTextResId);
        bundle.putCharSequence(TITLE_TEXT_KEY, this.titleText);
        bundle.putInt(POSITIVE_BUTTON_TEXT_RES_ID_KEY, this.positiveButtonTextResId);
        bundle.putCharSequence(POSITIVE_BUTTON_TEXT_KEY, this.positiveButtonText);
        bundle.putInt(NEGATIVE_BUTTON_TEXT_RES_ID_KEY, this.negativeButtonTextResId);
        bundle.putCharSequence(NEGATIVE_BUTTON_TEXT_KEY, this.negativeButtonText);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            bundle = getArguments();
        }
        this.overrideThemeResId = bundle.getInt(OVERRIDE_THEME_RES_ID);
        this.dateSelector = (DateSelector) bundle.getParcelable(DATE_SELECTOR_KEY);
        this.calendarConstraints = (CalendarConstraints) bundle.getParcelable(CALENDAR_CONSTRAINTS_KEY);
        this.titleTextResId = bundle.getInt(TITLE_TEXT_RES_ID_KEY);
        this.titleText = bundle.getCharSequence(TITLE_TEXT_KEY);
        this.inputMode = bundle.getInt(INPUT_MODE_KEY);
        this.positiveButtonTextResId = bundle.getInt(POSITIVE_BUTTON_TEXT_RES_ID_KEY);
        this.positiveButtonText = bundle.getCharSequence(POSITIVE_BUTTON_TEXT_KEY);
        this.negativeButtonTextResId = bundle.getInt(NEGATIVE_BUTTON_TEXT_RES_ID_KEY);
        this.negativeButtonText = bundle.getCharSequence(NEGATIVE_BUTTON_TEXT_KEY);
    }

    private int getThemeResId(Context context) {
        int i = this.overrideThemeResId;
        return i != 0 ? i : getDateSelector().getDefaultThemeResId(context);
    }

    @Override // androidx.fragment.app.DialogFragment
    public final Dialog onCreateDialog(Bundle bundle) {
        Dialog dialog = new Dialog(requireContext(), getThemeResId(requireContext()));
        Context context = dialog.getContext();
        this.fullscreen = isFullscreen(context);
        int resolveOrThrow = MaterialAttributes.resolveOrThrow(context, R.attr.colorSurface, MaterialDatePicker.class.getCanonicalName());
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(context, null, R.attr.materialCalendarStyle, R.style.Widget_MaterialComponents_MaterialCalendar);
        this.background = materialShapeDrawable;
        materialShapeDrawable.initializeElevationOverlay(context);
        this.background.setFillColor(ColorStateList.valueOf(resolveOrThrow));
        this.background.setElevation(ViewCompat.getElevation(dialog.getWindow().getDecorView()));
        return dialog;
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(this.fullscreen ? R.layout.mtrl_picker_fullscreen : R.layout.mtrl_picker_dialog, viewGroup);
        Context context = inflate.getContext();
        if (this.fullscreen) {
            inflate.findViewById(R.id.mtrl_calendar_frame).setLayoutParams(new LinearLayout.LayoutParams(getPaddedPickerWidth(context), -2));
        } else {
            inflate.findViewById(R.id.mtrl_calendar_main_pane).setLayoutParams(new LinearLayout.LayoutParams(getPaddedPickerWidth(context), -1));
        }
        TextView textView = (TextView) inflate.findViewById(R.id.mtrl_picker_header_selection_text);
        this.headerSelectionText = textView;
        ViewCompat.setAccessibilityLiveRegion(textView, 1);
        this.headerToggleButton = (CheckableImageButton) inflate.findViewById(R.id.mtrl_picker_header_toggle);
        TextView textView2 = (TextView) inflate.findViewById(R.id.mtrl_picker_title_text);
        CharSequence charSequence = this.titleText;
        if (charSequence != null) {
            textView2.setText(charSequence);
        } else {
            textView2.setText(this.titleTextResId);
        }
        initHeaderToggle(context);
        this.confirmButton = (Button) inflate.findViewById(R.id.confirm_button);
        if (getDateSelector().isSelectionComplete()) {
            this.confirmButton.setEnabled(true);
        } else {
            this.confirmButton.setEnabled(false);
        }
        this.confirmButton.setTag(CONFIRM_BUTTON_TAG);
        CharSequence charSequence2 = this.positiveButtonText;
        if (charSequence2 != null) {
            this.confirmButton.setText(charSequence2);
        } else {
            int i = this.positiveButtonTextResId;
            if (i != 0) {
                this.confirmButton.setText(i);
            }
        }
        this.confirmButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Iterator it = MaterialDatePicker.this.onPositiveButtonClickListeners.iterator();
                while (it.hasNext()) {
                    ((MaterialPickerOnPositiveButtonClickListener) it.next()).onPositiveButtonClick(MaterialDatePicker.this.getSelection());
                }
                MaterialDatePicker.this.dismiss();
            }
        });
        Button button = (Button) inflate.findViewById(R.id.cancel_button);
        button.setTag(CANCEL_BUTTON_TAG);
        CharSequence charSequence3 = this.negativeButtonText;
        if (charSequence3 != null) {
            button.setText(charSequence3);
        } else {
            int i2 = this.negativeButtonTextResId;
            if (i2 != 0) {
                button.setText(i2);
            }
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Iterator it = MaterialDatePicker.this.onNegativeButtonClickListeners.iterator();
                while (it.hasNext()) {
                    ((View.OnClickListener) it.next()).onClick(view);
                }
                MaterialDatePicker.this.dismiss();
            }
        });
        return inflate;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Window window = requireDialog().getWindow();
        if (this.fullscreen) {
            window.setLayout(-1, -1);
            window.setBackgroundDrawable(this.background);
            enableEdgeToEdgeIfNeeded(window);
        } else {
            window.setLayout(-2, -2);
            int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.mtrl_calendar_dialog_background_inset);
            Rect rect = new Rect(dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset);
            window.setBackgroundDrawable(new InsetDrawable((Drawable) this.background, dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset));
            window.getDecorView().setOnTouchListener(new InsetDialogOnTouchListener(requireDialog(), rect));
        }
        startPickerFragment();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        this.pickerFragment.clearOnSelectionChangedListeners();
        super.onStop();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public final void onCancel(DialogInterface dialogInterface) {
        Iterator<DialogInterface.OnCancelListener> it = this.onCancelListeners.iterator();
        while (it.hasNext()) {
            it.next().onCancel(dialogInterface);
        }
        super.onCancel(dialogInterface);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public final void onDismiss(DialogInterface dialogInterface) {
        Iterator<DialogInterface.OnDismissListener> it = this.onDismissListeners.iterator();
        while (it.hasNext()) {
            it.next().onDismiss(dialogInterface);
        }
        ViewGroup viewGroup = (ViewGroup) getView();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        super.onDismiss(dialogInterface);
    }

    public final S getSelection() {
        return getDateSelector().getSelection();
    }

    private void enableEdgeToEdgeIfNeeded(Window window) {
        if (this.edgeToEdgeEnabled) {
            return;
        }
        final View findViewById = requireView().findViewById(R.id.fullscreen_header);
        EdgeToEdgeUtils.applyEdgeToEdge(window, true, ViewUtils.getBackgroundColor(findViewById), null);
        final int paddingTop = findViewById.getPaddingTop();
        final int i = findViewById.getLayoutParams().height;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById, new OnApplyWindowInsetsListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.3
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                int i2 = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).top;
                if (i >= 0) {
                    findViewById.getLayoutParams().height = i + i2;
                    View view2 = findViewById;
                    view2.setLayoutParams(view2.getLayoutParams());
                }
                View view3 = findViewById;
                view3.setPadding(view3.getPaddingLeft(), paddingTop + i2, findViewById.getPaddingRight(), findViewById.getPaddingBottom());
                return windowInsetsCompat;
            }
        });
        this.edgeToEdgeEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeader() {
        String headerText = getHeaderText();
        this.headerSelectionText.setContentDescription(String.format(getString(R.string.mtrl_picker_announce_current_selection), headerText));
        this.headerSelectionText.setText(headerText);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPickerFragment() {
        PickerFragment<S> pickerFragment;
        int themeResId = getThemeResId(requireContext());
        this.calendar = MaterialCalendar.newInstance(getDateSelector(), themeResId, this.calendarConstraints);
        if (this.headerToggleButton.isChecked()) {
            pickerFragment = MaterialTextInputPicker.newInstance(getDateSelector(), themeResId, this.calendarConstraints);
        } else {
            pickerFragment = this.calendar;
        }
        this.pickerFragment = pickerFragment;
        updateHeader();
        FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.mtrl_calendar_frame, this.pickerFragment);
        beginTransaction.commitNow();
        this.pickerFragment.addOnSelectionChangedListener(new OnSelectionChangedListener<S>() { // from class: com.google.android.material.datepicker.MaterialDatePicker.4
            @Override // com.google.android.material.datepicker.OnSelectionChangedListener
            public void onSelectionChanged(S s) {
                MaterialDatePicker.this.updateHeader();
                MaterialDatePicker.this.confirmButton.setEnabled(MaterialDatePicker.this.getDateSelector().isSelectionComplete());
            }

            @Override // com.google.android.material.datepicker.OnSelectionChangedListener
            public void onIncompleteSelectionChanged() {
                MaterialDatePicker.this.confirmButton.setEnabled(false);
            }
        });
    }

    private void initHeaderToggle(Context context) {
        this.headerToggleButton.setTag(TOGGLE_BUTTON_TAG);
        this.headerToggleButton.setImageDrawable(createHeaderToggleDrawable(context));
        this.headerToggleButton.setChecked(this.inputMode != 0);
        ViewCompat.setAccessibilityDelegate(this.headerToggleButton, null);
        updateToggleContentDescription(this.headerToggleButton);
        this.headerToggleButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MaterialDatePicker.this.confirmButton.setEnabled(MaterialDatePicker.this.getDateSelector().isSelectionComplete());
                MaterialDatePicker.this.headerToggleButton.toggle();
                MaterialDatePicker materialDatePicker = MaterialDatePicker.this;
                materialDatePicker.updateToggleContentDescription(materialDatePicker.headerToggleButton);
                MaterialDatePicker.this.startPickerFragment();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateToggleContentDescription(CheckableImageButton checkableImageButton) {
        String string;
        if (this.headerToggleButton.isChecked()) {
            string = checkableImageButton.getContext().getString(R.string.mtrl_picker_toggle_to_calendar_input_mode);
        } else {
            string = checkableImageButton.getContext().getString(R.string.mtrl_picker_toggle_to_text_input_mode);
        }
        this.headerToggleButton.setContentDescription(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DateSelector<S> getDateSelector() {
        if (this.dateSelector == null) {
            this.dateSelector = (DateSelector) getArguments().getParcelable(DATE_SELECTOR_KEY);
        }
        return this.dateSelector;
    }

    private static Drawable createHeaderToggleDrawable(Context context) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842912}, AppCompatResources.getDrawable(context, R.drawable.material_ic_calendar_black_24dp));
        stateListDrawable.addState(new int[0], AppCompatResources.getDrawable(context, R.drawable.material_ic_edit_black_24dp));
        return stateListDrawable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isFullscreen(Context context) {
        return readMaterialCalendarStyleBoolean(context, 16843277);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isNestedScrollable(Context context) {
        return readMaterialCalendarStyleBoolean(context, R.attr.nestedScrollable);
    }

    static boolean readMaterialCalendarStyleBoolean(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(MaterialAttributes.resolveOrThrow(context, R.attr.materialCalendarStyle, MaterialCalendar.class.getCanonicalName()), new int[]{i});
        boolean z = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        return z;
    }

    private static int getPaddedPickerWidth(Context context) {
        Resources resources = context.getResources();
        int dimensionPixelOffset = resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_content_padding);
        int i = Month.current().daysInWeek;
        return (dimensionPixelOffset * 2) + (resources.getDimensionPixelSize(R.dimen.mtrl_calendar_day_width) * i) + ((i - 1) * resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_month_horizontal_padding));
    }

    public boolean addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<? super S> materialPickerOnPositiveButtonClickListener) {
        return this.onPositiveButtonClickListeners.add(materialPickerOnPositiveButtonClickListener);
    }

    public boolean removeOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<? super S> materialPickerOnPositiveButtonClickListener) {
        return this.onPositiveButtonClickListeners.remove(materialPickerOnPositiveButtonClickListener);
    }

    public void clearOnPositiveButtonClickListeners() {
        this.onPositiveButtonClickListeners.clear();
    }

    public boolean addOnNegativeButtonClickListener(View.OnClickListener onClickListener) {
        return this.onNegativeButtonClickListeners.add(onClickListener);
    }

    public boolean removeOnNegativeButtonClickListener(View.OnClickListener onClickListener) {
        return this.onNegativeButtonClickListeners.remove(onClickListener);
    }

    public void clearOnNegativeButtonClickListeners() {
        this.onNegativeButtonClickListeners.clear();
    }

    public boolean addOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        return this.onCancelListeners.add(onCancelListener);
    }

    public boolean removeOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        return this.onCancelListeners.remove(onCancelListener);
    }

    public void clearOnCancelListeners() {
        this.onCancelListeners.clear();
    }

    public boolean addOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        return this.onDismissListeners.add(onDismissListener);
    }

    public boolean removeOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        return this.onDismissListeners.remove(onDismissListener);
    }

    public void clearOnDismissListeners() {
        this.onDismissListeners.clear();
    }

    /* loaded from: classes.dex */
    public static final class Builder<S> {
        CalendarConstraints calendarConstraints;
        final DateSelector<S> dateSelector;
        int overrideThemeResId = 0;
        int titleTextResId = 0;
        CharSequence titleText = null;
        int positiveButtonTextResId = 0;
        CharSequence positiveButtonText = null;
        int negativeButtonTextResId = 0;
        CharSequence negativeButtonText = null;
        S selection = null;
        int inputMode = 0;

        private Builder(DateSelector<S> dateSelector) {
            this.dateSelector = dateSelector;
        }

        public static <S> Builder<S> customDatePicker(DateSelector<S> dateSelector) {
            return new Builder<>(dateSelector);
        }

        public static Builder<Long> datePicker() {
            return new Builder<>(new SingleDateSelector());
        }

        public static Builder<Pair<Long, Long>> dateRangePicker() {
            return new Builder<>(new RangeDateSelector());
        }

        public Builder<S> setSelection(S s) {
            this.selection = s;
            return this;
        }

        public Builder<S> setTheme(int i) {
            this.overrideThemeResId = i;
            return this;
        }

        public Builder<S> setCalendarConstraints(CalendarConstraints calendarConstraints) {
            this.calendarConstraints = calendarConstraints;
            return this;
        }

        public Builder<S> setTitleText(int i) {
            this.titleTextResId = i;
            this.titleText = null;
            return this;
        }

        public Builder<S> setTitleText(CharSequence charSequence) {
            this.titleText = charSequence;
            this.titleTextResId = 0;
            return this;
        }

        public Builder<S> setPositiveButtonText(int i) {
            this.positiveButtonTextResId = i;
            this.positiveButtonText = null;
            return this;
        }

        public Builder<S> setPositiveButtonText(CharSequence charSequence) {
            this.positiveButtonText = charSequence;
            this.positiveButtonTextResId = 0;
            return this;
        }

        public Builder<S> setNegativeButtonText(int i) {
            this.negativeButtonTextResId = i;
            this.negativeButtonText = null;
            return this;
        }

        public Builder<S> setNegativeButtonText(CharSequence charSequence) {
            this.negativeButtonText = charSequence;
            this.negativeButtonTextResId = 0;
            return this;
        }

        public Builder<S> setInputMode(int i) {
            this.inputMode = i;
            return this;
        }

        public MaterialDatePicker<S> build() {
            if (this.calendarConstraints == null) {
                this.calendarConstraints = new CalendarConstraints.Builder().build();
            }
            if (this.titleTextResId == 0) {
                this.titleTextResId = this.dateSelector.getDefaultTitleResId();
            }
            S s = this.selection;
            if (s != null) {
                this.dateSelector.setSelection(s);
            }
            if (this.calendarConstraints.getOpenAt() == null) {
                this.calendarConstraints.setOpenAt(createDefaultOpenAt());
            }
            return MaterialDatePicker.newInstance(this);
        }

        private Month createDefaultOpenAt() {
            if (!this.dateSelector.getSelectedDays().isEmpty()) {
                Month create = Month.create(this.dateSelector.getSelectedDays().iterator().next().longValue());
                if (monthInValidRange(create, this.calendarConstraints)) {
                    return create;
                }
            }
            Month current = Month.current();
            return monthInValidRange(current, this.calendarConstraints) ? current : this.calendarConstraints.getStart();
        }

        private static boolean monthInValidRange(Month month, CalendarConstraints calendarConstraints) {
            return month.compareTo(calendarConstraints.getStart()) >= 0 && month.compareTo(calendarConstraints.getEnd()) <= 0;
        }
    }
}
