package models;

import java.util.Objects;

public class Equipment extends InventoryItem {
    private String brand;
    private String assetId;
    private int warrantyMonths;

    public Equipment(String assetId, String name, boolean isAvailable, String brand, int warrantyMonths) {
        super(assetId, name, isAvailable); // id = assetId (simple and consistent)
        this.assetId = assetId;
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getItemType() {
        return "Equipment";
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
        setId(assetId); // keep base id in sync
    }

    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    @Override
    public String toString() {
        return "Equipment{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", isAvailable=" + isAvailable() +
                ", assetId='" + assetId + '\'' +
                ", brand='" + brand + '\'' +
                ", warrantyMonths=" + warrantyMonths +
                '}';
    }

    // compare equipment by assetId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        Equipment that = (Equipment) o;
        return Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId);
    }
}
