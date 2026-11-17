package com.learning.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.learning.pages.CartPage;
import com.learning.pages.CheckoutPage;
import com.learning.pages.InventoryPage;

/** Tests for the Cart page. */
public class CartTest extends BaseTest {

    private static final String BACKPACK   = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    private CartPage cartPage;

    @BeforeMethod
    public void goToCartWithTwoProducts() {
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addToCart(BACKPACK).addToCart(BIKE_LIGHT);
        cartPage = inventoryPage.openCart();
    }

    @Test
    public void cartShowsAddedProducts() {
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart");
        Assert.assertEquals(cartPage.getItemNames(), List.of(BACKPACK, BIKE_LIGHT));
    }

    @Test
    public void checkoutButtonOpensInformationPage() {
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        Assert.assertEquals(checkoutPage.getPageTitle(), "Checkout: Your Information");
    }
}
