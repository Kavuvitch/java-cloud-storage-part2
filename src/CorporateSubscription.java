public class CorporateSubscription extends CloudStorage {

    private final int activeLicensesCount;

    public CorporateSubscription(
            String name,
            double basePrice,
            String tariff,
            int activeLicensesCount) {

        super(name, basePrice, tariff);

        if (activeLicensesCount < 1) {
            System.out.println(
                    "[ПРЕДУПРЕЖДЕНИЕ] Количество лицензий меньше 1. "
                            + "Установлена одна лицензия.");
            this.activeLicensesCount = 1;
        } else {
            this.activeLicensesCount = activeLicensesCount;
        }
    }

    @Override
    public double calculateBillingPrice() {
        return roundMoney(calculateTariffPrice() * activeLicensesCount);
    }

    public int getActiveLicensesCount() {
        return activeLicensesCount;
    }
}