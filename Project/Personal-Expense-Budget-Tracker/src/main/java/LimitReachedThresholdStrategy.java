public class LimitReachedThresholdStrategy implements AlertThresholdStrategy {

    @Override
    public int getThresholdPercent() {
        return Constants.BUDGET_LIMIT_PERCENT;
    }
}
