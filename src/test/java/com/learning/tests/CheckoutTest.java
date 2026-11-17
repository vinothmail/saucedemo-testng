package com.learning.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.learning.pages.CheckoutPage;
import com.learning.pages.InventoryPage;

/** Tests for the three checkout screens. */
public class CheckoutTest extends BaseTest {

    private CheckoutPage checkoutPage;

    @BeforeMethod
    public void goToCheckoutWithTwoProducts() {
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addToCart("Sauce Labs Backpack").addToCart("Sauce Labs Bike Light");
        checkoutPage = inventoryPage.openCart().proceedToCheckout();
    }

    @Test
    public void missingFirstNameShowsError() {
        checkoutPage.continueToOverview();

        Assert.assertTrue(checkoutPage.getErrorMessage().contains("First Name is required"),
                "Actual error: " + checkoutPage.getErrorMessage());
    }

    @Test
    public void overviewShowsCorrectItemTotal() {
        checkoutPage.fillInformation("John", "Doe", "600001").continueToOverview();

        Assert.assertEquals(checkoutPage.getPageTitle(), "Checkout: Overview");
        // Backpack $29.99 + Bike Light $9.99
        Assert.assertTrue(checkoutPage.getItemTotal().contains("$39.98"),
                "Actual total: " + checkoutPage.getItemTotal());
    }

    @Test
    public void completingOrderShowsConfirmation() {
        checkoutPage.fillInformation("John", "Doe", "600001")
                    .continueToOverview()
                    .finishOrder();

        Assert.assertEquals(checkoutPage.getPageTitle(), "Checkout: Complete!");
        Assert.assertEquals(checkoutPage.getConfirmationMessage(), "Thank you for your order!");
    }
}
