package com.loogingko.ncjd;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class ApplicationTest {

    @Test
    void test1() {
        Assertions.assertEquals(4, getValue());
    }

    @Test
    void test2() {
        Assertions.assertNull(null, "居然是空的！");
    }

    @Test
    void test3() {
        Assertions.assertSame(4, 3+1, "==值不一致");
    }
    
    private int getValue() {
        return 4;
    }

    @BeforeEach
    void setUp() {
        System.out.println("每个Test前都执行，执行n次");
    }

    @AfterEach
    void tearDown() {
        System.out.println("每个Test后都执行，执行n次");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("运行测试类时最先运行且只执行一次");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("运行测试类时最后运行且只执行一次");
    }
}
