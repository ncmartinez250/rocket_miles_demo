UPDATE cashier_registry
    SET twenty_dollar_count = 1, ten_dollar_count = 2,
            five_dollar_count = 3, two_dollar_count = 4,
            one_dollar_count = 5
    WHERE id = 1;