package com.learning.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.learning.pages.InventoryPage;
import com.learning.pages.LoginPage;

/**
 * Login feature tests only. Each test is independent: before every test we
 * clear cookies and open a fresh login page, so test order doesn't matter.
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void openLoginPage() {
        driver.manage().deleteAllCookies();
        loginPage = new LoginPage(driver).open();
    }

    @Test(description = "Valid user lands on the Products page")
    public void validLogin() {
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("inventory.html"));
    }

    @Test(dataProvider = "invalidCredentials",
          description = "Bad credentials show the right error message")
    public void invalidLogin(String username, String password, String expectedError) {
        loginPage.loginExpectingError(username, password);

        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedError),
                "Actual error: " + loginPage.getErrorMessage());
    }

    // One test method, many data rows: each row runs as a separate test
    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            { "standard_user",   "wrong_password", "Username and password do not match" },
            { "unknown_user",    "secret_sauce",   "Username and password do not match" },
            { "locked_out_user", "secret_sauce",   "this user has been locked out" },
            { "",                "secret_sauce",   "Username is required" },
            { "standard_user",   "",               "Password is required" }
        };
    }
}