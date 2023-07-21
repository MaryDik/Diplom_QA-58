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
    void setUp() {
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

    @DisplayName("Отказ в покупке по карте, со статусом DECLINED")
    @Test
    void orderPositiveAllFieldValidDeclined() {
        startPage.orderCard();
        var cardInfo = Data.getDeclinedCard();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.declinedCard();
        assertEquals("DECLINED", SQL.getPaymentStatus());
    }

    @DisplayName("Отправка пустой формы запроса")
    @Test
    void creditNegativeAllFieldEmpty() {
        startPage.orderCard();
        var cardInfo = Data.getEmptyCard();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.requiredFieldNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Покупка по не существующей карте")
    @Test
    void buyingOnCreditWithDefunctCard() {
        startPage.orderCard();
        var cardInfo = Data.getCardNotInDatabase();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.declinedCard();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный номер банковской карты: 15 цифр")
    @Test
    void cardDataEntryLessThan16Symbols() {
        startPage.orderCard();
        var cardInfo = Data.getNumberCard15Symbols();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный месяц: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidMonthOneSymbol() {
        startPage.orderCard();
        var cardInfo = Data.getCardMonth1Symbol();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: цифра больше 12")
    @Test
    void shouldPaymentCardInvalidMonthOver12() {
        startPage.orderCard();
        var cardInfo = Data.getCardMonthOver12();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: месяц предшествующий текущему, год текущий")
    @Test
    void shouldPaymentIncorrectCardExpirationDate() {
        startPage.orderCard();
        var cardInfo = Data.getCardMonthPreviousToThisYear();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: не входит в валидный интервал 1-12")
    @Test
    void shouldPaymentWrongMonthFormatOverThisYear() {
        startPage.orderCard();
        var cardInfo = Data.getCardMonth00OverThisYear();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный год: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidYearOneSymbol() {
        startPage.orderCard();
        var cardInfo = Data.getCardYear1Symbol();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Покупка по карте, когда срок действия карты истёк")
    @Test
    void shouldPaymentExpiredCard() {
        startPage.orderCard();
        var cardInfo = Data.getCardYear00();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.expiredNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: срок окончания карты - год, предшествующий текущему")
    @Test
    void shouldPaymentCardYearUnderThisYear() {
        startPage.orderCard();
        var cardInfo = Data.getCardYearUnderThisYear();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.expiredNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: платежная карта действительна более 5 лет")
    @Test
    void shouldPaymentCardYearOverThisYearOn6() {
        startPage.orderCard();
        var cardInfo = Data.getCardYearOverThisYearOn6();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongValidityNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: введено только Имя")
    @Test
    void shouldPaymentInvalidCardHolder() {
        startPage.orderCard();
        var cardInfo = Data.getCardHolder1Word();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: имя и фамилия на кириллице")
    @Test
    void shouldPaymentInvalidCardHolderInCyrillic() {
        startPage.orderCard();
        var cardInfo = Data.getCardHolderCirillic();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: цифры в имени")
    @Test
    void shouldPaymentInvalidCardHolderWithNumbers() {
        startPage.orderCard();
        var cardInfo = Data.getCardHolderWithNumbers();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: символы в имени")
    @Test
    void shouldPaymentInvalidCardHolderSpecialSymbols() {
        startPage.orderCard();
        var cardInfo = Data.getCardSpecialSymbols();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный код CVC: ввод менее 3 цифр")
    @Test
    void shouldPaymentCardInvalidCvc2Symbols() {
        startPage.orderCard();
        var cardInfo = Data.getCardCvv2Symbols();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }

    @DisplayName("Невалидный код CVC состоящий из 1 цифры")
    @Test
    void shouldPaymentCardInvalidCvc1Symbol() {
        startPage.orderCard();
        var cardInfo = Data.getCardCvv1Symbol();
        var orderPage = new OrderCard();
        orderPage.insertCardData(cardInfo);
        orderPage.wrongFormatNotification();
        assertEquals("0", SQL.getOrderCount());
    }
}