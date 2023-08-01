package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import pages.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

class OrderCardPageTests {
    StartPage startPage = open("http://localhost:8080/", StartPage.class);

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
        SQLHelper.clearDB();
    }

    @DisplayName("Покупка по карте, со статусом APPROVED")
    @Test
    void orderPositiveAllFieldValidApproved() {
        var cardInfo = DataHelper.getApprovedCard();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkApprovedNotification();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());

    }

    @DisplayName("Отказ в покупке по карте, со статусом DECLINED")
    @Test
    void orderPositiveAllFieldValidDeclined() {
        var cardInfo = DataHelper.getDeclinedCard();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkDeclinedNotification();
        assertEquals("DECLINED", SQLHelper.getPaymentStatus());
    }

    @DisplayName("Отправка пустой формы запроса")
    @Test
    void creditNegativeAllFieldEmpty() {
        var cardInfo = DataHelper.getEmptyCard();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkRequiredFieldNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Покупка по не существующей карте")
    @Test
    void buyingOnCreditWithDefunctCard() {
        var cardInfo = DataHelper.getCardNotInDatabase();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkDeclinedNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный номер банковской карты: 15 цифр")
    @Test
    void cardDataEntryLessThan16Symbols() {
        var cardInfo = DataHelper.getNumberCard15Symbols();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный месяц: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidMonthOneSymbol() {
        var cardInfo = DataHelper.getCardMonth1Symbol();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: цифра больше 12")
    @Test
    void shouldPaymentCardInvalidMonthOver12() {
        var cardInfo = DataHelper.getCardMonthOver12();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: месяц предшествующий текущему, год текущий")
    @Test
    void shouldPaymentIncorrectCardExpirationDate() {
        var cardInfo = DataHelper.getCardMonthPreviousToThisYear();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: не входит в валидный интервал 1-12")
    @Test
    void shouldPaymentWrongMonthFormatOverThisYear() {
        var cardInfo = DataHelper.getCardMonth00OverThisYear();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный год: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidYearOneSymbol() {
        var cardInfo = DataHelper.getCardYear1Symbol();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Покупка по карте, когда срок действия карты истёк")
    @Test
    void shouldPaymentExpiredCard() {
        var cardInfo = DataHelper.getCardYear00();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkExpiredNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: срок окончания карты - год, предшествующий текущему")
    @Test
    void shouldPaymentCardYearUnderThisYear() {
        var cardInfo = DataHelper.getCardYearUnderThisYear();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkExpiredNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: платежная карта действительна более 5 лет")
    @Test
    void shouldPaymentCardYearOverThisYearOn6() {
        var cardInfo = DataHelper.getCardYearOverThisYearOn6();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: введено только Имя")
    @Test
    void shouldPaymentInvalidCardHolder() {
        var cardInfo = DataHelper.getCardHolder1Word();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: имя и фамилия на кириллице")
    @Test
    void shouldPaymentInvalidCardHolderInCyrillic() {
        var cardInfo = DataHelper.getCardHolderCirillic();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: цифры в имени")
    @Test
    void shouldPaymentInvalidCardHolderWithNumbers() {
        var cardInfo = DataHelper.getCardHolderWithNumbers();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: символы в имени")
    @Test
    void shouldPaymentInvalidCardHolderSpecialSymbols() {
        var cardInfo = DataHelper.getCardSpecialSymbols();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный код CVC: ввод менее 3 цифр")
    @Test
    void shouldPaymentCardInvalidCvc2Symbols() {
        var cardInfo = DataHelper.getCardCvv2Symbols();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный код CVC состоящий из 1 цифры")
    @Test
    void shouldPaymentCardInvalidCvc1Symbol() {
        var cardInfo = DataHelper.getCardCvv1Symbol();
        var orderPage = startPage.goToOrderCardPage();
        orderPage.insertCardData(cardInfo);
        orderPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }
}