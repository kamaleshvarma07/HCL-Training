import java.math.BigDecimal;
import java.util.Objects;

import com.hcltraining.model.Budget;

public interface AlertThresholdStrategy {

    int getThresholdPercent();

    static BigDecimal requirePositiveBudgetLimit(Budget budget) {
        Objects.requireNonNull(budget, "budget");
        BigDecimal limit = budget.getLimit();
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget limit must be non-null and greater than zero.");
        }
        return limit;
    }

    default boolean isThresholdReached(Budget budget, BigDecimal spending) {
        Objects.requireNonNull(spending, "spending");

        BigDecimal threshold = requirePositiveBudgetLimit(budget)
                .multiply(BigDecimal.valueOf(getThresholdPercent()))
                .movePointLeft(2);
        return spending.compareTo(threshold) >= 0;
    }
}
