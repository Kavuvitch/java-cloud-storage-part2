public class EmailNotification implements NotificationService {

    @Override
    public void sendNotification(String message, String recipient) {
        if (recipient == null
                || !recipient.contains("@")
                || !recipient.contains(".")) {
            System.out.println(
                    "[ПРЕДУПРЕЖДЕНИЕ] Некорректный email. Письмо не отправлено.");
        } else {
            System.out.println(
                    "[EMAIL] На адрес " + recipient + " отправлено письмо: " + message);
        }
    }
}