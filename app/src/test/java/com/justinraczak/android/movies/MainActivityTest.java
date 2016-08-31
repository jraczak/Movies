package com.justinraczak.android.movies;

/**
 * Created by justinr on 8/26/16.
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
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

public class MainActivityTest {

    private static final int SHORT_TIMEOUT = 5;

    private static EnhancedAndroidDriver driver;

    @Rule
    public TestWatcher testWatcher = Factory.createWatcher();

    @Before
    public void setup() throws Exception {
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
    public void clickFirstMovie() {
        driver.label("Tap first movie test");
        MobileElement firstMoviePoster = (MobileElement) driver.findElement
                (By.xpath("//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.GridView[1]/android.widget.ImageView[1]"));
        firstMoviePoster.click();
        driver.getScreenshotAs(OutputType.FILE);
        MobileElement movieTitleTextField = (MobileElement) driver.findElementById("com.justinraczak.android.movies:id/movie_title");
        assertEquals("Suicide Squad", movieTitleTextField.getText());
    }

    @After
    public void cleanup(){ driver.quit(); }

}
