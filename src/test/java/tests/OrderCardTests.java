package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.Data;
import data.SQL;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import pages.OrderCard;
import pages.Start;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

class OrderCardTests {
    Start startPage = open("http://localhost:8080/", Start.class);

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp()  {
        SQL.clearDB();
    }
    @DisplayName("Покупка по карте, со статусом APPROVED")
    @Test
    void orderPositiveAllFieldValidApproved() {
        startPage.orderCard();
        var cardInfo = Data.getApprovedCard();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.approvedCard();
      assertEquals("APPROVED", SQL.getPaymentStatus());

    }

}