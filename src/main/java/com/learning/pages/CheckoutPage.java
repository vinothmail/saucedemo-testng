package com.learning.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Covers the three checkout screens: information, overview, complete. */
public class CheckoutPage extends BasePage {

    // Step 1: your information
    private final By firstName  = By.id("first-name");
    private final By lastName   = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueBtn = By.id("continue");

    // Step 2: overview
    private final By itemTotal = By.className("summary_subtotal_label");
    private final By finishBtn = By.id("finish");

    // Step 3: complete
    private final By confirmationHeader = By.className("complete-header");

    private final By errorMessage = By.cssSelector("[data-test='error']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public CheckoutPage fillInformation(String first, String last, String zip) {
        type(firstName, first);
        type(lastName, last);
        type(postalCode, zip);
        return this;
    }

    public CheckoutPage continueToOverview() {
        click(continueBtn);
        return this;
    }

    public String getItemTotal() {
        return getText(itemTotal);
    }

    public CheckoutPage finishOrder() {
        click(finishBtn);
        return this;
    }

    public String getConfirmationMessage() {
        return getText(confirmationHeader);
    }
}
