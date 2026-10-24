package com.raxim.myscoutee.domain.chat.document;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bson.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@org.springframework.data.mongodb.core.mapping.Document(collection = "chatMessages")
@CompoundIndexes({
        @CompoundIndex(name = "chat_message_lookup", def = "{'chatId': 1, 'id': 1}", unique = true),
        @CompoundIndex(name = "chat_message_page_desc", def = "{'chatId': 1, 'sentAtIso': -1, 'id': -1}"),
        @CompoundIndex(name = "chat_message_sender_date", def = "{'messageSenderUserIds': 1, 'sentAtIso': -1}"),
        @CompoundIndex(name = "chat_message_reader_date", def = "{'messageReadByUserIds': 1, 'sentAtIso': -1}"),
        @CompoundIndex(name = "chat_message_reaction_date", def = "{'messageReactionUserIds': 1, 'sentAtIso': -1}"),
        @CompoundIndex(name = "chat_message_attachment_date", def = "{'messageAttachmentKeys': 1, 'sentAtIso': -1}"),
        @CompoundIndex(name = "chat_poll_voter_date", def = "{'pollVoterUserIds': 1, 'sentAtIso': -1}"),
        @CompoundIndex(name = "chat_message_qmau_window", def = "{'sentAtIso': 1, 'senderAvatar.id': 1, 'deletedAtIso': 1}")
})
@lombok.Getter @lombok.Setter
public class ChatMessage {
    @MongoId(FieldType.STRING)
    private String mongoId;

    @Field("chatId")
    private String chatId;

    @Field("id")
    private String id;
    private String clientId;
    private String sender;
    private ChatAvatarRecord senderAvatar;
    private String text;
    private String time;
    private String sentAtIso;
    private Boolean mine;
    private List<ChatReadAvatarRecord> readBy = new ArrayList<>();
    private String deletedAtIso;
    private String deletedByUserId;
    private String deletedByName;
    private String editedAtIso;
    private String pinnedAtIso;
    private String pinnedByUserId;
    private ChatMessageReplyRecord replyTo;
    private List<ChatMessageReactionRecord> reactions = new ArrayList<>();
    private List<ChatMessageAttachmentRecord> attachments = new ArrayList<>();

    private List<String> messageSenderUserIds = new ArrayList<>();
    private List<String> messageReadByUserIds = new ArrayList<>();
    private List<String> messageReactionUserIds = new ArrayList<>();
    private List<String> messageAttachmentKeys = new ArrayList<>();
    private List<String> pollVoterUserIds = new ArrayList<>();

    public void setReadBy(List<ChatReadAvatarRecord> readBy) {
        this.readBy = readBy == null ? new ArrayList<>() : readBy;
    }

    public void setReactions(List<ChatMessageReactionRecord> reactions) {
        this.reactions = reactions == null ? new ArrayList<>() : reactions;
    }

    public void setAttachments(List<ChatMessageAttachmentRecord> attachments) {
        this.attachments = attachments == null ? new ArrayList<>() : attachments;
    }

    public void setMessageSenderUserIds(List<String> messageSenderUserIds) {
        this.messageSenderUserIds = messageSenderUserIds == null ? new ArrayList<>() : new ArrayList<>(messageSenderUserIds);
    }

    public void setMessageReadByUserIds(List<String> messageReadByUserIds) {
        this.messageReadByUserIds = messageReadByUserIds == null ? new ArrayList<>() : new ArrayList<>(messageReadByUserIds);
    }

    public void setMessageReactionUserIds(List<String> messageReactionUserIds) {
        this.messageReactionUserIds = messageReactionUserIds == null ? new ArrayList<>() : new ArrayList<>(messageReactionUserIds);
    }

    public void setMessageAttachmentKeys(List<String> messageAttachmentKeys) {
        this.messageAttachmentKeys = messageAttachmentKeys == null ? new ArrayList<>() : new ArrayList<>(messageAttachmentKeys);
    }

    public void setPollVoterUserIds(List<String> pollVoterUserIds) {
        this.pollVoterUserIds = pollVoterUserIds == null ? new ArrayList<>() : new ArrayList<>(pollVoterUserIds);
    }

    public void refreshMessageSearchFields() {
        Set<String> senderUserIds = new LinkedHashSet<>();
        Set<String> readByUserIds = new LinkedHashSet<>();
        Set<String> reactionUserIds = new LinkedHashSet<>();
        Set<String> attachmentKeys = new LinkedHashSet<>();
        Set<String> pollVoterIds = new LinkedHashSet<>();

        ChatAvatarRecord senderAvatar = getSenderAvatar();
        addNormalized(senderUserIds, senderAvatar == null ? null : senderAvatar.getId());
        for (ChatReadAvatarRecord reader : getReadBy() == null ? List.<ChatReadAvatarRecord>of() : getReadBy()) {
            addNormalized(readByUserIds, reader == null ? null : reader.getId());
        }
        for (ChatMessageReactionRecord reaction : getReactions() == null ? List.<ChatMessageReactionRecord>of() : getReactions()) {
            addNormalized(reactionUserIds, reaction == null ? null : reaction.getUserId());
        }
        for (ChatMessageAttachmentRecord attachment : getAttachments() == null ? List.<ChatMessageAttachmentRecord>of() : getAttachments()) {
            if (attachment == null) {
                continue;
            }
            String attachmentKey = messageAttachmentKey(attachment.getType(), attachment.getEntityId());
            if (!attachmentKey.isBlank()) {
                attachmentKeys.add(attachmentKey);
            }
            if ("poll".equals(normalizeText(attachment.getType()).toLowerCase(Locale.ROOT))) {
                collectPollVoterUserIds(attachment.getDescription(), pollVoterIds);
            }
        }

        this.messageSenderUserIds = new ArrayList<>(senderUserIds);
        this.messageReadByUserIds = new ArrayList<>(readByUserIds);
        this.messageReactionUserIds = new ArrayList<>(reactionUserIds);
        this.messageAttachmentKeys = new ArrayList<>(attachmentKeys);
        this.pollVoterUserIds = new ArrayList<>(pollVoterIds);
    }

    public static String buildMongoId(String chatId, String messageId) {
        String normalizedChatId = normalizeText(chatId);
        String normalizedMessageId = normalizeText(messageId);
        if (normalizedMessageId.isBlank()) {
            normalizedMessageId = UUID.randomUUID().toString();
        }
        return "chat-message:" + normalizedChatId + ":" + normalizedMessageId;
    }

    public static String messageAttachmentKey(String type, String entityId) {
        String normalizedType = normalizeText(type).toLowerCase(Locale.ROOT);
        String normalizedEntityId = normalizeText(entityId);
        if (normalizedType.isBlank() || normalizedEntityId.isBlank()) {
            return "";
        }
        return normalizedType + ":" + normalizedEntityId;
    }

    private static void collectPollVoterUserIds(String description, Set<String> pollVoterIds) {
        String normalizedDescription = normalizeText(description);
        if (normalizedDescription.isBlank()) {
            return;
        }
        try {
            Object optionsValue = Document.parse(normalizedDescription).get("options");
            if (!(optionsValue instanceof List<?> optionNodes)) {
                return;
            }
            for (Object option : optionNodes) {
                if (!(option instanceof Document optionDocument)) {
                    continue;
                }
                Object votesValue = optionDocument.get("votes");
                if (!(votesValue instanceof List<?> voteNodes)) {
                    continue;
                }
                for (Object vote : voteNodes) {
                    if (vote instanceof Document voteDocument) {
                        addNormalized(pollVoterIds, Objects.toString(voteDocument.get("userId"), ""));
                    }
                }
            }
        } catch (Exception ignored) {
            // Poll descriptions are user-created JSON blobs; malformed values are ignored for search fields.
        }
    }

    private static void addNormalized(Set<String> values, String value) {
        String normalized = normalizeText(value);
        if (!normalized.isBlank()) {
            values.add(normalized);
        }
    }

    private static String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    @lombok.Getter @lombok.Setter
    public static class ChatMessageReplyRecord {
        @Field("id")
        private String id;
        private String sender;
        private String text;

    }

    @lombok.Getter @lombok.Setter
    public static class ChatMessageReactionRecord {
        private String emoji;
        private String userId;
        private String userName;
        private String userInitials;
        private String userGender;
        private String reactedAtIso;

    }

    @lombok.Getter @lombok.Setter
    public static class ChatMessageAttachmentRecord {
        private Long lifecycleRevision;
        @Field("id")
        private String id;
        private String type;
        private String title;
        private String entityId;
        private String assetType;
        private String ownerUserId;
        private String status;
        private String unavailableReason;
        private String subtitle;
        private String description;
        private String url;
        private String previewUrl;
        private String mimeType;
        private Long sizeBytes;

    }

    @lombok.Getter @lombok.Setter
    public static class ChatAvatarRecord {
        @Field("id")
        private String id;
        private String initials;
        private String gender;

    }

    @lombok.Getter @lombok.Setter
    public static class ChatReadAvatarRecord {
        @Field("id")
        private String id;
        private String initials;
        private String gender;

    }
}
