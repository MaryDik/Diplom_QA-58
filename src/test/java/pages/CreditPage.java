package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class CreditPage {
    private final SelenideElement heading = $x("//h3[text()[contains(., 'Кредит по данным карты')]]");
    private final SelenideElement cardNumber = $x("//*[contains(text(), 'Номер карты')]/../span/input");
    private final SelenideElement month = $x("//*[contains(text(), 'Месяц')]/../*/input");
    private final SelenideElement year = $x("//*[contains(text(), 'Год')]/../*/input");
    private final SelenideElement cardHolder = $x("//*[contains(text(), 'Владелец')]/../*/input");
    private final SelenideElement cvc = $x("//*[contains(text(), 'CVC/CVV')]/../*/input");
    private final SelenideElement continueButton = $x("//*[text()[contains(., 'Продолжить')]]");


    public CreditPage() {
        heading.shouldBe(visible);
        heading.shouldHave(text("Кредит по данным карты"));
    }

    public void insertCardData(DataHelper.CardInfo cardInfo) {
        cardNumber.setValue(cardInfo.getCardNumber());
        month.setValue(cardInfo.getMonth());
        year.setValue(cardInfo.getYear());
        cardHolder.setValue(cardInfo.getCardHolder());
        cvc.setValue(cardInfo.getCvc());
        continueButton.click();
    }

    public void checkApprovedNotification() {
        SelenideElement successfulNotification = $(".notification_status_ok .notification__content").shouldHave(Condition.text("Операция одобрена Банком."), Duration.ofMillis(15000));
        successfulNotification.shouldBe(Condition.visible);
    }

    public void checkDeclinedNotification() {
        SelenideElement declineNotification = $(".notification_status_error .notification__content").shouldHave(Condition.text("Ошибка! Банк отказал в проведении операции."), Duration.ofMillis(15000));
        declineNotification.shouldBe(Condition.visible);
    }

    public void checkWrongFormatNotification() {
        SelenideElement wrongFormat = $(".input__sub").shouldHave(Condition.text("Неверный формат"));
        wrongFormat.shouldBe(Condition.visible);
    }

    public void checkRequiredFieldNotification() {
        SelenideElement empty = $(".input__sub").shouldHave(Condition.text("Поле обязательно для заполнения"));
        empty.shouldBe(Condition.visible);
    }

    public void checkExpiredNotification() {
        SelenideElement expired = $(".input__sub").shouldHave(Condition.text("Истёк срок действия карты"));
        expired.shouldBe(Condition.visible);
    }

    public void checkWrongValidityNotification() {
        SelenideElement expired = $(".input__sub").shouldHave(Condition.text("Неверно указан срок действия карты"));
        expired.shouldBe(Condition.visible);
    }
}