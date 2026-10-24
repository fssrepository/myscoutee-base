package com.raxim.myscoutee.domain.chat.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "chats")
@CompoundIndexes({
        @CompoundIndex(name = "chat_owner_public_id", def = "{'ownerUserId': 1, 'id': 1}", unique = true),
        @CompoundIndex(name = "chat_public_id_lookup", def = "{'id': 1}"),
        @CompoundIndex(name = "chat_owner_date", def = "{'ownerUserId': 1, 'dateIso': -1}"),
        @CompoundIndex(name = "chat_owner_channel_owner", def = "{'ownerUserId': 1, 'channelType': 1, 'ownerId': 1}"),
        @CompoundIndex(name = "chat_owner_channel_date", def = "{'ownerUserId': 1, 'channelType': 1, 'dateIso': -1, 'id': 1}"),
        @CompoundIndex(name = "chat_owner_channel_relevance", def = "{'ownerUserId': 1, 'channelType': 1, 'unread': -1, 'dateIso': -1, 'id': 1}"),
        @CompoundIndex(name = "chat_owner_support_status_date", def = "{'ownerUserId': 1, 'supportCase.status': 1, 'dateIso': -1, 'id': 1}")
})
@lombok.Getter @lombok.Setter
public class Chat implements com.raxim.myscoutee.domain.chat.api.ChatDirectory.Chat {
    @MongoId(FieldType.STRING)
    private String mongoId;

    @Field("id")
    private String chatId;

    @Indexed(name = "chat_owner_user")
    private String ownerUserId;
    private String supportBaseGroupId;

    private String avatar;
    private String title;
    private String lastMessage;
    private String lastSenderId;
    private List<String> memberIds = new ArrayList<>();
    private Integer unread;
    private String dateIso;
    private String contextStartAtIso;
    private String contextEndAtIso;
    private String channelType;
    private String ownerId;
    @Indexed(name = "chat_event")
    private String eventId;
    private String ownerStatus;
    private Long revision = 1L;
    private String subEventId;
    private String caseOfferId;

    private String groupId;
    private Double distanceKm;
    private Integer distanceMetersExact;
    private SupportCaseRecord supportCase;

    @Transient
    private List<ChatMessage> messages = new ArrayList<>();

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds == null ? new ArrayList<>() : memberIds;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages == null ? new ArrayList<>() : messages;
    }

    @lombok.Getter @lombok.Setter
    public static class SupportCaseRecord implements com.raxim.myscoutee.domain.chat.api.ChatDirectory.SupportCase {
        private String status;
        private SupportCaseAssigneeRecord assignee;
        private String updatedAtIso;

    }

    @lombok.Getter @lombok.Setter
    public static class SupportCaseAssigneeRecord {
        private String userId;
        private String name;
        private String initials;

    }

}
