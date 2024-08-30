package dev.unzor.ShopBotRewrite.Discord.Commands;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.Discount;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.SqlUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class RemoveDiscountCommand {
    public static void removeDiscount(SlashCommandInteractionEvent event) {
        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(Constants.ShopRole))) {
            event.reply("You do not have the shop role").setEphemeral(true).queue();
            return;
        }

        String id = event.getOption("id", OptionMapping::getAsString);


        SqlUtil.removeDiscount(id);
        event.reply("Discount removed!").setEphemeral(true).queue();
    }
}
