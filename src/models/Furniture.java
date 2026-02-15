package models;

public class Furniture extends InventoryItem {
    private String roomNumber;
    private String material;

    public Furniture(String id, String name, boolean isAvailable, String roomNumber, String material) {
        super(id, name, isAvailable);
        this.roomNumber = roomNumber;
        this.material = material;
    }

    @Override
    public String getItemType() {
        return "Furniture";
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    @Override
    public String toString() {
        return "Furniture{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", isAvailable=" + isAvailable() +
                ", roomNumber='" + roomNumber + '\'' +
                ", material='" + material + '\'' +
                '}';
    }
}
