package com.jacknic.glut;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.TypedValue;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.jacknic.glut", appContext.getPackageName());
        appContext.setTheme(R.style.AppTheme_cyan);
        TypedValue typedValue = new TypedValue();
        appContext.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        System.out.println("元颜色" + appContext.getResources().getColor(R.color.cyan));
        System.out.println("获取到的" + typedValue.data);
    }
}
