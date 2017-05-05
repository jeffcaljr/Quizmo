package com.example.jeff.viewpagerdelete.Miscellaneous;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.example.jeff.viewpagerdelete.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jeff on 5/4/17.
 */

/**
 * Serves to loop through a set of color theme sets, for providing visual differentiation among list items
 */

public class ColorAlternatorUtil {

    public class ColorSet {
        private int mForegroundColor;
        private int mBackGroundColor;

        public ColorSet(int mForegroundColor, int mBackGroundColor) {
            this.mForegroundColor = mForegroundColor;
            this.mBackGroundColor = mBackGroundColor;
        }

        public int getForegroundColor() {
            return mForegroundColor;
        }

        public int getBackGroundColor() {
            return mBackGroundColor;
        }
    }

    private ArrayList<ColorSet> mDefaultColorSets;
    private Iterator<ColorSet> mDefaultColorSetsIterator;

    private ArrayList<ColorSet> mCustomColorSets;
    private Iterator<ColorSet> mCustomColorSetsIterator;

    private Context mContext;

    public ColorAlternatorUtil(Context context) {
        this.mContext = context;

        int darkPrimaryColor = ContextCompat.getColor(context, R.color.jccolorPrimaryDark);
        int accentColor = ContextCompat.getColor(context, R.color.jccolorAccentBright);
        int amberColor = ContextCompat.getColor(context, R.color.jccolorAmber);
        int whiteColor = ContextCompat.getColor(context, R.color.colorWhite);
        int darkGrayColor = ContextCompat.getColor(context, R.color.primary_text);
//        int purpleColor = ContextCompat.getColor(context, R.color.jccolorPurple);
//        int greenColor = ContextCompat.getColor(context, R.color.jccolorGreen);

        this.mDefaultColorSets = new ArrayList<>();

        ColorSet pinkWhite = new ColorSet(whiteColor, accentColor);
        ColorSet blueWhite = new ColorSet(whiteColor, darkPrimaryColor);
        ColorSet amberGray = new ColorSet(darkGrayColor, amberColor);
//        ColorSet greenWhite = new ColorSet(whiteColor, greenColor);
//        ColorSet purpleWhite = new ColorSet(whiteColor, purpleColor);

        this.mDefaultColorSets.add(blueWhite);
        this.mDefaultColorSets.add(pinkWhite);
        this.mDefaultColorSets.add(amberGray);
//        this.mDefaultColorSets.add(purpleWhite);
//        this.mDefaultColorSets.add(greenWhite);

        mDefaultColorSetsIterator = this.getDefaultColorSets().iterator();
    }

    public void defineColorSets(ArrayList<ColorSet> colorSets) {
        this.mCustomColorSets = colorSets;
        this.mCustomColorSetsIterator = this.mCustomColorSets.iterator();
    }

    public ArrayList<ColorSet> getCustomColorSets() {
        return this.mCustomColorSets;
    }

    public ArrayList<ColorSet> getDefaultColorSets() {
        return this.mDefaultColorSets;
    }

    public void appendColorSet(ColorSet colorSet) {
        this.mCustomColorSets.add(colorSet);
    }

    public ColorSet getNextDefaultColorSet() {

        if (this.mDefaultColorSetsIterator.hasNext()) {
            return this.mDefaultColorSetsIterator.next();
        } else {
            this.mDefaultColorSetsIterator = this.mDefaultColorSets.iterator();
            return this.mDefaultColorSetsIterator.next();
        }
    }

    public ColorSet getNextCustomColorSet() {
        if (this.mCustomColorSetsIterator.hasNext()) {
            return this.mCustomColorSetsIterator.next();
        } else {
            this.mCustomColorSetsIterator = this.mCustomColorSets.iterator();
            return this.mCustomColorSetsIterator.next();
        }
    }


}
