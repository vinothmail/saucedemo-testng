package com.learning.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    private final By inventoryItems = By.className("inventory_item");
    private final By cartBadge      = By.className("shopping_cart_badge");
    private final By cartLink       = By.className("shopping_cart_link");
    private final By menuButton     = By.id("react-burger-menu-btn");
    private final By logoutLink     = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public int getProductCount() {
        waitForVisible(inventoryItems);
        return driver.findElements(inventoryItems).size();
    }

    /** Example: addToCart("Sauce Labs Backpack") clicks id "add-to-cart-sauce-labs-backpack". */
    public InventoryPage addToCart(String productName) {
        click(By.id("add-to-cart-" + toSlug(productName)));
        return this;
    }

    public String getRemoveButtonText(String productName) {
        return getText(By.id("remove-" + toSlug(productName)));
    }

    public String getCartBadgeCount() {
        return getText(cartBadge);
    }

    public CartPage openCart() {
        click(cartLink);
        return new CartPage(driver);
    }

    public LoginPage logout() {
        click(menuButton);
        click(logoutLink);
        return new LoginPage(driver);
    }

    // "Sauce Labs Bike Light" -> "sauce-labs-bike-light"
    private String toSlug(String productName) {
        return productName.toLowerCase().replace(" ", "-");
    }
}
