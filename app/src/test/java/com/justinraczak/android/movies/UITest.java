package com.justinraczak.android.movies;

/**
 * Created by justinr on 8/25/16.
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.xamarin.testcloud.appium.Factory;
import com.xamarin.testcloud.appium.EnhancedAndroidDriver;
import org.junit.rules.TestWatcher;
import org.junit.Rule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.remote.http.HttpClient;

import static org.junit.Assert.*;

public class UITest {

    private static final int SHORT_TIMEOUT = 5;

    private static EnhancedAndroidDriver driver;

    @Rule
    public TestWatcher testWatcher = Factory.createWatcher();

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("appium-version", "1.5.3");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.justinraczak.android.movies");
        capabilities.setCapability("appActivity", ".MainActivity");
        capabilities.setCapability("automationName", "Appium");
        capabilities.setCapability("app", "/Users/justinr/AndroidStudioProjects/Movies/app/app-release.apk");
        capabilities.setCapability("deviceName", "Nexus5X");
        URL host = null;
        try {
            host = new URL("http://127.0.0.1:4723/wd/hub");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        driver = Factory.createAndroidDriver(host, capabilities);

        driver.manage().timeouts().implicitlyWait(SHORT_TIMEOUT, TimeUnit.SECONDS);
    }

    @Test
    public void screenshotTest() {
        driver.label("Successful test screenshot");
    }

    @After
    public void cleanup() {
        driver.quit();
    }

}
