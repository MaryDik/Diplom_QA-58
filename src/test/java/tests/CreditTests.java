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
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Покупка в кредит по не существующей карте")
    @Test
    void buyingOnCreditWithDefunctCard() {
        startPage.credit();
        var cardInfo = Data.getCardNotInDatabase();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.declinedCard();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный номер банковской карты: 15 цифр")
    @Test
    void cardDataEntryLessThan16Symbols() {
        startPage.credit();
        var cardInfo = Data.getNumberCard15Symbols();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный месяц: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidMonthOneSymbol() {
        startPage.credit();
        var cardInfo = Data.getCardMonth1Symbol();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: цифра больше 12")
    @Test
    void shouldPaymentCardInvalidMonthOver12() {
        startPage.credit();
        var cardInfo = Data.getCardMonthOver12();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: месяц предшествующий текущему, год текущий")
    @Test
    void shouldPaymentIncorrectCardExpirationDate() {
        startPage.credit();
        var cardInfo = Data.getCardMonthPreviousToThisYear();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: не входит в валидный интервал 1-12")
    @Test
    void shouldPaymentWrongMonthFormatOverThisYear() {
        startPage.credit();
        var cardInfo = Data.getCardMonth00OverThisYear();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный год: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidYearOneSymbol() {
        startPage.credit();
        var cardInfo = Data.getCardYear1Symbol();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Покупка в кредит по карте, когда срок действия карты истёк")
    @Test
    void shouldPaymentExpiredCard() {
        startPage.credit();
        var cardInfo = Data.getCardYear00();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.expiredNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: срок окончания карты - год, предшествующий текущему")
    @Test
    void shouldPaymentCardYearUnderThisYear() {
        startPage.credit();
        var cardInfo = Data.getCardYearUnderThisYear();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.expiredNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: платежная карта действительна более 5 лет")
    @Test
    void shouldPaymentCardYearOverThisYearOn6() {
        startPage.credit();
        var cardInfo = Data.getCardYearOverThisYearOn6();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: введено только Имя")
    @Test
    void shouldPaymentInvalidCardHolder() {
        startPage.credit();
        var cardInfo = Data.getCardHolder1Word();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: имя и фамилия на кириллице")
    @Test
    void shouldPaymentInvalidCardHolderInCyrillic() {
        startPage.credit();
        var cardInfo = Data.getCardHolderCirillic();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: цифры в имени")
    @Test
    void shouldPaymentInvalidCardHolderWithNumbers() {
        startPage.credit();
        var cardInfo = Data.getCardHolderWithNumbers();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: символы в имени")
    @Test
    void shouldPaymentInvalidCardHolderSpecialSymbols() {
        startPage.credit();
        var cardInfo = Data.getCardSpecialSymbols();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный код CVC: ввод менее 3 цифр")
    @Test
    void shouldPaymentCardInvalidCvc2Symbols() {
        startPage.credit();
        var cardInfo = Data.getCardCvv2Symbols();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный код CVC состоящий из 1 цифры")
    @Test
    void shouldPaymentCardInvalidCvc1Symbol() {
        startPage.credit();
        var cardInfo = Data.getCardCvv1Symbol();
        var creditPage = new Credit();
        creditPage.insertCardData(cardInfo);
        creditPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }
}

