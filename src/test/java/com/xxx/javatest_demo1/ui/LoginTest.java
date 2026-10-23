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

import com.xxx.javatest_demo1.ScreenshotListener;

public class LoginTest {
	
	private WebDriver driver;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		if(this.driver != null) {
			this.driver.quit(); 
			this.driver = null;
		}
		
		// 启动 Chrome（由 Selenium 自动匹配 chromedriver）
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		
		ScreenshotListener.driver = this.driver;
	}
	
//	@Test(priority = 1, description = "打开腾讯公益页验证欢迎语")
	@Test(priority = 1, groups = {"ui", "smoke"})
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
	
	@Test(priority = 2, groups = {"ui", "smoke"}, description = "搜索框输入并验证")
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
	
	@Test(priority=3, groups = {"ui", "smoke"}, description = "失败截图验证")
	public void testLoginFail() {
		driver.get("https://gongyi.qq.com");
		driver.findElement(By.id("kw")).click();
	}
	
	@AfterMethod
	public void tearDown() {
		if (this.driver != null) {
			this.driver.quit();
			this.driver = null;
		}
	}

}
