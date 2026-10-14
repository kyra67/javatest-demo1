package com.xxx.javatest_demo1;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.*;

public class AppTest {
	
	private Calculator calculator;
	
	@BeforeMethod
	void setUp() {
		calculator = new Calculator();
	}
	
	@Test(priority = 1, description = "加法测试")
	void testAdd() {
		assertEquals(5, calculator.add(2, 3));
	}
	
	@Test(priority = 2, description = "除零异常测试")
	void testDivideByZero() {
		Assert.assertThrows(IllegalArgumentException.class, () -> {
			calculator.divide(10, 0);
		});
	}
	
	@Test(priority = 2, description = "冒烟-加法")
	void testAddSmoke() {
		assertTrue(calculator.add(1, 1) == 2);
	}
}
