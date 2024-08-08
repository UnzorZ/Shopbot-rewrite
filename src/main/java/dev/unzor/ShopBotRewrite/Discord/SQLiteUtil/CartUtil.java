package dev.unzor.ShopBotRewrite.Discord.SQLiteUtil;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Main;
import dev.unzor.ShopBotRewrite.Utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CartUtil {
    public static Map<String, Map<Item,Integer>> cart = new HashMap<>();
    static void createShoppingCart(String uid, Item item){
        Map<Item,Integer> items = new HashMap<>();
        items.put(item,1);
        cart.put(uid,items);
    }
    public static void addItemToCart(String uid, Item item){
        if(!cart.containsKey(uid)){
            createShoppingCart(uid, item);
        }else{
            Map<Item,Integer> items = cart.get(uid);
            if(items.containsKey(item)){
                items.replace(item,items.get(item)+1);
            }else{
                items.put(item,1);
            }
        }
    }
    public static void removeItemFromCart(String uid , Item item) {
        Map<Item, Integer> items = cart.get(uid);
        if (!cart.containsKey(uid)) {
            System.out.println("You don't have a shopping cart!");
        } else if (items.get(item) < 1) {

            items.remove(item);
        }else if (items.containsKey(item)) {
            items.replace(item, items.get(item) -1);
        }
    }
    public static Map<Item,Integer> getShoppingCart(String uid){
        return cart.get(uid);
    }

    public static Map<String,String> ticketMap = new HashMap<>();

    public static void createTicket(String UID){
        Map<Item, Integer> cart = getShoppingCart(UID);
        Guild guild = Main.jda.getGuildById(Constants.GUID);

        System.out.println("Creating ticket for " + UID);

        for (int i=0; i<cart.size(); i++)
            System.out.println(cart.get(i));

        assert guild != null;
        guild.retrieveMemberById(UID).queue(member -> {
            member.getUser().openPrivateChannel().queue(privateChannel -> {
                StringBuilder message = new StringBuilder();
                StringBuilder finalTicket = new StringBuilder();
                double total = 0;
                for(Map.Entry<Item ,Integer> entry : cart.entrySet()){
                    message.append(entry.getKey().getName()).append(" x ").append(entry.getValue()).append(" = ").append(entry.getKey().getPrice() * entry.getValue()).append(Constants.Currency).append("\n");
                    finalTicket.append(entry.getKey().getName()).append(" x ").append(entry.getValue()).append(" = ").append(entry.getKey().getPrice() * entry.getValue()).append(Constants.Currency).append("\n");
                    total += entry.getKey().getPrice() * entry.getValue();
                }
                message.append("The total is: ").append(new DecimalFormat("#.##").format(total)).append(Constants.Currency).append("\n");
                message.append("--------------------------------------------------------------");
                message.append("\n");
                message.append("\n");
                message.append("React with \uD83D\uDED2 to confirm the order and create a ticket \n React with ❌ to cancel the order");
                EmbedBuilder ticket = new EmbedBuilder();
                ticket.setTitle("Ticket for " + member.getEffectiveName());
                ticket.setColor(ColorUtils.getRandomColor());
                ticket.setDescription(message.toString());
                finalTicket.append("The total is: ").append(new DecimalFormat("#.##").format(total)).append(Constants.Currency).append("\n");
                finalTicket.append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                finalTicket.append("\n");
                finalTicket.append("\n");
                if(ticketMap.containsKey(UID)){
                    ticketMap.replace(UID, finalTicket.toString());
                } else {
                    ticketMap.put(UID, finalTicket.toString());
                }

                System.out.println("Ticket created for " + UID + ". Trying to delete the last message");

                // Obtener los mensajes recientes en lugar de usar getLatestMessageId()
                privateChannel.getHistory().retrievePast(1).queue(messages -> {
                    if (!messages.isEmpty()) {
                        Message lastMessage = messages.get(0);
                        if (lastMessage.getAuthor().isBot()) {
                            lastMessage.delete().queue(
                                    success -> System.out.println("Last message deleted successfully"),
                                    error -> System.out.println("Failed to delete last message: " + error.getMessage())
                            );
                        }
                    }
                }, error -> System.out.println("Failed to retrieve message history: " + error.getMessage()));

                System.out.println("Ticket created for " + UID + ". Sending the ticket");
                privateChannel.sendMessageEmbeds(ticket.build()).addActionRow(
                        Button.success("createticket", "Create ticket").withEmoji(Emoji.fromUnicode("U+2705")),
                        Button.danger("deletecart", "Delete Cart").withEmoji(Emoji.fromUnicode("U+1F4A3"))
                ).queue();
            });
        });
    }

    public static void deleteCart(User user){
        cart.remove(user.getId());
    }
    public static double getTotal(User user){
        Map<Item,Integer> cart = getShoppingCart(user.getId());
        double total = 0;
        for(Map.Entry<Item ,Integer> entry : cart.entrySet()){
            total = total + (entry.getKey().getPrice() * entry.getValue());
        }
        return total;
    }
}
