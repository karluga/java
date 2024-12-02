// Utility Class for Date Formatting
// Uses DRY
class DateFormatter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String formatDate(LocalDate date) {
        return date.format(formatter);
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, formatter);
    }
}