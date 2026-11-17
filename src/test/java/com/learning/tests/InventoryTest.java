package com.learning.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.learning.pages.InventoryPage;
import com.learning.pages.LoginPage;

/** Tests for the Products (inventory) page. */
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void goToInventory() {
        inventoryPage = loginAsStandardUser();
    }

    @Test
    public void productsPageShowsSixProducts() {
        Assert.assertEquals(inventoryPage.getPageTitle(), "Products");
        Assert.assertEquals(inventoryPage.getProductCount(), 6);
    }

    @Test
    public void addingOneProductUpdatesCartBadge() {
        inventoryPage.addToCart("Sauce Labs Backpack");

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1");
        Assert.assertEquals(inventoryPage.getRemoveButtonText("Sauce Labs Backpack"), "Remove");
    }

    @Test
    public void addingTwoProductsUpdatesCartBadge() {
        inventoryPage.addToCart("Sauce Labs Backpack").addToCart("Sauce Labs Bike Light");

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "2");
    }

    @Test
    public void logoutReturnsToLoginPage() {
        LoginPage loginPage = inventoryPage.logout();

        Assert.assertTrue(loginPage.isLoginButtonDisplayed());
        Assert.assertEquals(loginPage.getCurrentUrl(), "https://www.saucedemo.com/");
    }
}
