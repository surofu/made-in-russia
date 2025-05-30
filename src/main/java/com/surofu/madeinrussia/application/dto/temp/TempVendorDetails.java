package com.surofu.madeinrussia.application.dto.temp;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class TempVendorDetails implements Serializable {

    private final String mainDescription;
    private final String furtherDescription;
    private final List<String> images;

    public TempVendorDetails() {
        int index = ThreadLocalRandom.current().nextInt(0, 3);

        mainDescription = List.of(
                "Высококачественный металлопрокат для тяжелой промышленности и строительства. Наша продукция – горячекатаные и холоднокатаные листы, балки, арматура – соответствует ГОСТ и международным стандартам (ASTM, EN). Собственные лаборатории контролируют состав сплавов и механические свойства. Поставляем металл для мостов, нефтяных платформ и высотного строительства с гарантией отсутствия внутренних дефектов.",
                "Щебень гранитный и песок кварцевый с доставкой от карьера до объекта. Добываем сырье на месторождениях с запасами 50+ млн тонн, используем дробильно-сортировочные комплексы Metso и Sandvik. Фракции 5-20, 20-40, 40-70 мм – для дорожного строительства, ЖБИ и ландшафтного дизайна. Лабораторные протоколы на лещадность, морозостойкость F300 и радиоактивность 1 класса прилагаются к каждой партии.",
                "Полиэтиленовые трубы ПЭ100 и ПЭ80+ для газовых сетей под давлением до 12 атм. Экструзионные линии Battenfeld-Cincinnati производят трубы диаметром 20–1200 мм с сертификатами ISO 4437 и ГОСТ Р 50838. Срок службы – 50 лет без коррозии и зарастания сечения. Участвовали в замене изношенных стальных магистралей в 15 регионах РФ.",
                "Синтетические и минеральные смазки для экстремальных нагрузок  \n" +
                        "Линейка включает:  \n" +
                        "- Термостойкие составы для сталелитейных цехов (до +600°C);  \n" +
                        "- Биоразлагаемые масла для лесной техники;  \n" +
                        "- Пластичные смазки с дисульфидом молибдена для горнодобывающего оборудования.  \n" +
                        "Снижаем износ узлов трения в 3 раза по сравнению с аналогами."
        ).get(index);
        furtherDescription = List.of(
                "Гибкие условия для оптовых покупателей: отсрочка платежа, логистика со складов в 5 регионах и индивидуальные маркировки партий.",
                "Собственный автопарк самосвалов Volvo – обеспечим отгрузку 24/7 даже в сезонное пиковое время.",
                "Бесплатный расчет проекта газопровода и шеф-монтаж для оптовых заказчиков.",
                "Разрабатываем индивидуальные рецептуры под вашу технику – пришлите техническое задание для тестового образца."
        ).get(index);

        images = List.of(
                "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8Y29tcGFueXxlbnwwfHwwfHx8MA%3D%3D",
                "https://images.unsplash.com/photo-1590880795696-20c7dfadacde?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjB8fGNvbXBhbnklMjB3b29kfGVufDB8fDB8fHww",
                "https://images.unsplash.com/photo-1642005581880-3536a680febf?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8am9iJTIwd29vZHxlbnwwfHwwfHx8MA%3D%3D",
                "https://plus.unsplash.com/premium_photo-1661932816149-291a447e3022?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTN8fGpvYiUyMG1pbmVyfGVufDB8fDB8fHww"
        );
    }
}
