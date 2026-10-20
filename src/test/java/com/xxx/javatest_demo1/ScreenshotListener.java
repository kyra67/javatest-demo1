package com.xxx.javatest_demo1;

import java.io.ByteArrayInputStream;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Allure;

public class ScreenshotListener implements ITestListener {
	public static WebDriver driver;
	
	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("【监听器】进入失败回调，driver是否null：" + (driver == null));
		if(driver != null) {
			byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("失败截图", "image/png", new ByteArrayInputStream(screenshotBytes), "png");
		}
	}
}
