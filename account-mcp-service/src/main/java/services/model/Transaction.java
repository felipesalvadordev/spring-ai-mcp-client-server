package services.model;

public record Transaction(String id, String date, String type, String value, String accountId, String description) {
}
