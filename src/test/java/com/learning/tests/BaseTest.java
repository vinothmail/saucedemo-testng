package com.learning.tests;

import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.learning.pages.InventoryPage;
import com.learning.pages.LoginPage;

/** Every test class extends this to get a ready-to-use browser. */
public class BaseTest {

    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        options.addArguments("--incognito");

        // CI servers have no screen: run with  mvn test -Dheadless=true
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new", "--window-size=1920,1080",
                    "--no-sandbox", "--disable-dev-shm-usage");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    /** Clears cookies AND localStorage so the cart never leaks between tests. */
    @BeforeMethod
    public void resetBrowserState() {
        driver.get("https://www.saucedemo.com/");
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver)
                .executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
    }

    /** Opens the site and logs in as the standard user. */
    protected InventoryPage loginAsStandardUser() {
        return new LoginPage(driver).open().loginAs("standard_user", "secret_sauce");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
