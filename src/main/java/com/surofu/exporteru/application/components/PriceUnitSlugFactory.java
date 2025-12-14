package com.surofu.exporteru.application.components;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceUnitSlugFactory {
  private final static Map<String, String> map = new HashMap<>();

  static {
    // Русский
    map.put("миллиграмм", "mg");
    map.put("грамм", "g");
    map.put("килограмм", "kg");
    map.put("центнер", "c");
    map.put("тонна", "t");
    map.put("миллилитр", "ml");
    map.put("литр", "l");
    map.put("гектолитр", "hl");
    map.put("метр³", "m3");
    map.put("метр²", "m2");
    map.put("сантиметр²", "cm2");
    map.put("штука", "pcs");
    map.put("упаковка", "pack");
    map.put("метр", "m");
    map.put("сантиметр", "cm");
    map.put("пара", "pair");
    map.put("комплект", "set");
    map.put("коробка", "box");
    map.put("мешок", "bag");

    // Английский
    map.put("milligram", "mg");
    map.put("gram", "g");
    map.put("kilogram", "kg");
    map.put("centner", "c");
    map.put("tonne", "t");
    map.put("milliliter", "ml");
    map.put("liter", "l");
    map.put("hectoliter", "hl");
    map.put("cubic meter", "m3");
    map.put("square meter", "m2");
    map.put("square centimeter", "cm2");
    map.put("piece", "pcs");
    map.put("packaging", "pack");
    map.put("meter", "m");
    map.put("centimeter", "cm");
    map.put("pair", "pair");
    map.put("set", "set");
    map.put("box", "box");
    map.put("bag", "bag");

    // Китайский
    map.put("毫克", "mg");
    map.put("克", "g");
    map.put("公斤", "kg");
    map.put("公担", "c");
    map.put("吨", "t");
    map.put("毫升", "ml");
    map.put("升", "l");
    map.put("百升", "hl");
    map.put("立方米", "m3");
    map.put("平方米", "m2");
    map.put("平方厘米", "cm2");
    map.put("件", "pcs");
    map.put("包装", "pack");
    map.put("米", "m");
    map.put("厘米", "cm");
    map.put("双", "pair");
    map.put("套", "set");
    map.put("盒", "box");
    map.put("袋", "bag");

    // Хинди
    map.put("मिलीग्राम", "mg");
    map.put("ग्राम", "g");
    map.put("किलोग्राम", "kg");
    map.put("क्विंटल", "c");
    map.put("टन", "t");
    map.put("मिलीलीटर", "ml");
    map.put("लीटर", "l");
    map.put("हेक्टोलीटर", "hl");
    map.put("घन मीटर", "m3");
    map.put("वर्ग मीटर", "m2");
    map.put("वर्ग सेंटीमीटर", "cm2");
    map.put("टुकड़ा", "pcs");
    map.put("पैकेजिंग", "pack");
    map.put("मीटर", "m");
    map.put("सेंटीमीटर", "cm");
    map.put("जोड़ी", "pair");
    map.put("सेट", "set");
    map.put("डिब्बा", "box");
    map.put("बैग", "bag");
  }

  public static String getSlug(String unit) {
    if (!map.containsKey(unit)) {
      log.error("No such unit: {}", unit);
    }
    return map.getOrDefault(unit, unit);
  }
}