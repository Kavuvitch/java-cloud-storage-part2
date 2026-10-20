public class UserSubscription extends CloudStorage {

    public UserSubscription(String name, double basePrice) {
        super(name, basePrice);
    }

    public UserSubscription(String name, double basePrice, String tariff) {
        super(name, basePrice, tariff);
    }

    @Override
    public double calculateBillingPrice() {
        return calculateTariffPrice();
    }
}