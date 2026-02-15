package Exception;

// Base exception class
public class InventoryException extends Exception {
    public InventoryException(String message) {
        super(message);
    }

    // Equipment not available
    public static class EquipmentNotAvailableException extends InventoryException {
        public EquipmentNotAvailableException(String message) {
            super(message);
        }
    }

    // Staff member not found
    public static class StaffMemberNotFoundException extends InventoryException {
        public StaffMemberNotFoundException(String message) {
            super(message);
        }
    }

    // Assignment limit exceeded
    public static class AssignmentLimitExceededException extends InventoryException {
        public AssignmentLimitExceededException(String message) {
            super(message);
        }
    }
}
