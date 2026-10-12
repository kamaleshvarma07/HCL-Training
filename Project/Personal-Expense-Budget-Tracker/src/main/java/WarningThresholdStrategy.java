public class WarningThresholdStrategy implements AlertThresholdStrategy {

    @Override
    public int getThresholdPercent() {
        return Constants.BUDGET_WARNING_PERCENT;
    }
}
