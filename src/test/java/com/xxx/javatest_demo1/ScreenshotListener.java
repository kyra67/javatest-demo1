package com.xxx.javatest_demo1;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Attachment;

public class ScreenshotListener implements ITestListener {
	public static WebDriver driver;
	
	@Attachment(value = "失败截图", type = "image/png")
	public byte[] saveScreenshot(WebDriver driver) {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		if(driver != null) {
			saveScreenshot(driver);
		}
	}
}
