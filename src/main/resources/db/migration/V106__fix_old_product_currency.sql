update product_prices
set currency = 'RUB'
where currency not in ('RUB', 'USD', 'CNY');