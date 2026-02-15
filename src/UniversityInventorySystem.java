import java.util.Scanner;

import exceptions.InventoryException;
import managers.InventoryManager;
import managers.InventoryReports;
import models.*;

public class UniversityInventorySystem {

    // Arrays (matches assignment style)
    private static final int MAX_ITEMS = 100;
    private static final int MAX_STAFF = 50;

    private static InventoryItem[] items = new InventoryItem[MAX_ITEMS];
    private static int itemCount = 0;

    private static StaffMember[] staffMembers = new StaffMember[MAX_STAFF];
    private static int staffCount = 0;

    private static InventoryManager manager = new InventoryManager(MAX_ITEMS);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Preload 1 Furniture + 1 LabEquipment (polymorphism demonstration)
        addItemToSystem(new Furniture("F-001", "Office Chair", true, "B12", "Wood"));
        addItemToSystem(new LabEquipment("L-001", "Microscope", true, "Chem Lab", "2026-01-15"));

        boolean running = true;

        while (running) { // menu loop
            System.out.println("\n===== UNIVERSITY INVENTORY SYSTEM =====");
            System.out.println("1. Add new equipment");
            System.out.println("2. Register a new staff member");
            System.out.println("3. Assign equipment to staff");
            System.out.println("4. Return equipment");
            System.out.println("5. Search inventory");
            System.out.println("6. Generate reports");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");

            int choice = readInt(sc);

            try {
                switch (choice) {
                    case 1:
                        addNewEquipment(sc);
                        break;

                    case 2:
                        registerStaff(sc);
                        break;

                    case 3:
                        assignEquipmentMenu(sc);
                        break;

                    case 4:
                        returnEquipmentMenu(sc);
                        break;

                    case 5:
                        searchMenu(sc);
                        break;

                    case 6:
                        reportsMenu(sc);
                        break;

                    case 7:
                        running = false;
                        System.out.println("Exiting... Bye!");
                        break;

                    default:
                        System.out.println("Invalid option. Choose 1-7.");
                }
            } catch (InventoryException ie) {
                // Task 3 + Task 6 requirement: friendly messages
                System.out.println("Error: " + ie.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }

        sc.close();
    }

    // -------------------- MENU ACTIONS --------------------

    private static void addNewEquipment(Scanner sc) {
        System.out.print("Asset ID: ");
        String assetId = sc.nextLine().trim();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Brand: ");
        String brand = sc.nextLine().trim();

        System.out.print("Warranty months (0 means expired): ");
        int warrantyMonths = readInt(sc);

        System.out.print("Category (IT/LAB/AV/OTHER): ");
        String category = sc.nextLine().trim();

        Equipment eq = new Equipment(assetId, name, true, brand, warrantyMonths, category);

        // Add to system arrays + manager inventory
        addItemToSystem(eq);
        manager.addEquipmentToInventory(eq);

        System.out.println("Equipment added: " + eq);
    }

    private static void registerStaff(Scanner sc) {
        System.out.print("Staff ID (number): ");
        int staffId = readInt(sc);

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Department: ");
        String dept = sc.nextLine().trim();

        StaffMember s = new StaffMember(staffId, name, email);
        s.setDepartment(dept); // requires the small patch in StaffMember

        if (staffCount < staffMembers.length) {
            staffMembers[staffCount++] = s;
            System.out.println("Staff registered: " + s.getName() + " (" + s.getStaffId() + ")");
        } else {
            System.out.println("Staff list full. Cannot register more.");
        }
    }

    private static void assignEquipmentMenu(Scanner sc) throws InventoryException {
        System.out.print("Enter Staff ID: ");
        int staffId = readInt(sc);

        System.out.print("Enter Equipment Asset ID: ");
        String assetId = sc.nextLine().trim();

        StaffMember staff = findStaffById(staffId);
        if (staff == null) {
            throw new InventoryException.StaffMemberNotFoundException("Staff ID not found: " + staffId);
        }

        Equipment eq = findEquipmentByAssetId(assetId);
        if (eq == null) {
            throw new InventoryException("Equipment not found with assetId: " + assetId);
        }

        manager.assignEquipment(staff, eq);
        System.out.println("Assigned " + eq.getAssetId() + " to " + staff.getName());
    }

    private static void returnEquipmentMenu(Scanner sc) throws InventoryException {
        System.out.print("Enter Staff ID: ");
        int staffId = readInt(sc);

        System.out.print("Enter Equipment Asset ID to return: ");
        String assetId = sc.nextLine().trim();

        StaffMember staff = findStaffById(staffId);
        if (staff == null) {
            throw new InventoryException.StaffMemberNotFoundException("Staff ID not found: " + staffId);
        }

        manager.returnEquipment(staff, assetId);
        System.out.println("Returned equipment: " + assetId);
    }

    private static void searchMenu(Scanner sc) {
        System.out.println("\n--- SEARCH MENU ---");
        System.out.println("1. Search by name");
        System.out.println("2. Search by category (optional: available only)");
        System.out.println("3. Search by warranty range");
        System.out.print("Choose: ");

        int option = readInt(sc);

        Equipment[] results;

        switch (option) {
            case 1:
                System.out.print("Enter name keyword: ");
                String name = sc.nextLine().trim();
                results = manager.searchEquipment(name);
                printEquipmentResults(results);
                break;

            case 2:
                System.out.print("Enter category keyword: ");
                String cat = sc.nextLine().trim();
                System.out.print("Available only? (y/n): ");
                String yn = sc.nextLine().trim().toLowerCase();
                boolean availableOnly = yn.equals("y");
                results = manager.searchEquipment(cat, availableOnly);
                printEquipmentResults(results);
                break;

            case 3:
                System.out.print("Min warranty months: ");
                int minW = readInt(sc);
                System.out.print("Max warranty months: ");
                int maxW = readInt(sc);
                results = manager.searchEquipment(minW, maxW);
                printEquipmentResults(results);
                break;

            default:
                System.out.println("Invalid search option.");
        }
    }

    private static void reportsMenu(Scanner sc) {
        InventoryReports reports = new InventoryReports(items, itemCount, staffMembers, staffCount);

        System.out.println("\n--- REPORTS MENU ---");
        System.out.println("1. Inventory report");
        System.out.println("2. Expired warranties");
        System.out.println("3. Assignments by department");
        System.out.println("4. Utilisation rate");
        System.out.println("5. Maintenance schedule");
        System.out.print("Choose: ");

        int option = readInt(sc);

        switch (option) {
            case 1:
                reports.generateInventoryReport();
                break;
            case 2:
                reports.findExpiredWarranties();
                break;
            case 3:
                reports.displayAssignmentsByDepartment();
                break;
            case 4:
                reports.calculateUtilisationRate();
                break;
            case 5:
                reports.generateMaintenanceSchedule();
                break;
            default:
                System.out.println("Invalid report option.");
        }
    }

    // -------------------- HELPERS --------------------

    private static void addItemToSystem(InventoryItem item) {
        if (itemCount < items.length) {
            items[itemCount++] = item;
        }
    }

    private static StaffMember findStaffById(int staffId) {
        for (int i = 0; i < staffCount; i++) {
            if (staffMembers[i] != null && staffMembers[i].getStaffId() == staffId) {
                return staffMembers[i];
            }
        }
        return null;
    }

    private static Equipment findEquipmentByAssetId(String assetId) {
        for (int i = 0; i < itemCount; i++) {
            InventoryItem it = items[i];
            if (it instanceof Equipment) {
                Equipment eq = (Equipment) it;
                if (eq.getAssetId() != null && eq.getAssetId().equals(assetId)) {
                    return eq;
                }
            }
        }
        return null;
    }

    private static void printEquipmentResults(Equipment[] results) {
        if (results == null || results.length == 0) {
            System.out.println("No results found.");
            return;
        }
        System.out.println("\nResults:");
        for (Equipment e : results) {
            System.out.println(e);
        }
    }

    private static int readInt(Scanner sc) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}


