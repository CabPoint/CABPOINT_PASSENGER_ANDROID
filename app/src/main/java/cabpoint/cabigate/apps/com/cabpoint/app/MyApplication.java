package cabpoint.cabigate.apps.com.cabpoint.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class MyApplication extends Application {
    public static Context context;
    public static final String CABPOINT_BASE_URL="http://paxapi.cabigate.com/index.php";
    public static final String SIGN_UP=CABPOINT_BASE_URL+"/signup";
    @Override
    public void onCreate() {
        super.onCreate();
        initializeFonts();
        context=getApplicationContext();
    }
    private void initializeFonts() {
        Fonts.SanFranciscoTextLightFont = Typeface.createFromAsset(getAssets(), "san_francisco_text_light.ttf");
        Fonts.SanFranciscoTextRegularFont = Typeface.createFromAsset(getAssets(), "san_francisco_text_regular.ttf");
        Fonts.SanFranciscoDisplaySemiBold = Typeface.createFromAsset(getAssets(), "san_francisco_display_semi_bold.ttf");
    }
    public static final class Fonts {

        public static Typeface SanFranciscoTextLightFont;
        public static Typeface SanFranciscoTextRegularFont;
        public static Typeface SanFranciscoDisplaySemiBold;
    }

}
