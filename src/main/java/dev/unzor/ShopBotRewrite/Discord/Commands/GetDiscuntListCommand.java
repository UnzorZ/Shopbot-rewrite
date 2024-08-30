package dev.unzor.ShopBotRewrite.Discord.Commands;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.Discount;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.SqlUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;

public class GetDiscuntListCommand {
    public static void getDiscountList(SlashCommandInteractionEvent event){
        if (!event.getMember().getRoles().stream().anyMatch(role -> role.getId().equals(Constants.ShopRole))) {
            event.reply("You need to have the role " + Constants.ShopRole + " to use this action").setEphemeral(true).queue();
            return;
        }

        ArrayList<Discount> discounts = SqlUtil.getDiscounts();

        StringBuilder message = new StringBuilder();
        for (Discount discount : discounts) {
            message.append(discount.getId()).append(" - ").append(discount.get()).append("%. ").append("Usage limit: ").append(discount.getUsageLimit()).append(". Used: ").append(discount.getCurrentUses()).append("\n");
        }

        event.reply(message.toString()).setEphemeral(true).queue();
    }
}
