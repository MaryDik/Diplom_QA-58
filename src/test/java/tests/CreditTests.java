package tests;

import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import data.Data;
import data.SQL;
import pages.OrderCard;
import pages.Start;
import pages.Credit;
import com.codeborne.selenide.logevents.SelenideLogger;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;


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


}

