import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class CloudStorage {

  private static final double MIN_BASE_PRICE = 100.0;
  private static final String DEFAULT_TARIFF = "100 ГБ";

  private final String name;
  private final double basePrice;
  private final String tariff;

  private BackupOption appliedOption = null;
  private int backupFrequency = 0;

  protected CloudStorage(String name, double basePrice) {
    this(name, basePrice, DEFAULT_TARIFF);
  }

  protected CloudStorage(String name, double basePrice, String tariff) {
    if (name == null || name.isBlank()) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Название не указано. Установлено «Хранилище».");
      this.name = "Хранилище";
    } else {
      this.name = name;
    }

    if (!Double.isFinite(basePrice) || basePrice <= 0) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Некорректная базовая цена. "
                      + "Установлено 100 рублей.");
      this.basePrice = MIN_BASE_PRICE;
    } else {
      this.basePrice = Math.max(0.01, roundMoney(basePrice));
    }

    this.tariff = tariff == null ? "" : tariff;
  }

  public abstract double calculateBillingPrice();

  // Общий расчет тарифа для обоих наследников.
  protected double calculateTariffPrice() {
    int multiplier = switch (tariff) {
      case "100 ГБ" -> 1;
      case "512 ГБ" -> 2;
      case "2 ТБ" -> 4;
      default -> {
        System.out.println(
                "[ПРЕДУПРЕЖДЕНИЕ] Неизвестный тариф «" + tariff
                        + "». Расчет выполнен по тарифу 100 ГБ.");
        yield 1;
      }
    };

    return roundMoney(basePrice * multiplier);
  }

  // Стоимость подписки без резервного копирования.
  public double calculateFinalPrice() {
    return calculateBillingPrice();
  }

  // Одно резервное копирование в день.
  public double calculateFinalPrice(BackupOption option) {
    return calculateFinalPrice(option, 1);
  }

  // Стоимость подписки и резервного копирования.
  public double calculateFinalPrice(BackupOption option, int frequency) {
    if (option == null) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Опция не указана. "
                      + "Возвращена стоимость без резервного копирования.");
      return calculateBillingPrice();
    }

    if (frequency < 1) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Частота должна быть больше нуля. "
                      + "Установлено одно копирование в день.");
      frequency = 1;
    }

    double finalPrice = roundMoney(
            calculateBillingPrice() + option.getPrice() * frequency);

    appliedOption = option;
    backupFrequency = frequency;

    return finalPrice;
  }

  public double enableBackup(BackupOption option) {
    return calculateFinalPrice(option);
  }

  public double enableBackup(BackupOption option, int frequency) {
    return calculateFinalPrice(option, frequency);
  }

  protected static double roundMoney(double amount) {
    if (!Double.isFinite(amount)) {
      throw new IllegalArgumentException(
              "Денежная сумма выходит за допустимый диапазон.");
    }

    return BigDecimal.valueOf(amount)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
  }

  public String getName() {
    return name;
  }

  public double getBasePrice() {
    return basePrice;
  }

  public String getTariff() {
    return tariff;
  }

  public BackupOption getAppliedOption() {
    return appliedOption;
  }

  public int getBackupFrequency() {
    return backupFrequency;
  }
}