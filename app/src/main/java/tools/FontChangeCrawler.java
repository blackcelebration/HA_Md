package tools;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Eli on 15/12/2016.
 */
public class FontChangeCrawler {
    private Typeface typeface;
    public static Typeface regularFont;
    public static Typeface boldFont;
    public FontChangeCrawler(Typeface typeface)
    {
        this.typeface = typeface;
    }

    public FontChangeCrawler(AssetManager assets, String assetsFontFileName)
    {
        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
        regularFont = Typeface.createFromAsset(assets, "fonts/assistantregular.ttf");
        boldFont = Typeface.createFromAsset(assets, "fonts/assistantsemibold.ttf");

        //typeface = Typeface.createFromFile("c:\\Users\\Eli\\StudioProjects\\HA_Md\\app\\src\\main\\assets\\fonts\\assistantregular.ttf");
    }

    public void replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child);
            }
            else if(child instanceof TextView)
            {
                // base case
                ((TextView) child).setTypeface(typeface);
            }
        }
    }

}
