package dev.unzor.ShopBotRewrite.Discord;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Discord.Commands.*;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.CartUtil;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.ItemManager;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.TicketUtil;
import dev.unzor.ShopBotRewrite.Main;
import dev.unzor.ShopBotRewrite.Utils.EmbedUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping" -> PingCommand.ping(event);
            case "additem" -> AddItemCommand.addItem(event);
            case "creatediscount" -> CreateDiscountCommand.createDiscount(event);
            case "removediscount" -> RemoveDiscountCommand.removeDiscount(event);
            case "adddiscount" -> AddDiscountCommand.addDiscount(event);
            case "getdiscountlist" -> GetDiscuntListCommand.getDiscountList(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        Member member = event.getMember();
        switch (event.getButton().getId()) {
            case "addtocart" -> {
                CartUtil.addItemToCart(event.getMember().getId().toString(), ItemManager.getItemById(event.getMessage().getId()));
                CartUtil.createTicket(event.getMember().getId(), null);
                event.deferReply(true).queue(hook ->
                        hook.deleteOriginal().queue()
                );
            }
            case "removefromcart" -> {
                CartUtil.removeItemFromCart(event.getMember().getId().toString(), ItemManager.getItemById(event.getMessage().getId()));
                CartUtil.createTicket(event.getMember().getId(), null);
                event.deferReply(true).queue(hook ->
                        hook.deleteOriginal().queue()
                );
            }
            case "createticket" -> {
                TicketUtil.createTicket(event.getUser());
                EmbedUtil.log("Ticket created", "Ticket created for " + Main.jda.getUserById(event.getUser().getId()).getAsTag(), Color.GREEN);
                event.deferReply(true).queue(hook ->
                        hook.deleteOriginal().queue()
                );
            }
            case "markasap" -> { //There's a limitation in the discord api that only allows 2 changes per button
                TextChannel channel = event.getChannel().asTextChannel();
                Role roleObj = event.getGuild().getRoleById(Constants.ShopRole);

                if (!member.getRoles().stream().anyMatch(role -> role.getId().equals(Constants.ShopRole))) {
                    event.reply("You need to have the role " + roleObj.getAsMention() + " to use this action").setEphemeral(true).queue();
                    return;
                }

                if (channel.getName().startsWith("\uD83D\uDCEC")) {
                    channel.getManager().setName(channel.getName().replace("📬-", "")).queue();
                    event.reply("Unmarked as ASAP").setEphemeral(true).queue();
                    System.out.println(channel.getName());
                    return;
                }

                channel.getManager().setName("\uD83D\uDCEC-" + channel.getName()).queue();
                System.out.println(channel.getName());
                event.reply("Marked as ASAP").setEphemeral(true).queue();
            }
            case "done" -> {
                TextChannel channel = event.getChannel().asTextChannel();
                Role roleObj = event.getGuild().getRoleById(Constants.ShopRole);

                if (!member.getRoles().stream().anyMatch(role -> role.getId().equals(Constants.ShopRole))) {
                    event.reply("You need to have the role " + roleObj.getAsMention() + " to use this action").setEphemeral(true).queue();
                    return;
                }
                channel.getManager().setName("✅-" + channel.getName()).queue();
                EmbedUtil.log("Ticket marked as done", "Ticket marked as done for " + event.getUser().getName(), Color.GREEN);
                event.reply("Marked as done").setEphemeral(true).queue();
            }
            case "delete" -> {
                Role roleObj = event.getGuild().getRoleById(Constants.ShopRole);
                if (!member.getRoles().stream().anyMatch(role -> role.getId().equals(Constants.ShopRole))) {
                    event.reply("You need to have the role " + roleObj.getAsMention() + " to use this action").setEphemeral(true).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Delete");
                embed.setDescription("Are you sure you want to delete this ticket?");
                embed.setColor(Color.RED);
                event.replyEmbeds(embed.build()).addActionRow(
                        Button.danger("deleteticket", "Delete").withEmoji(Emoji.fromUnicode("U+1F4A3")),
                        Button.secondary("cancelticketdelete", "Cancel")
                ).queue(message -> message.deleteOriginal().queueAfter(20, TimeUnit.SECONDS));
            }

            case "deletecart" -> {
                EmbedUtil.log("Cart deleted", "Cart deleted for " + event.getUser().getName(), Color.RED);
                CartUtil.deleteCart(event.getUser());
                event.reply("Cart deleted").queue();
            }
            case "deleteticket" -> {
                EmbedUtil.log("Ticket deleted", "Ticket deleted for " + event.getUser().getName(), Color.RED);
                event.reply("The ticket will be deleted in 5 seconds")
                        .queue(e -> e.getInteraction().getChannel().delete().queueAfter(5, TimeUnit.SECONDS));
            }
            case "cancelticketdelete" -> {
                event.reply("Ticket deletion cancelled").setEphemeral(true).queue();
            }
        }

    }
}
