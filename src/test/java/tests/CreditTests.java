package tests;

import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.Data;
import data.SQL;
import pages.Start;
import pages.Credit;
import com.codeborne.selenide.logevents.SelenideLogger;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class CreditTests {

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
    void setUp() {
        SQL.clearDB();
    }

   @DisplayName("Успешная покупка в кредит по карте, со статусом APPROVED")
   @Test
   void creditPositiveAllFieldValidApproved() {
       startPage.credit();
       var cardInfo = Data.getApprovedCard();
       var creditPage = new Credit();
       creditPage.insertCardData(cardInfo);
       creditPage.approvedCard();
      assertEquals("APPROVED", SQL.getCreditRequestStatus());
    }

    @DisplayName("Отказ в покупке в кредит по карте, со статусом DECLINED")
    @Test
    void creditPositiveAllFieldValidDeclined() {
        startPage.credit();
        var cardInfo = Data.getDeclinedCard();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.declinedCard();
        assertEquals("DECLINED", SQL.getCreditRequestStatus());
    }
    @DisplayName("Отправка пустой формы запроса")
    @Test
    void creditNegativeAllFieldEmpty() {
        startPage.credit();
        var cardInfo = Data.getEmptyCard();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.requiredFieldNotification();
        assertNull(SQL.getCreditRequestStatus());
    }


}

