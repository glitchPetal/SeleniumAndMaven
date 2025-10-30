package com.example;

import java.io.File;
import java.net.MalformedURLException;
import java.time.Duration;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SumWebTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String htmlUrl;

    /**
     * This @Before method runs before *each* @Test.
     * We set up the driver, the wait, and the URL here to avoid duplicating
     * code in every test.
     */
    @Before
    public void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Headless mode for Jenkins
        options.addArguments("--allow-file-access-from-files");
        options.addArguments("--disable-gpu"); // Common for headless
        options.addArguments("--window-size=1920,1080"); // Define window size
        driver = new ChromeDriver(options);

        // Initialize the explicit wait. We will reuse this.
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Get the portable URL.
        htmlUrl = getHtmlUrl();
    }

    /**
     * Helper method to get the correct, portable path to the HTML file.
     */
    private String getHtmlUrl() throws MalformedURLException {
        File file = new File("src/test/resources/sum.html");
        return file.toURI().toURL().toString();
    }

    // --- TEST CASE 1: Positive Numbers ---
    @Test
    public void testSumPositive() {
        driver.get(htmlUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("num1")));

        driver.findElement(By.id("num1")).sendKeys("10");
        driver.findElement(By.id("num2")).sendKeys("20");
        driver.findElement(By.id("calcBtn")).click();

        // **THIS IS THE FIX YOU MISSED**
        // We replace Thread.sleep() with an explicit wait for the *expected text*.
        // The test will wait *until* the text is "Sum = 30" or 5 seconds pass.
        wait.until(ExpectedConditions.textToBe(By.id("result"), "Sum = 30"));

        String output = driver.findElement(By.id("result")).getText().trim();
        assertEquals("Sum = 30", output);
    }

    // --- TEST CASE 2: Negative Numbers ---
    @Test
    public void testSumWithNegatives() {
        driver.get(htmlUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("num1")));

        driver.findElement(By.id("num1")).sendKeys("-15");
        driver.findElement(By.id("num2")).sendKeys("5");
        driver.findElement(By.id("calcBtn")).click();

        // Wait for the new expected result
        wait.until(ExpectedConditions.textToBe(By.id("result"), "Sum = -10"));

        String output = driver.findElement(By.id("result")).getText().trim();
        assertEquals("Sum = -10", output);
    }

    // --- TEST CASE 3: Invalid Input (Non-numeric) ---
    @Test
    public void testSumWithInvalidInput() {
        driver.get(htmlUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("num1")));

        driver.findElement(By.id("num1")).sendKeys("abc"); // Invalid
        driver.findElement(By.id("num2")).sendKeys("10"); // Valid

        // Your JavaScript (|| 0) handles "abc" by turning it into 0.
        // So the result should be 0 + 10 = 10.
        driver.findElement(By.id("calcBtn")).click();

        // Wait for the expected result of this scenario
        wait.until(ExpectedConditions.textToBe(By.id("result"), "Sum = 10"));

        String output = driver.findElement(By.id("result")).getText().trim();
        assertEquals("Sum = 10", output);
    }

    /**
     * This @After method runs after *each* @Test.
     * It ensures the browser is closed even if a test fails.
     */
    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}