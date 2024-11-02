package su.kartushin.busAPI.enums;

import su.kartushin.busAPI.utils.Description;

public enum Country {
    @Description("Россия")
    RU,
    @Description("Нидерланды")
    NL,
    @Description("Израиль")
    IL,
    @Description("Чехия")
    CZ,
    @Description("Румыния")
    RO,
    @Description("Португалия")
    PT,
    @Description("Сербия")
    RS,
    @Description("Великобритания")
    GB,
    @Description("Канада")
    CA,
    @Description("Словакия")
    SK,
    @Description("Турция")
    TR,
    @Description("Италия")
    IT,
    @Description("Швеция")
    SE,
    @Description("Ирландия")
    IE,
    @Description("Литва")
    LT,
    @Description("Норвегия")
    NO,
    @Description("Япония")
    JP,
    @Description("Германия")
    DE,
    @Description("Латвия")
    LV,
    @Description("США")
    US,
    @Description("Польша")
    PL,
    @Description("Финляндия")
    FI,
    @Description("Швейцария")
    CH,
    @Description("Франция")
    FR,
    @Description("Эстония")
    EE,
    @Description("Бельгия")
    BE,
    @Description("Словения")
    SI,
    @Description("Гонконг")
    HK,
    @Description("Молдова")
    MD,
    @Description("Украина")
    UA,
    @Description("Болгария")
    BG,
    @Description("Венгрия")
    HU,
    @Description("Казахстан")
    KZ,
    @Description("Испания")
    ES,
    @Description("Дания")
    DK,
    @Description("Исландия")
    IS,
    @Description("Северная Македония")
    MK,
    @Description("Австрия")
    AT,
    @Description("Бразилия")
    BR,
    @Description("Армения")
    AM,
    @Description("Хорватия")
    HR;


    public String getDescription() {
        try {
            return this.getClass()
                    .getField(this.name())
                    .getAnnotation(Description.class)
                    .value();
        } catch (NoSuchFieldException e) {
            return "Нет описания";
        }
    }
}
