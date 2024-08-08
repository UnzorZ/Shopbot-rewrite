package dev.unzor.ShopBotRewrite.Discord.Commands;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Utils.EmbedUtil;
import dev.unzor.ShopBotRewrite.Utils.ColorUtils;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.Item;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.ItemManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class AddItemCommand {
    static double quantity;
    public static void addItem(SlashCommandInteraction event) {
        String name = event.getOption("name", OptionMapping::getAsString);
        double price = event.getOption("price", OptionMapping::getAsDouble);
        String imageurl = event.getOption("imageurl", OptionMapping::getAsString);
        String description = event.getOption("description", OptionMapping::getAsString);
        String color = event.getOption("color", OptionMapping::getAsString);

        try {
            quantity = event.getOption("quantity", OptionMapping::getAsDouble);
        } catch (NullPointerException exception) {
            System.out.println("No quantity number");
            quantity = 0;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(name);
        if (description!=null)
            embed.setDescription(description);

        embed.addField("Price", price + Constants.Currency, true);

        if ((int) quantity!=0)
            embed.addField("Quantity", String.valueOf((int) quantity), true);

        if (imageurl!=null)
            formatImageURL(imageurl, embed);

        if (color!=null)
            embed.setColor(getColor(color, embed, event));

        event.getChannel().sendMessageEmbeds(embed.build())
                .addActionRow(
                        Button.success("addtocart", "Add to cart").withEmoji(Emoji.fromUnicode("U+1F6D2")), // U+1F4E5
                        Button.danger("removefromcart", "Remove from cart").withEmoji(Emoji.fromUnicode("U+26D4")) // U+1F4E4
                )
                .queue(msg -> {
                    if (quantity>0)
                        ItemManager.addItem(new Item(name, price, msg.getId(), event.getChannel().getId(), (int) quantity));
                    else
                        ItemManager.addItem(new Item(name, price, msg.getId(), event.getChannel().getId(), -1));
                });

        event.reply("Item added!").setEphemeral(true).queue();

    }

    public static int getColor(String input, EmbedBuilder embed, SlashCommandInteraction event) {
        int color = 0;
        switch (input) {
            case "red" -> color = 15158332;
            case "green" -> color = 3066993;
            case "yellow" -> color = 16776960;
            case "purple" -> color = 10181046;
            case "black" -> color = 2303786;
            case "aqua" -> color = 1752220;
            case "dark_aqua" -> color = 1146986;
            case "blue" -> color = 3447003;
            case "dark_blue" -> color = 2123412;
            case "gold" -> color = 15844367;
            case "gray" -> color = 9807270;
            case "dark_gray" -> color = 9936031;
            case "dark_green" -> color = 2067276;
            case "light_gray" -> color = 12370112;
            case "dark_purple" -> color = 7419530;
            case "dark_red" -> color = 10038562;
            case "white" -> color = 16777215;
            case "random" -> embed.setColor(ColorUtils.getRandomColor());
            default -> {
                event.replyEmbeds(EmbedUtil.errorEmbed("Invalid color").build()).setEphemeral(true).queue();
                return color;
            }
        }
        return color;
    }

    public static EmbedBuilder formatImageURL(String imageurl, EmbedBuilder embed) {
        if (imageurl.contains("imgur.com")) {
            if (imageurl.endsWith(".png") || imageurl.endsWith(".jpg") || imageurl.endsWith(".jpeg"))
                return embed.setImage(imageurl);
            else
                return embed.setImage(imageurl + ".png");
        }
        else {
            return embed.setImage(imageurl);
        }
    }

}
