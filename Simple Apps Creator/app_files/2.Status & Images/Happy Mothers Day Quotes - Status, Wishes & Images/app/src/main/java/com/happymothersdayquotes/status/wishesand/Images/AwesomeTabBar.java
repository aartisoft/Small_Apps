package com.happymothersdayquotes.status.wishesand.Images;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;


public class AwesomeTabBar extends HorizontalScrollView {
    private Context context;
    private int tabWidth;
    private int tabHeight;
    private int indicatorColor;
    private int indicatorHeight;
    private int selectedTabBackgroundColor;
    private int selectedTabTextColor;
    private int selectedTabImageColor;
    private int indicatorLineColor;
    private boolean noImage;
    private boolean noText;
    private int imagePosition;
    private int imageMargin;
    private int imageWidth;
    private int imageHeight;
    private int textMargin;
    private float textSize;
    private String fontpath;

    private LinearLayout llTabs;
    private View indicator;

    public AwesomeTabBar(Context context) {
        super(context);
        init(context, null);
    }

    public AwesomeTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.context = context;
        if (attributeSet != null)
            initAttributes(attributeSet);
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AwesomeTabBar, 0, 0);
        try {
            int minimumTabWidth = context.getResources().getDimensionPixelSize(R.dimen.minimumTabWidth);
            int minimumTabHeight = context.getResources().getDimensionPixelSize(R.dimen.minimumTabHeight);
            tabWidth = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_tabWidth, minimumTabWidth);
            tabHeight = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_tabHeight, minimumTabHeight);
            int defaultIndicatorColor = ContextCompat.getColor(context, R.color.defaultIndicatorColor);
            indicatorColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_indicatorColor, defaultIndicatorColor);
            int minimumIndicatorHeight = context.getResources().getDimensionPixelSize(R.dimen.minimumIndicatorHeight);
            indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_indicatorHeight, minimumIndicatorHeight);
            selectedTabBackgroundColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_selectedTabBackgroundColor, 0);
            selectedTabTextColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_selectedTabTextColor, 0);
            selectedTabImageColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_selectedTabImageColor, 0);
            indicatorLineColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_indicatorLineColor, 0);
            noImage = typedArray.getBoolean(R.styleable.AwesomeTabBar_atb_noImage, false);
            noText = typedArray.getBoolean(R.styleable.AwesomeTabBar_atb_noText, false);
            imagePosition = typedArray.getInt(R.styleable.AwesomeTabBar_atb_imagePosition, 0);
            int defaultImageMargin = context.getResources().getDimensionPixelSize(R.dimen.tab_image_margin);
            imageMargin = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_imageMargin, defaultImageMargin);
            int defaultImageSize = context.getResources().getDimensionPixelSize(R.dimen.tab_image_size);
            imageWidth = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_imageWidth, defaultImageSize);
            imageHeight = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_imageHeight, defaultImageSize);
            int defaultTextMargin = context.getResources().getDimensionPixelSize(R.dimen.tab_image_margin);
            textMargin = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_textMargin, defaultTextMargin);
            int defaultTextSize = context.getResources().getDimensionPixelSize(R.dimen.tab_text_size);
            textSize = typedArray.getDimension(R.styleable.AwesomeTabBar_atb_textSize, defaultTextSize);
            fontpath = typedArray.getString(R.styleable.AwesomeTabBar_atb_fontPath);
        } finally {
            typedArray.recycle();
        }
    }

    public void setupWithViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            throw new RuntimeException("Your ViewPager is null");
        } else if (viewPager.getAdapter() == null) {
            throw new RuntimeException("Your ViewPager has no adapter");
        } else if (!(viewPager.getAdapter() instanceof AwesomeTabBarAdapter)) {
            throw new RuntimeException("Your ViewPager's adapter must extend SimpleTabBarAdapter");
        } else {
            setHorizontalScrollBarEnabled(false);
            addView(prepareContainerWithTabsAndIndicator(viewPager));
            scrollWithViewPager(viewPager);
        }
    }

    private LinearLayout prepareContainerWithTabsAndIndicator(ViewPager viewPager) {
        llTabs = new LinearLayout(context);
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llTabs.setOrientation(LinearLayout.HORIZONTAL);
        llTabs.setLayoutParams(tabParams);
        addTabs(viewPager);

        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.setLayoutParams(parentParams);
        parent.setOrientation(LinearLayout.VERTICAL);

        parent.addView(llTabs);
        parent.addView(getFieldWithIndicator());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                changeMaxTabWidth();
                fitTabs(tabWidth);
                invalidate();
            }
        });

        return parent;
    }

    private FrameLayout getFieldWithIndicator() {
        FrameLayout indicatorField = new FrameLayout(context);
        if (indicatorLineColor != 0)
            indicatorField.setBackgroundColor(indicatorLineColor);
        indicator = new View(context);
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(tabWidth, indicatorHeight);
        indicator.setBackgroundColor(indicatorColor);
        indicatorField.addView(indicator, indicatorParams);
        return indicatorField;
    }

    private void addTabs(final ViewPager viewPager) {
        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            addTab(viewPager, i);
        }
    }

    private void addTab(final ViewPager viewPager, final int position) {
        View view = newTab(viewPager, position);
        view.setMinimumWidth(tabWidth);
        view.setMinimumHeight(tabHeight);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(position);
            }
        });
        llTabs.addView(view);
    }

    private View newTab(ViewPager viewPager, int position) {
        return getTabViewWithIcon(((AwesomeTabBarAdapter) viewPager.getAdapter()), position);
    }

    private View getTabViewWithIcon(AwesomeTabBarAdapter adapter, int position) {
        LinearLayout tabView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_tab, null);
        TextView tv = (TextView) tabView.findViewById(R.id.tvText);
        ImageView iv = (ImageView) tabView.findViewById(R.id.ivImage);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if (fontpath != null) {
            tv.setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontpath));
        }

        setMargins(tv, textMargin, textMargin, textMargin, textMargin);
        setMargins(iv, imageMargin, imageMargin, imageMargin, imageMargin);
        setSizes(iv, imageWidth, imageHeight);
        if (imagePosition == 1) {
            tabView.setOrientation(LinearLayout.VERTICAL);
        } else if (imagePosition == 2) {
            invertTextAndImagePositions(tabView, iv, tv);
        } else if (imagePosition == 3) {
            tabView.setOrientation(LinearLayout.VERTICAL);
            invertTextAndImagePositions(tabView, iv, tv);
        }
        tv.setVisibility(noText ? GONE : VISIBLE);
        iv.setVisibility(noImage ? GONE : VISIBLE);
        tv.setText(adapter.getPageTitle(position));
        iv.setImageResource(adapter.getIconResource(position));
        tabView.setBackgroundResource(adapter.getColorResource(position));
        return tabView;
    }

    private void invertTextAndImagePositions(LinearLayout tabView, ImageView iv, TextView tv) {
        tabView.removeView(tv);
        tabView.removeView(iv);
        tabView.addView(tv);
        tabView.addView(iv);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) view.getLayoutParams();
        p.setMargins(left, top, right, bottom);
        view.setLayoutParams(p);
        view.requestLayout();
    }

    private void setSizes(View view, int width, int height) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (width != 0)
            p.width = width;
        if (height != 0)
            p.height = height;
        view.setLayoutParams(p);
        view.requestLayout();
    }

    private void scrollWithViewPager(final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.animate()
                        .x((position + positionOffset) * tabWidth)
                        .setDuration(0)
                        .start();
                scrollTo((int) ((position + positionOffset - 1) * tabWidth), 0);
            }

            @Override
            public void onPageSelected(int position) {
                selectTab(position, viewPager);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void selectTab(int position, ViewPager viewPager) {
        for (int i = 0; i < llTabs.getChildCount(); i++) {
            View view = llTabs.getChildAt(i);
            TextView tv = (TextView) view.findViewById(R.id.tvText);
            ImageView iv = (ImageView) view.findViewById(R.id.ivImage);
            if (i == position) {
                if (selectedTabBackgroundColor != 0)
                    view.setBackgroundColor(selectedTabBackgroundColor);
                if (selectedTabTextColor != 0)
                    tv.setTextColor(selectedTabTextColor);
                if (selectedTabImageColor != 0)
                    iv.setColorFilter(new PorterDuffColorFilter(selectedTabImageColor, PorterDuff.Mode.MULTIPLY));
            } else {
                int backgroundColorRes = ((AwesomeTabBarAdapter) viewPager.getAdapter()).getColorResource(i);
                int backgroundColor = ContextCompat.getColor(context, backgroundColorRes);
                view.setBackgroundColor(backgroundColor);
                int textColorRes = ((AwesomeTabBarAdapter) viewPager.getAdapter()).getTextColorResource(i);
                int textColor = ContextCompat.getColor(context, textColorRes);
                tv.setTextColor(textColor);
                iv.setColorFilter(null);
            }
        }
    }

    private void changeMaxTabWidth() {
        int screenWidth = getScreenWidth();
        int maxWidth = 0;
        for (int i = 0; i < llTabs.getChildCount(); i++) {
            View tab = llTabs.getChildAt(i);
            if (maxWidth < tab.getWidth())
                maxWidth = tab.getWidth();
        }
        if (llTabs.getChildCount() > 0) {
            View lastTab = llTabs.getChildAt(llTabs.getChildCount() - 1);
            if (lastTab.getRight() < screenWidth) {
                maxWidth = screenWidth / llTabs.getChildCount();
            }
        }
        tabWidth = maxWidth;
    }

    private void fitTabs(int maxWidth) {
        for (int i = 0; i < llTabs.getChildCount(); i++) {
            View tab = llTabs.getChildAt(i);
            tab.setMinimumWidth(maxWidth);
        }
        LayoutParams indicatorParams = new LayoutParams(maxWidth, indicatorHeight);
        indicator.setLayoutParams(indicatorParams);
        indicator.invalidate();
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
