package dev.unzor.ShopBotRewrite.Discord.Commands;

import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.CartUtil;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.Discount;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.SqlUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AddDiscountCommand {
    public static void addDiscount(SlashCommandInteractionEvent event){

        String id = event.getOption("id").getAsString();

        Discount discount = SqlUtil.getDiscount(id);

        if (discount == null){
            event.reply("Discount not found").setEphemeral(true).queue();
            return;
        } else if (!discount.isActive()) {
            event.reply("Discount is not active").setEphemeral(true).queue();
            return;
        } else if (discount.getCurrentUses() >= discount.getUsageLimit()) {
            event.reply("Discount got its limit of uses").setEphemeral(true).queue();
            return;
        }

        CartUtil.createTicket(event.getUser().getId(), discount);
        event.reply("Discount added").setEphemeral(true).queue();
    }
}
