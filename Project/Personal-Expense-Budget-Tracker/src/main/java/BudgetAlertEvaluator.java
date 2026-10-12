import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hcltraining.model.Budget;

public class BudgetAlertEvaluator {

    private final List<AlertThresholdStrategy> strategies;

    public BudgetAlertEvaluator(List<AlertThresholdStrategy> strategies) {
        this.strategies = List.copyOf(Objects.requireNonNull(strategies, "strategies"));
    }

    public List<Integer> findTriggeredThresholds(Budget budget, BigDecimal spending) {
        AlertThresholdStrategy.requirePositiveBudgetLimit(budget);
        Objects.requireNonNull(spending, "spending");

        List<Integer> triggeredThresholds = new ArrayList<>();
        for (AlertThresholdStrategy strategy : strategies) {
            if (strategy.isThresholdReached(budget, spending)) {
                triggeredThresholds.add(strategy.getThresholdPercent());
            }
        }
        return List.copyOf(triggeredThresholds);
    }
}
