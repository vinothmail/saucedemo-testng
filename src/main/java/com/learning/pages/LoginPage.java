package com.learning.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final String URL = "https://www.saucedemo.com/";

    // Locators live here, and only here
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.id("login-button");
    private final By errorMessage  = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(URL);
        return this;
    }

    /** Successful login: returns the page the user lands on. */
    public InventoryPage loginAs(String username, String password) {
        enterCredentialsAndSubmit(username, password);
        return new InventoryPage(driver);
    }

    /** Failed login: we stay on the login page, so return this page. */
    public LoginPage loginExpectingError(String username, String password) {
        enterCredentialsAndSubmit(username, password);
        return this;
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isLoginButtonDisplayed() {
        return waitForVisible(loginButton).isDisplayed();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    private void enterCredentialsAndSubmit(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
    }
}
