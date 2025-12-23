package com.surofu.exporteru.application.components;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceUnitSlugFactory {
  private final static Map<String, String> map = new HashMap<>();

  static {
    // --- РУССКИЙ ЯЗЫК ---
    // Масса
    addRussianVariants("миллиграмм", "миллиграмм", "миллиграмма", "миллиграмму", "миллиграмме",
        "миллиграммом", "миллиграмме", "миллиграммы", "миллиграммов", "миллиграммам",
        "миллиграммами", "миллиграммах", "mg");
    // Вариант с одной "л" (частая опечатка/упрощение)
    addRussianVariants("милиграмм", "милиграмм", "милиграмма", "милиграмму", "милиграмме",
        "милиграммом", "милиграмме", "милиграммы", "милиграммов", "милиграммам", "милиграммами",
        "милиграммах", "mg");

    addRussianVariants("грамм", "грамм", "грамма", "грамму", "грамме", "граммом", "грамме",
        "граммы", "граммов", "граммам", "граммами", "граммах", "g");
    addRussianVariants("килограмм", "килограмм", "килограмма", "килограмму", "килограмме",
        "килограммом", "килограмме", "килограммы", "килограммов", "килограммам", "килограммами",
        "килограммах", "kg");
    map.put("центнер", "c"); // Центнер склоняется редко, обычно используется в им. падеже.
    map.put("центнера", "c");
    map.put("центнеров", "c");
    addRussianVariants("тонна", "тонна", "тонны", "тонне", "тонну", "тонной", "тонне", "тонны",
        "тонн", "тоннам", "тоннами", "тоннах", "t");

    // Объем
    addRussianVariants("миллилитр", "миллилитр", "миллилитра", "миллилитру", "миллилитре",
        "миллилитром", "миллилитре", "миллилитры", "миллилитров", "миллилитрам", "миллилитрами",
        "миллилитрах", "ml");
    addRussianVariants("литр", "литр", "литра", "литру", "литре", "литром", "литре", "литры",
        "литров", "литрам", "литрами", "литрах", "l");
    map.put("гектолитр", "hl");
    map.put("гектолитра", "hl");
    map.put("гектолитров", "hl");

    // Длина, Площадь, Объем (геом.)
    addRussianVariants("метр", "метр", "метра", "метру", "метре", "метром", "метре", "метры",
        "метров", "метрам", "метрами", "метрах", "m");
    addRussianVariants("сантиметр", "сантиметр", "сантиметра", "сантиметру", "сантиметре",
        "сантиметром", "сантиметре", "сантиметры", "сантиметров", "сантиметрам", "сантиметрами",
        "сантиметрах", "cm");
    map.put("метр²", "m2");
    map.put("квадратный метр", "m2");
    map.put("кв. м", "m2");
    map.put("сантиметр²", "cm2");
    map.put("квадратный сантиметр", "cm2");
    map.put("кв. см", "cm2");
    map.put("метр³", "m3");
    map.put("кубический метр", "m3");
    map.put("куб. м", "m3");

    // Штучные
    addRussianVariants("штука", "штука", "штуки", "штуке", "штуку", "штукой", "штуке", "штуки",
        "штук", "штукам", "штуками", "штуках", "pcs");
    map.put("шт.", "pcs");
    addRussianVariants("упаковка", "упаковка", "упаковки", "упаковке", "упаковку", "упаковкой",
        "упаковке", "упаковки", "упаковок", "упаковкам", "упаковками", "упаковках", "pack");
    map.put("уп.", "pack");
    addRussianVariants("пара", "пара", "пары", "паре", "пару", "парой", "паре", "пары", "пар",
        "парам", "парами", "парах", "pair");
    addRussianVariants("комплект", "комплект", "комплекта", "комплекту", "комплекте",
        "комплектом", "комплекте", "комплекты", "комплектов", "комплектам", "комплектами",
        "комплектах", "set");
    addRussianVariants("коробка", "коробка", "коробки", "коробке", "коробку", "коробкой",
        "коробке", "коробки", "коробок", "коробкам", "коробками", "коробках", "box");
    addRussianVariants("мешок", "мешок", "мешка", "мешку", "мешке", "мешком", "мешке", "мешки",
        "мешков", "мешкам", "мешками", "мешках", "bag");

    // --- АНГЛИЙСКИЙ ЯЗЫК ---
    // Масса
    map.put("milligram", "mg");
    map.put("milligrams", "mg");
    map.put("gram", "g");
    map.put("grams", "g");
    map.put("kilogram", "kg");
    map.put("kilograms", "kg");
    map.put("centner", "c");
    map.put("centners", "c");
    map.put("ton", "t");
    map.put("tons", "t"); // tonne/tonnes тоже вариант
    map.put("tonne", "t");
    map.put("tonnes", "t");

    // Объем
    map.put("milliliter", "ml");
    map.put("millilitre", "ml"); // Брит. вариант
    map.put("milliliters", "ml");
    map.put("millilitres", "ml");
    map.put("liter", "l");
    map.put("litre", "l");
    map.put("liters", "l");
    map.put("litres", "l");
    map.put("hectoliter", "hl");
    map.put("hectolitre", "hl");
    map.put("hectoliters", "hl");
    map.put("hectolitres", "hl");

    // Длина, Площадь, Объем (геом.)
    map.put("meter", "m");
    map.put("metre", "m");
    map.put("meters", "m");
    map.put("metres", "m");
    map.put("centimeter", "cm");
    map.put("centimetre", "cm");
    map.put("centimeters", "cm");
    map.put("centimetres", "cm");
    map.put("square meter", "m2");
    map.put("square metre", "m2");
    map.put("square meters", "m2");
    map.put("square metres", "m2");
    map.put("sq m", "m2");
    map.put("square centimeter", "cm2");
    map.put("square centimetre", "cm2");
    map.put("square centimeters", "cm2");
    map.put("square centimetres", "cm2");
    map.put("sq cm", "cm2");
    map.put("cubic meter", "m3");
    map.put("cubic metre", "m3");
    map.put("cubic meters", "m3");
    map.put("cubic metres", "m3");
    map.put("cu m", "m3");

    // Штучные
    map.put("piece", "pcs");
    map.put("pieces", "pcs");
    map.put("pc", "pcs");
    map.put("pcs", "pcs");
    map.put("packaging", "pack");
    map.put("packagings", "pack");
    map.put("pack", "pack");
    map.put("packs", "pack");
    map.put("packet", "pack");
    map.put("packets", "pack"); // Альтернатива
    map.put("pair", "pair");
    map.put("pairs", "pair");
    map.put("set", "set");
    map.put("sets", "set");
    map.put("kit", "set");
    map.put("kits", "set"); // Альтернатива
    map.put("box", "box");
    map.put("boxes", "box");
    map.put("bag", "bag");
    map.put("bags", "bag");
    map.put("sack", "bag");
    map.put("sacks", "bag"); // Альтернатива (мешок)

    // --- КИТАЙСКИЙ ЯЗЫК ---
    // Масса
    map.put("毫克", "mg"); // миллиграмм
    map.put("克", "g");    // грамм
    map.put("千克", "kg"); // килограмм (альтернатива)
    map.put("公斤", "kg"); // килограмм
    map.put("斤", "0.5kg"); // цзинь (0.5 кг) - важно для Китая!
    map.put("公担", "c");  // центнер
    map.put("吨", "t");    // тонна

    // Объем
    map.put("毫升", "ml"); // миллилитр
    map.put("升", "l");    // литр
    map.put("百升", "hl"); // гектолитр

    // Длина, Площадь, Объем (геом.)
    map.put("米", "m");    // метр
    map.put("厘米", "cm"); // сантиметр
    map.put("平方米", "m2"); // квадратный метр
    map.put("平米", "m2");  // разг. вариант
    map.put("平方厘米", "cm2"); // кв. сантиметр
    map.put("立方米", "m3"); // кубический метр
    map.put("立方", "m3");   // разг. вариант для объема

    // Штучные
    map.put("件", "pcs");   // штука, предмет
    map.put("个", "pcs");   // общая классификаторная "штука"
    map.put("包装", "pack"); // упаковка
    map.put("包", "pack");   // пакет, упаковка
    map.put("双", "pair");  // пара (обуви, носков)
    map.put("对", "pair");  // пара (предметов)
    map.put("套", "set");   // комплект, набор
    map.put("盒", "box");   // коробка
    map.put("箱", "box");   // ящик, коробка (крупнее)
    map.put("袋", "bag");   // мешок, пакет
    map.put("桶", "drum");  // бочка (может понадобиться)

    // --- ХИНДИ ---
    // Масса (мужской род)
    addHindiMasculineVariants("मिलीग्राम", "mg");
    addHindiMasculineVariants("ग्राम", "g");
    addHindiMasculineVariants("किलोग्राम", "kg");
    map.put("क्विंटल", "c"); // Centner
    addHindiMasculineVariants("टन", "t");

    // Объем (мужской род)
    addHindiMasculineVariants("मिलीलीटर", "ml");
    addHindiMasculineVariants("लीटर", "l");
    addHindiMasculineVariants("हेक्टोलीटर", "hl");

    // Длина, Площадь (мужской род)
    addHindiMasculineVariants("मीटर", "m");
    addHindiMasculineVariants("सेंटीमीटर", "cm");
    map.put("वर्ग मीटर", "m2");
    map.put("वर्ग सेंटीमीटर", "cm2");
    map.put("घन मीटर", "m3");

    // Штучные
    // "टुकड़ा" (штука) - мужской род
    addHindiMasculineVariants("टुकड़ा", "pcs");
    // "पैकेजिंग" (упаковка) - женский род
    addHindiFeminineVariants("पैकेजिंग", "pack");
    // "जोड़ी" (пара) - женский род
    addHindiFeminineVariants("जोड़ी", "pair");
    // "सेट" (комплект) - мужской род (заимствование)
    addHindiMasculineVariants("सेट", "set");
    // "डिब्बा" (коробка) - мужской род
    addHindiMasculineVariants("डिब्बा", "box");
    // "बैग" (мешок) - мужской род (заимствование)
    addHindiMasculineVariants("बैग", "bag");
    map.put("थैला", "bag"); // Альтернативное слово (мешок)
  }

  // Вспомогательные методы для добавления склонений
  private static void addRussianVariants(String baseKey,
                                         String nominativeSingular, String genitiveSingular,
                                         String dativeSingular, String accusativeSingular,
                                         String instrumentalSingular, String prepositionalSingular,
                                         String nominativePlural, String genitivePlural,
                                         String dativePlural, String instrumentalPlural,
                                         String prepositionalPlural, String code) {
    PriceUnitSlugFactory.map.put(nominativeSingular.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(genitiveSingular.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(dativeSingular.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(accusativeSingular.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(instrumentalSingular.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(prepositionalSingular.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(nominativePlural.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(genitivePlural.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(dativePlural.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(instrumentalPlural.toLowerCase(), code);
    PriceUnitSlugFactory.map.put(prepositionalPlural.toLowerCase(), code);
    // Добавляем также базовый ключ (опционально, для удобства)
    PriceUnitSlugFactory.map.put(baseKey.toLowerCase(), code);
  }

  // Для хинди: мужской род (окончания: а, е, ओं)
  private static void addHindiMasculineVariants(String baseWord,
                                                String code) {
    PriceUnitSlugFactory.map.put(baseWord, code); // Именительный, ед.ч.
    PriceUnitSlugFactory.map.put(baseWord + "ा", code); // Винительный, ед.ч.
    PriceUnitSlugFactory.map.put(baseWord + "े", code); // Множественное число (им., вин.)
    PriceUnitSlugFactory.map.put(baseWord + "ों", code); // Множественное число (косв. падежи)
  }

  // Для хинди: женский род (окончания: ई, एँ, ओं)
  private static void addHindiFeminineVariants(String baseWord, String code) {
    PriceUnitSlugFactory.map.put(baseWord, code); // Именительный, ед.ч.
    PriceUnitSlugFactory.map.put(baseWord + "ी", code); // Винительный, ед.ч.
    PriceUnitSlugFactory.map.put(baseWord + "ें", code); // Множественное число (им., вин.)
    PriceUnitSlugFactory.map.put(baseWord + "ों", code); // Множественное число (косв. падежи)
  }

  public static String getSlug(String unit) {
    if (!map.containsKey(unit)) {
      log.error("No such unit: {}", unit);
    }
    return map.getOrDefault(unit, unit);
  }
}