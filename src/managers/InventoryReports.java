package Managers;

import models.Equipment;
import models.InventoryItem;
import models.StaffMember;

public class InventoryReports {

    private InventoryItem[] items;
    private int itemCount;

    private StaffMember[] staffMembers;
    private int staffCount;

    public InventoryReports(InventoryItem[] items, int itemCount,
                            StaffMember[] staffMembers, int staffCount) {
        this.items = items;
        this.itemCount = itemCount;
        this.staffMembers = staffMembers;
        this.staffCount = staffCount;
    }

    // 1) FOR loop: display all items with status
    public void generateInventoryReport() {
        System.out.println("\n--- INVENTORY REPORT ---");

        for (int i = 0; i < itemCount; i++) { // FOR LOOP (required)
            InventoryItem item = items[i];
            if (item == null) continue;

            String status = item.isAvailable() ? "AVAILABLE" : "ASSIGNED";
            System.out.println(item.getItemType() + " | " + item.getId() + " | " + item.getName() + " | " + status);
        }
    }

    // 2) WHILE loop: show equipment with expired warranties (warrantyMonths == 0)
    public void findExpiredWarranties() {
        System.out.println("\n--- EXPIRED WARRANTIES (warrantyMonths == 0) ---");

        int i = 0;
        boolean foundAny = false;

        while (i < itemCount) { // WHILE LOOP (required)
            InventoryItem item = items[i];

            if (item instanceof Equipment) {
                Equipment eq = (Equipment) item;
                if (eq.getWarrantyMonths() == 0) {
                    System.out.println(eq);
                    foundAny = true;
                }
            }

            i++;
        }

        if (!foundAny) {
            System.out.println("No expired warranties found.");
        }
    }

    // 3) FOREACH loop: group assignments by department
    // NOTE: StaffMember must have getDepartment() for this to work (see quick patch below)
    public void displayAssignmentsByDepartment() {
        System.out.println("\n--- ASSIGNMENTS BY DEPARTMENT ---");

        // Step A: collect unique departments (using foreach)
        String[] departments = new String[staffCount];
        int deptCount = 0;

        for (StaffMember s : staffMembers) { // FOREACH LOOP (required)
            if (s == null) continue;

            String dept = safe(s.getDepartment());
            if (!contains(departments, deptCount, dept)) {
                departments[deptCount++] = dept;
            }
        }

        // Step B: print grouped assignments (also uses foreach)
        for (int d = 0; d < deptCount; d++) {
            String dept = departments[d];
            System.out.println("\nDepartment: " + dept);

            for (StaffMember s : staffMembers) { // FOREACH LOOP
                if (s == null) continue;
                if (!safe(s.getDepartment()).equals(dept)) continue;

                System.out.println("  Staff: " + s.getName() + " (" + s.getStaffId() + ")");

                Equipment[] assigned = s.getAssignedEquipment();
                boolean any = false;

                for (Equipment eq : assigned) { // FOREACH LOOP
                    if (eq != null) {
                        System.out.println("    - " + eq.getAssetId() + " | " + eq.getName() + " | " + eq.getCategory());
                        any = true;
                    }
                }

                if (!any) System.out.println("    (no equipment assigned)");
            }
        }
    }

    // 4) NESTED loops: utilisation stats
    // Here: utilisation = assigned / total per category
    public void calculateUtilisationRate() {
        System.out.println("\n--- UTILISATION RATE (by category) ---");

        String[] categories = {"IT", "LAB", "AV", "OTHER"};

        for (int c = 0; c < categories.length; c++) { // outer loop
            String cat = categories[c];
            int total = 0;
            int assigned = 0;

            for (int i = 0; i < itemCount; i++) { // inner loop (NESTED LOOPS required)
                InventoryItem item = items[i];
                if (!(item instanceof Equipment)) continue;

                Equipment eq = (Equipment) item;
                String eqCat = safe(eq.getCategory()).toUpperCase();
                if (!eqCat.equals(cat)) continue;

                total++;
                if (!eq.isAvailable()) assigned++;
            }

            if (total == 0) {
                System.out.println(cat + ": No items");
            } else {
                double rate = (assigned * 100.0) / total;
                System.out.printf("%s: %d/%d assigned (%.2f%%)%n", cat, assigned, total, rate);
            }
        }
    }

    // 5) DO-WHILE loop: create a simple maintenance schedule
    // Reasonable approach: schedule items with expired warranty first, then LAB category
    public void generateMaintenanceSchedule() {
        System.out.println("\n--- MAINTENANCE SCHEDULE ---");

        int day = 1;
        int i = 0;

        do { // DO-WHILE LOOP (required)
            if (i >= itemCount) break;

            InventoryItem item = items[i];

            if (item instanceof Equipment) {
                Equipment eq = (Equipment) item;

                boolean needsMaintenance =
                        (eq.getWarrantyMonths() == 0) ||
                        safe(eq.getCategory()).equalsIgnoreCase("LAB");

                if (needsMaintenance) {
                    System.out.println("Day " + day + ": " + eq.getAssetId() + " | " + eq.getName()
                            + " | category=" + eq.getCategory() + " | warranty=" + eq.getWarrantyMonths());
                    day++;
                }
            }

            i++;
        } while (i < itemCount);

        if (day == 1) {
            System.out.println("No maintenance items scheduled.");
        }
    }

    // ----------------- helpers -----------------
    private String safe(String s) {
        if (s == null || s.trim().isEmpty()) return "Unknown";
        return s.trim();
    }

    private boolean contains(String[] arr, int size, String value) {
        for (int i = 0; i < size; i++) {
            if (arr[i] != null && arr[i].equals(value)) return true;
        }
        return false;
    }
}
