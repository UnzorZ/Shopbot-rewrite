package dev.unzor.ShopBotRewrite.Discord.SQLiteUtil;

public class Discount {
    private final String id;
    private final int discountValue;
    private final int usageLimit;
    private final int currentUses;
    private final boolean isActive;

    public Discount(String id, int discountValue, int usageLimit, int currentUses, boolean isActive) {
        this.id = id;
        this.discountValue = discountValue;
        this.usageLimit = usageLimit;
        this.currentUses = currentUses;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public int get() {
        return discountValue;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public int getCurrentUses() {
        return currentUses;
    }

    public boolean isActive() {
        return isActive;
    }


}
