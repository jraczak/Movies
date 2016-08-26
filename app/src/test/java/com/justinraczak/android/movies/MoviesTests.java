package com.justinraczak.android.movies;

import com.xamarin.testcloud.appium.EnhancedAndroidDriver;
import com.xamarin.testcloud.appium.Factory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by johnl on 8/25/16.
 */
public class MoviesTests {

    private static final int SHORT_TIMEOUT = 2;

    private static EnhancedAndroidDriver driver;

    @Rule
    public TestWatcher testWatcher = Factory.createWatcher();

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("appium-version", "1.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.justinraczak.android.movies");
        capabilities.setCapability("appActivity", ".MainActivity");
        capabilities.setCapability("automationName", "Appium");
        capabilities.setCapability("app", "/Users/johnl/Developer/Movies/app/build/outputs/apk/app-debug.apk");
        capabilities.setCapability("deviceName","Nexus_5X_API_23");
        URL host = null;
        try {
            host = new URL("http://127.0.0.1:4723/wd/hub");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        driver =  Factory.createAndroidDriver(host, capabilities);

        driver.manage().timeouts().implicitlyWait(SHORT_TIMEOUT, TimeUnit.SECONDS);

    }

    @Test
    public void screenShotTest(){

        driver.label("Successful Test Capture");
    }

    @After
    public void cleanUp(){
        driver.quit();
    }
}
