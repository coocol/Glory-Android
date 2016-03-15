package cc.coocol.jinxiujob.application;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.models.User;

/**
 * Created by coocol on 2016/3/9.
 */

//@ReportsCrashes(formUri = "http://yourserver.com/yourscript",
//        mode = ReportingInteractionMode.DIALOG,
//        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
//        resDialogText = R.string.crash_dialog_text,
//        resDialogIcon = android.R.drawable.ic_dialog_info//optional. default is a warning sign
//)

public class GloryApplication extends com.activeandroid.app.Application {//com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//
//        // The following line triggers the initialization of ACRA
//        ACRA.init(this);
//    }
}
