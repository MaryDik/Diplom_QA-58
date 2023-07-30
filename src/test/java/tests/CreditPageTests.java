package tests;

import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.DataHelper;
import data.SQLHelper;
import pages.StartPage;
import pages.CreditPage;
import com.codeborne.selenide.logevents.SelenideLogger;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class CreditPageTests {

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

    @DisplayName("Успешная покупка в кредит по карте, со статусом APPROVED")
    @Test
    void creditPositiveAllFieldValidApproved() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getApprovedCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkApprovedNotification();
        assertEquals("APPROVED", SQLHelper.getCreditRequestStatus());
    }

    @DisplayName("Отказ в покупке в кредит по карте, со статусом DECLINED")
    @Test
    void creditPositiveAllFieldValidDeclined() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getDeclinedCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkDeclinedNotification();
        assertEquals("DECLINED", SQLHelper.getCreditRequestStatus());
    }

    @DisplayName("Отправка пустой формы запроса")
    @Test
    void creditNegativeAllFieldEmpty() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getEmptyCard();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkRequiredFieldNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Покупка в кредит по не существующей карте")
    @Test
    void buyingOnCreditWithDefunctCard() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardNotInDatabase();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkDeclinedNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный номер банковской карты: 15 цифр")
    @Test
    void cardDataEntryLessThan16Symbols() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getNumberCard15Symbols();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный месяц: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidMonthOneSymbol() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardMonth1Symbol();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: цифра больше 12")
    @Test
    void shouldPaymentCardInvalidMonthOver12() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardMonthOver12();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: месяц предшествующий текущему, год текущий")
    @Test
    void shouldPaymentIncorrectCardExpirationDate() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardMonthPreviousToThisYear();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный формат месяца: не входит в валидный интервал 1-12")
    @Test
    void shouldPaymentWrongMonthFormatOverThisYear() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardMonth00OverThisYear();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный год: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidYearOneSymbol() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardYear1Symbol();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Покупка в кредит по карте, когда срок действия карты истёк")
    @Test
    void shouldPaymentExpiredCard() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardYear00();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkExpiredNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: срок окончания карты - год, предшествующий текущему")
    @Test
    void shouldPaymentCardYearUnderThisYear() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardYearUnderThisYear();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkExpiredNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный период действия карты: платежная карта действительна более 5 лет")
    @Test
    void shouldPaymentCardYearOverThisYearOn6() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardYearOverThisYearOn6();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongValidityNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: введено только Имя")
    @Test
    void shouldPaymentInvalidCardHolder() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardHolder1Word();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: имя и фамилия на кириллице")
    @Test
    void shouldPaymentInvalidCardHolderInCyrillic() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardHolderCirillic();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: цифры в имени")
    @Test
    void shouldPaymentInvalidCardHolderWithNumbers() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardHolderWithNumbers();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Данные о владельце карты указаны неверно: символы в имени")
    @Test
    void shouldPaymentInvalidCardHolderSpecialSymbols() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardSpecialSymbols();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный код CVC: ввод менее 3 цифр")
    @Test
    void shouldPaymentCardInvalidCvc2Symbols() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardCvv2Symbols();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }

    @DisplayName("Невалидный код CVC состоящий из 1 цифры")
    @Test
    void shouldPaymentCardInvalidCvc1Symbol() {
        startPage.goToCreditPage();
        var cardInfo = DataHelper.getCardCvv1Symbol();
        var creditPage = new CreditPage();
        creditPage.insertCardData(cardInfo);
        creditPage.checkWrongFormatNotification();
        assertEquals("0", SQLHelper.getOrderCount());
    }
}

