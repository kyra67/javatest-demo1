package com.xxx.javatest_demo1.ui;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTest {
	
	private WebDriver driver;
	
	@BeforeMethod
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
	}
	
	@Test(priority = 1, description = "打开腾讯公益页验证欢迎语")
	public void testOpenTencentGY() {
		driver.get("https://gongyi.qq.com");
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement welcome = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[contains(@class, 'login')]")
		));
		
		String text = welcome.getText().trim();
		System.out.println("页面标题：" + text);
		Assert.assertTrue(text.contains("腾讯公益"));
	}
	
	@Test(priority = 2, description = "搜索框输入并验证")
	public void testSearchInput() {
		driver.get("https://gongyi.qq.com");
		
		// 等搜索按钮可见且可交互
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement searchButton = wait.until(
			ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='nav-wrap']/a"))
		);
		
		searchButton.click();
		
		// 等等搜索框可见且可交互
		WebElement searchBox = wait.until(
		        ExpectedConditions.elementToBeClickable(By.id("key_words"))
		);
		searchBox.sendKeys("免费午餐");
		
		WebElement searchBtn = driver.findElement(By.id("key_btn"));
		searchBtn.click();
		
		// 等搜索结果页面加载（URL 变化)
		String encoded = URLEncoder.encode("免费午餐", StandardCharsets.UTF_8);
	    wait.until(ExpectedConditions.urlContains("s_key=" + encoded));

	    String currentUrl = driver.getCurrentUrl();
	    System.out.println("搜索后URL: " + currentUrl);
	    Assert.assertTrue(currentUrl.contains("s_key=" + encoded), "URL应包含搜索关键词");
	}
	
	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

}
