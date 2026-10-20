public class SmsNotification implements NotificationService {

    @Override
    public void sendNotification(String message, String recipient) {
        if (recipient == null) {
            System.out.println(
                    "[ПРЕДУПРЕЖДЕНИЕ] Номер телефона не указан. SMS не отправлено.");
            return;
        }

        boolean validNumber =
                (recipient.startsWith("+7") && recipient.matches("\\+7\\d{10}"))
                        || (recipient.startsWith("8") && recipient.matches("8\\d{10}"));

        if (validNumber) {
            System.out.println(
                    "[SMS] На номер " + recipient + " отправлено сообщение: " + message);
        } else {
            System.out.println(
                    "[ПРЕДУПРЕЖДЕНИЕ] Некорректный телефон. "
                            + "Используйте формат +79991234567 или 89991234567.");
        }
    }
}