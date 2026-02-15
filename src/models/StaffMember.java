
package models;

public class StaffMember {
    private int staffId;
    private String name;
    private String email;
    private Equipment[] assignedEquipment; // max 5

    public StaffMember(int staffId, String name, String email) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.assignedEquipment = new Equipment[5]; // empty array
    }

    // Getters and setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Equipment[] getAssignedEquipment() {
        return assignedEquipment;
    }

    public void setAssignedEquipment(Equipment[] assignedEquipment) {
        // Keep it simple for Task 1 (later we can improve this)
        this.assignedEquipment = assignedEquipment;
    }

    // Adds equipment to the assignedEquipment array (first empty slot)
    public void addAssignedEquipment(Equipment equipment) {
        if (equipment == null) return;

        // Prevent duplicates by assetId
        for (Equipment eq : assignedEquipment) {
            if (eq != null && eq.equals(equipment)) {
                return; // already assigned
            }
        }

        // Find an empty slot
        for (int i = 0; i < assignedEquipment.length; i++) {
            if (assignedEquipment[i] == null) {
                assignedEquipment[i] = equipment;
                return;
            }
        }

        // If we reach here, array is full (max 5)
        // For Task 1 we just stop. Later Task 3 you’ll throw AssignmentLimitExceededException here.
    }

    // Removes equipment by assetId and shifts array left to keep it tidy
    public void removeAssignedEquipment(String assetId) {
        if (assetId == null) return;

        for (int i = 0; i < assignedEquipment.length; i++) {
            if (assignedEquipment[i] != null && assetId.equals(assignedEquipment[i].getAssetId())) {
                assignedEquipment[i] = null;

                // shift left so no gaps in the middle
                for (int j = i; j < assignedEquipment.length - 1; j++) {
                    assignedEquipment[j] = assignedEquipment[j + 1];
                    assignedEquipment[j + 1] = null;
                }
                return;
            }
        }
    }

    // Returns how many are currently assigned
    public int getAssignedEquipmentCount() {
        int count = 0;
        for (Equipment eq : assignedEquipment) {
            if (eq != null) count++;
        }
        return count;
    }
}
