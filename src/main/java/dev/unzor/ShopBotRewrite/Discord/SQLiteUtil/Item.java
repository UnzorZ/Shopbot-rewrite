package dev.unzor.ShopBotRewrite.Discord.SQLiteUtil;

public class Item {
    private String name;
    private double price;
    private String msgId;
    private String channelId;
    private int quantity;
    public Item(String name, double price , String msgId, String channelId, int quantity){
        this.name = name;
        this.price = price;
        this.msgId = msgId;
        this.channelId = channelId;
        this.quantity = quantity;
    }

    public String getName(){
        return name;
    }
    public String getMsgId(){
        return msgId;
    }
    public String getChannelId(){
        return channelId;
    }
    public double getPrice(){
        return price;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPrice(Double price){
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
