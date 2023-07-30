package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;

public class StartPage {
    private static SelenideElement heading = $(byText("Путешествие дня"));
    private static SelenideElement buyButton = $(byText("Купить"));
    private static SelenideElement creditButton = $(byText("Купить в кредит"));

    public StartPage() {
        heading.shouldBe(visible);
    }

    public OrderCardPage goToOrderCardPage() {
        buyButton.click();
        return new OrderCardPage();
    }

    public CreditPage goToCreditPage() {
        creditButton.click();
        return new CreditPage();
    }
}
