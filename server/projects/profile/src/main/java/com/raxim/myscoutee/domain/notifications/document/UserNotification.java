package com.raxim.myscoutee.domain.notifications.document;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userNotifications")
@CompoundIndexes({
        @CompoundIndex(
                name = "user_notifications_recipient_read_created",
                def = "{'recipientUserId': 1, 'readAtIso': 1, 'createdDate': -1, '_id': -1}"),
        @CompoundIndex(
                name = "user_notifications_recipient_created",
                def = "{'recipientUserId': 1, 'createdDate': -1, '_id': -1}"),
        @CompoundIndex(
                name = "user_notifications_source_created",
                def = "{'sourceType': 1, 'sourceId': 1, 'createdDate': -1}"),
        @CompoundIndex(
                name = "user_notifications_recipient_kind_source",
                def = "{'recipientUserId': 1, 'kind': 1, 'sourceType': 1, 'sourceId': 1, 'readAtIso': 1}")
})
@lombok.Getter @lombok.Setter
public class UserNotification {
    @Id
    private String id;

    @Indexed(name = "user_notifications_sync_key", unique = true, sparse = true)
    private String syncKey;

    @Indexed(name = "user_notifications_aggregation_key", unique = true, sparse = true)
    private String aggregationKey;

    private String outboxId;
    private String recipientUserId;
    private String kind;
    private String category;
    private String title;
    private String message;
    private String senderUserId;
    private String actionPath;
    private String sourceType;
    private String sourceId;
    private Map<String, String> payload = new LinkedHashMap<>();
    private Integer occurrenceCount;
    private Long revision;
    private String readAtIso;
    private String createdDate;
    private String createdUser;
    private String updatedDate;
    private String updatedUser;

    public void setPayload(Map<String, String> payload) {
        this.payload = payload == null ? new LinkedHashMap<>() : new LinkedHashMap<>(payload);
    }

}
