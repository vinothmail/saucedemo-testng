package com.learning.tests;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * End-to-end flow for https://www.saucedemo.com/
 *
 * Flow: invalid login -> valid login -> add to cart -> verify cart
 *       -> checkout -> order confirmation -> logout
 *
 * Concepts shown: @BeforeClass / @AfterClass, @Test with priority and
 * dependsOnMethods, explicit waits, Assert, and common locator strategies.
 */
public class SauceDemoE2ETest {

    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "secret_sauce";

    private WebDriver driver;
    private WebDriverWait wait;

    // ---------------------------------------------------------------
    // Setup & Teardown
    // ---------------------------------------------------------------

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Stops Chrome's "Change your password" popup from blocking the page
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        options.addArguments("--incognito");
        // options.addArguments("--headless=new"); // uncomment to run without a browser window

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ---------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------

    private void login(String user, String pass) {
        WebElement userField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        userField.clear();
        userField.sendKeys(user);

        WebElement passField = driver.findElement(By.id("password"));
        passField.clear();
        passField.sendKeys(pass);

        driver.findElement(By.id("login-button")).click();
    }

    private String pageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='title']"))).getText();
    }

    // ---------------------------------------------------------------
    // Tests (run in priority order; later ones depend on earlier ones)
    // ---------------------------------------------------------------

    @Test(priority = 1, description = "Login with wrong password shows an error")
    public void invalidLoginShowsError() {
        login(USERNAME, "wrong_password");

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='error']")));

        Assert.assertTrue(error.getText().contains("Username and password do not match"),
                "Unexpected error message: " + error.getText());
    }

    @Test(priority = 2, dependsOnMethods = "invalidLoginShowsError",
          description = "Login with valid credentials lands on the Products page")
    public void validLogin() {
        login(USERNAME, PASSWORD);

        wait.until(ExpectedConditions.urlContains("inventory.html"));
        Assert.assertEquals(pageTitle(), "Products");

        List<WebElement> items = driver.findElements(By.className("inventory_item"));
        Assert.assertEquals(items.size(), 6, "Expected 6 products on the page");
    }

    @Test(priority = 3, dependsOnMethods = "validLogin",
          description = "Add two products and check the cart badge")
    public void addProductsToCart() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        WebElement badge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(badge.getText(), "2", "Cart badge should show 2 items");

        // Button text flips from "Add to cart" to "Remove"
        String buttonText = driver.findElement(By.id("remove-sauce-labs-backpack")).getText();
        Assert.assertEquals(buttonText, "Remove");
    }

    @Test(priority = 4, dependsOnMethods = "addProductsToCart",
          description = "Cart page lists exactly the products we added")
    public void verifyCartContents() {
        driver.findElement(By.className("shopping_cart_link")).click();
        wait.until(ExpectedConditions.urlContains("cart.html"));
        Assert.assertEquals(pageTitle(), "Your Cart");

        List<WebElement> names = driver.findElements(By.className("inventory_item_name"));
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(names.get(0).getText(), "Sauce Labs Backpack");
        Assert.assertEquals(names.get(1).getText(), "Sauce Labs Bike Light");
    }

    @Test(priority = 5, dependsOnMethods = "verifyCartContents",
          description = "Fill in checkout details and review the order")
    public void checkoutAndReviewOrder() {
        driver.findElement(By.id("checkout")).click();
        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        Assert.assertEquals(pageTitle(), "Checkout: Your Information");

        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("postal-code")).sendKeys("600001");
        driver.findElement(By.id("continue")).click();

        wait.until(ExpectedConditions.urlContains("checkout-step-two.html"));
        Assert.assertEquals(pageTitle(), "Checkout: Overview");

        // Backpack $29.99 + Bike Light $9.99 = $39.98 item total
        String itemTotal = driver.findElement(By.className("summary_subtotal_label")).getText();
        Assert.assertTrue(itemTotal.contains("$39.98"), "Item total mismatch: " + itemTotal);
    }

    @Test(priority = 6, dependsOnMethods = "checkoutAndReviewOrder",
          description = "Finish the order and see the confirmation")
    public void completeOrder() {
        driver.findElement(By.id("finish")).click();

        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));
        Assert.assertEquals(pageTitle(), "Checkout: Complete!");

        String confirmation = driver.findElement(By.className("complete-header")).getText();
        Assert.assertEquals(confirmation, "Thank you for your order!");
    }

    @Test(priority = 7, dependsOnMethods = "completeOrder",
          description = "Logout returns to the login page")
    public void logout() {
        driver.findElement(By.id("react-burger-menu-btn")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL);
    }
}
