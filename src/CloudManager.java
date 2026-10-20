import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

public class CloudManager {

  private static int totalOrdersCount = 0;
  private static double totalRevenue = 0.0;

  private final NotificationService notificationService;

  public CloudManager(NotificationService notificationService) {
    this.notificationService = Objects.requireNonNull(
            notificationService, "Сервис уведомлений не должен быть null.");
  }

  public void processPayment(
          CloudStorage storage, String contact, double finalPrice) {

    if (storage == null) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Хранилище не указано. Оплата отменена.");
      return;
    }

    if (!Double.isFinite(finalPrice) || finalPrice <= 0) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Некорректная сумма. Оплата отменена.");
      return;
    }

    double paymentAmount = roundMoney(finalPrice);

    if (paymentAmount <= 0) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Сумма после округления равна нулю. "
                      + "Оплата отменена.");
      return;
    }

    double newRevenue = BigDecimal.valueOf(totalRevenue)
            .add(BigDecimal.valueOf(paymentAmount))
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();

    if (!Double.isFinite(newRevenue)
            || totalOrdersCount == Integer.MAX_VALUE) {
      System.out.println(
              "[ПРЕДУПРЕЖДЕНИЕ] Превышен предел статистики. Оплата отменена.");
      return;
    }

    totalRevenue = newRevenue;
    totalOrdersCount++;

    System.out.printf(
            Locale.ROOT,
            "Оплата хранилища «%s»: %.2f руб.%n",
            storage.getName(),
            paymentAmount);

    String message = String.format(
            Locale.ROOT,
            "Оплата хранилища «%s» получена. Сумма: %.2f руб.",
            storage.getName(),
            paymentAmount);

    notificationService.sendNotification(message, contact);
  }

  private static double roundMoney(double amount) {
    return BigDecimal.valueOf(amount)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
  }

  public static int getTotalOrdersCount() {
    return totalOrdersCount;
  }

  public static double getTotalRevenue() {
    return totalRevenue;
  }

  public static void printGlobalStats() {
    System.out.println("\n=== Общая статистика ===");
    System.out.println("Оплаченных заказов: " + totalOrdersCount);

    System.out.printf(
            Locale.ROOT,
            "Суммарная выручка: %.2f руб.%n",
            totalRevenue);
  }

  public static void main(String[] args) {
    System.out.println("Вариант 17. Облачное хранилище. Часть 2.");
    System.out.println("Расчет стоимости за один день.");

    BackupOption backup =
            new BackupOption("Резервное копирование", 10.0);

    // Пользователь: уведомление по SMS.
    CloudManager smsManager =
            new CloudManager(new SmsNotification());

    CloudStorage user =
            new UserSubscription("Личные файлы", 100.0, "512 ГБ");

    System.out.println("\n=== Обычный пользователь ===");

    double userPrice = user.enableBackup(backup, 2);

    smsManager.processPayment(user, "+79991234567", userPrice);

    // Компания: уведомление по Email.
    CloudManager emailManager =
            new CloudManager(new EmailNotification());

    CloudStorage company =
            new CorporateSubscription("Архив компании", 100.0, "2 ТБ", 5);

    System.out.println("\n=== Корпоративный клиент ===");

    double companyPrice = company.enableBackup(backup, 3);

    emailManager.processPayment(
            company, "company@example.com", companyPrice);

    // Проверки некорректных значений без проведения новых оплат.
    System.out.println("\n=== Проверки входных данных ===");

    CloudStorage invalidUser =
            new UserSubscription("Проверка цены и тарифа", -50.0, "Неизвестный");

    System.out.printf(
            Locale.ROOT,
            "Стоимость после исправления: %.2f руб.%n",
            invalidUser.calculateBillingPrice());

    CorporateSubscription invalidCompany =
            new CorporateSubscription("Проверка лицензий", 100.0, "100 ГБ", 0);

    System.out.println(
            "Количество лицензий после исправления: "
                    + invalidCompany.getActiveLicensesCount());

    System.out.printf(
            Locale.ROOT,
            "Стоимость при неверной частоте бэкапа: %.2f руб.%n",
            invalidCompany.enableBackup(backup, 0));

    NotificationService emailService = new EmailNotification();
    emailService.sendNotification("Проверка email", "wrong-email");

    NotificationService smsService = new SmsNotification();
    smsService.sendNotification("Проверка телефона", "12345");

    printGlobalStats();
  }
}