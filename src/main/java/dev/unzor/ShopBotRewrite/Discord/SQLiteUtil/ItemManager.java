package dev.unzor.ShopBotRewrite.Discord.SQLiteUtil;

import java.util.ArrayList;

public class ItemManager {
    private static final ArrayList<Item> itemList = new ArrayList<>();
    public static void addItem(Item item){
        if(!itemList.contains(item)){
            itemList.add(item);
            SqlUtil.addItem(item);
        }
    }
    public static void delItem(Item item){
        itemList.remove(item);
        SqlUtil.delItem(item.getMsgId());
    }

    public static Item getItemById(String id){
        Item ret = null;
        for(Item i : itemList){
            if(i.getMsgId().equalsIgnoreCase(id)){
                ret = i;
            }
        }
        return ret;
    }
    public static void loadItem(Item item){
        if(!itemList.contains(item)){
            itemList.add(item);
        }
    }
    public static ArrayList<Item> getItemList(){
        return itemList;
    }
}
