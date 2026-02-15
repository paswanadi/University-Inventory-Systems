package Managers;

import models.Equipment;
import models.StaffMember;
import exceptions.InventoryException;

public class InventoryManager {

    // Inventory stored as an array (simple + matches your assignment style)
    private Equipment[] inventory;
    private int inventoryCount;

    public InventoryManager(int maxItems) {
        this.inventory = new Equipment[maxItems];
        this.inventoryCount = 0;
    }

    // Optional helper: add equipment into inventory
    public void addEquipmentToInventory(Equipment equipment) {
        if (equipment == null) return;
        if (inventoryCount < inventory.length) {
            inventory[inventoryCount] = equipment;
            inventoryCount++;
        }
    }

    // ---------------- TASK 4 REQUIRED METHODS ----------------

    // assignEquipment: if-else checks availability + assignment limit
    public void assignEquipment(StaffMember staff, Equipment equipment) throws InventoryException {
        validateAssignment(staff, equipment);

        // If validation passed, assign
        staff.addAssignedEquipment(equipment);
        equipment.setAvailable(false);
    }

    // returnEquipment: validate return and update availability
    public void returnEquipment(StaffMember staff, String assetId) throws InventoryException {
        if (staff == null) {
            throw new InventoryException.StaffMemberNotFoundException("Staff member not found.");
        }
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new InventoryException("Asset ID cannot be empty.");
        }

        // Find the equipment inside staff assignments first (so we can set it available again)
        Equipment found = null;
        for (Equipment eq : staff.getAssignedEquipment()) {
            if (eq != null && assetId.equals(eq.getAssetId())) {
                found = eq;
                break;
            }
        }

        if (found == null) {
            throw new InventoryException("This staff member does not have assetId: " + assetId);
        }

        // Remove from staff + mark available
        staff.removeAssignedEquipment(assetId);
        found.setAvailable(true);
    }

    // calculateMaintenanceFee: MUST use switch/if by category
    public double calculateMaintenanceFee(Equipment equipment, int daysOverdue) {
        if (equipment == null || daysOverdue <= 0) return 0.0;

        String category = equipment.getCategory();
        if (category == null) category = "OTHER";

        double ratePerDay;

        // Switch statement (required)
        switch (category.trim().toUpperCase()) {
            case "IT":
                ratePerDay = 5.0;
                break;
            case "LAB":
                ratePerDay = 12.0;
                break;
            case "AV":
                ratePerDay = 8.0;
                break;
            default:
                ratePerDay = 3.0;
                break;
        }

        return ratePerDay * daysOverdue;
    }

    // Overloaded search #1: by name
    public Equipment[] searchEquipment(String name) {
        if (name == null) name = "";
        String target = name.trim().toLowerCase();

        Equipment[] results = new Equipment[inventoryCount];
        int count = 0;

        for (int i = 0; i < inventoryCount; i++) {
            Equipment eq = inventory[i];
            if (eq != null && eq.getName() != null &&
                    eq.getName().toLowerCase().contains(target)) {
                results[count++] = eq;
            }
        }

        return trimResults(results, count);
    }

    // Overloaded search #2: by category (+ optional availableOnly)
    public Equipment[] searchEquipment(String category, boolean availableOnly) {
        if (category == null) category = "";
        String target = category.trim().toLowerCase();

        Equipment[] results = new Equipment[inventoryCount];
        int count = 0;

        for (int i = 0; i < inventoryCount; i++) {
            Equipment eq = inventory[i];
            if (eq == null) continue;

            boolean categoryMatch = (eq.getCategory() != null) &&
                    eq.getCategory().toLowerCase().contains(target);

            boolean availabilityMatch = !availableOnly || eq.isAvailable();

            if (categoryMatch && availabilityMatch) {
                results[count++] = eq;
            }
        }

        return trimResults(results, count);
    }

    // Overloaded search #3: by warranty range
    public Equipment[] searchEquipment(int minWarranty, int maxWarranty) {
        Equipment[] results = new Equipment[inventoryCount];
        int count = 0;

        for (int i = 0; i < inventoryCount; i++) {
            Equipment eq = inventory[i];
            if (eq == null) continue;

            int w = eq.getWarrantyMonths();
            if (w >= minWarranty && w <= maxWarranty) {
                results[count++] = eq;
            }
        }

        return trimResults(results, count);
    }

    // validateAssignment: nested if-else validation
    public void validateAssignment(StaffMember staff, Equipment equipment) throws InventoryException {
        if (staff == null) {
            throw new InventoryException.StaffMemberNotFoundException("Staff member not found.");
        } else {
            if (equipment == null) {
                throw new InventoryException("Equipment cannot be null.");
            } else {
                // Nested validation logic
                if (!equipment.isAvailable()) {
                    throw new InventoryException.EquipmentNotAvailableException(
                            "Equipment is not available: " + equipment.getAssetId()
                    );
                } else {
                    if (staff.getAssignedEquipmentCount() >= 5) {
                        throw new InventoryException.AssignmentLimitExceededException(
                                "Assignment limit exceeded. Max 5 items allowed."
                        );
                    }
                }
            }
        }
    }

    // ---------------- HELPER ----------------
    private Equipment[] trimResults(Equipment[] arr, int size) {
        Equipment[] trimmed = new Equipment[size];
        for (int i = 0; i < size; i++) {
            trimmed[i] = arr[i];
        }
        return trimmed;
    }
}
