package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;

public class Start {
    private static SelenideElement heading = $(byText("Путешествие дня"));
    private static SelenideElement buyButton = $(byText("Купить"));
    private static SelenideElement creditButton = $(byText("Купить в кредит"));

    public Start() {
        heading.shouldBe(visible);
    }

    public OrderCard orderCard() {
        buyButton.click();
        return new OrderCard();
    }

    public Credit credit() {
        creditButton.click();
        return new Credit();
    }
}
