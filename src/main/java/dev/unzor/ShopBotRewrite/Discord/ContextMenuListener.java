package dev.unzor.ShopBotRewrite.Discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.List;

public class ContextMenuListener extends ListenerAdapter {
    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        if (event.getName().equals("Get user avatar")) {
            event.reply("Avatar: " + event.getTarget().getEffectiveAvatarUrl()).queue();
        }
    }

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        if (event.getName().equals("Disable item")) {
            event.getInteraction().getTarget().editMessageEmbeds(getEmbedFromMessage(event).build()).setActionRow(
                        Button.success("addtocart", "Add to cart").withEmoji(Emoji.fromUnicode("U+1F6D2")).asDisabled(),
                        Button.danger("removefromcart", "Remove from cart").withEmoji(Emoji.fromUnicode("U+26D4")).asDisabled())
                    .queue();
            event.reply("Disabling item").setEphemeral(true).queue();
        } else if (event.getName().equals("Enable item")) {
            event.getInteraction().getTarget().editMessageEmbeds(getEmbedFromMessage(event).build()).setActionRow(
                    Button.success("addtocart", "Add to cart").withEmoji(Emoji.fromUnicode("U+1F6D2")),
                    Button.danger("removefromcart", "Remove from cart").withEmoji(Emoji.fromUnicode("U+26D4")))
                    .queue();
            event.reply("Enabling item").setEphemeral(true).queue();
        } else if (event.getName().equals("Edit Quantity")) {
            EmbedBuilder embed = getEmbedFromMessage(event);

            TextInput embedId = TextInput.create("embedId", "Embed ID", TextInputStyle.SHORT)
                    .setPlaceholder("Enter embed ID")
                    .setMinLength(0)
                    .setMaxLength(50)
                    .build();

            TextInput quantity = TextInput.create("quantity", "Quantity", TextInputStyle.SHORT)
                    .setPlaceholder("Enter quantity")
                    .setMinLength(0)
                    .setMaxLength(10)
                    .build();

            Modal modal = Modal.create("editQuantity", "Edit Quantity")
                    .addComponents(ActionRow.of(embedId), ActionRow.of(quantity))
                    .build();

            event.getInteraction().getTarget().editMessageEmbeds(embed.build()).queue();
            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("editQuantity")) {
            event.getChannel().retrieveMessageById(event.getValue("embedId").getAsString()).queue(message -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(message.getEmbeds().get(0).getTitle());
                embed.setDescription(message.getEmbeds().get(0).getDescription());
                embed.setColor(message.getEmbeds().get(0).getColor());

                if (message.getEmbeds().get(0).getImage() != null) {
                    embed.setImage(message.getEmbeds().get(0).getImage().getUrl());
                }

                if (message.getEmbeds().get(0).getFooter() != null) {
                    embed.setFooter(message.getEmbeds().get(0).getFooter().getText(), message.getEmbeds().get(0).getFooter().getIconUrl());
                }

                if (message.getEmbeds().get(0).getThumbnail() != null) {
                    embed.setThumbnail(message.getEmbeds().get(0).getThumbnail().getUrl());
                }

                if (message.getEmbeds().get(0).getTimestamp() != null) {
                    embed.setTimestamp(message.getEmbeds().get(0).getTimestamp());
                }

                if (message.getEmbeds().get(0).getAuthor() != null) {
                    embed.setAuthor(message.getEmbeds().get(0).getAuthor().getName(), message.getEmbeds().get(0).getAuthor().getIconUrl(), message.getEmbeds().get(0).getAuthor().getUrl());
                }

                List<MessageEmbed.Field> fields = message.getEmbeds().get(0).getFields();
                for (MessageEmbed.Field field : fields) {
                    if (field.getName().equals("Quantity")) {
                        continue;
                    }
                    embed.addField(field.getName(), field.getValue(), true);
                }

                String quantity = event.getValue("quantity").getAsString();
                embed.addField("Quantity", quantity, true);
                message.editMessageEmbeds(embed.build()).queue(q -> event.reply("Modal submitted").setEphemeral(true).queue());

            });
        }
    }

    public EmbedBuilder getEmbedFromMessage(MessageContextInteractionEvent event) {
        MessageEmbed messageEmbed = event.getInteraction().getTarget().getEmbeds().get(0);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(messageEmbed.getTitle());
        embed.setDescription(messageEmbed.getDescription());
        embed.setColor(messageEmbed.getColor());

        if (messageEmbed.getImage() != null) {
            embed.setImage(messageEmbed.getImage().getUrl());
        }

        if (messageEmbed.getFooter() != null) {
            embed.setFooter(messageEmbed.getFooter().getText(), messageEmbed.getFooter().getIconUrl());
        }

        if (messageEmbed.getThumbnail() != null) {
            embed.setThumbnail(messageEmbed.getThumbnail().getUrl());
        }

        if (messageEmbed.getTimestamp() != null) {
            embed.setTimestamp(messageEmbed.getTimestamp());
        }

        if (messageEmbed.getAuthor() != null) {
            embed.setAuthor(messageEmbed.getAuthor().getName(), messageEmbed.getAuthor().getIconUrl(), messageEmbed.getAuthor().getUrl());
        }

        List<MessageEmbed.Field> fields = messageEmbed.getFields();
        if (fields != null) {
            for (MessageEmbed.Field field : fields) {
                embed.addField(field.getName(), field.getValue(), field.isInline());
            }
        }

        return embed;
    }
}
