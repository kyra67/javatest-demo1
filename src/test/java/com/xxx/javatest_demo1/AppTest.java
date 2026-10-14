package com.xxx.javatest_demo1;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
	
	private Calculator calculator;
	
	@BeforeEach
	void setUp() {
		calculator = new Calculator();
	}
	
	@Test
	@DisplayName("加法测试")
	void testAdd() {
		assertEquals(5, calculator.add(2, 3));
	}
	
	@Test
	@DisplayName("除零异常测试")
	void testDivideByZero() {
		assertThrows(IllegalArgumentException.class, new Executable() {
			public void execute() throws Throwable {
				calculator.divide(10, 0);
			}
		});
	}
	
	@Test
	@Tag("smoke")
	@DisplayName("冒烟-加法")
	void testAddSmoke() {
		assertTrue(calculator.add(1, 1) == 2);
	}
}
