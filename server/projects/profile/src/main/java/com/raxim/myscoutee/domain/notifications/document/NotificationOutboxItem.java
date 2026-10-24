package com.raxim.myscoutee.domain.notifications.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notificationOutbox")
@CompoundIndexes({
        @CompoundIndex(name = "notification_outbox_group_status_retry", def = "{'baseGroupId': 1, 'status': 1, 'nextRetryAt': 1, 'createdDate': 1}"),
        @CompoundIndex(name = "notification_outbox_status_retry", def = "{'status': 1, 'nextRetryAt': 1, 'createdDate': 1}"),
        @CompoundIndex(name = "notification_outbox_collapse_status", def = "{'collapseKey': 1, 'status': 1}"),
        @CompoundIndex(name = "notification_outbox_entity_created", def = "{'entityType': 1, 'entityId': 1, 'createdDate': -1}")
})
@lombok.Getter @lombok.Setter
public class NotificationOutboxItem implements com.raxim.myscoutee.domain.notifications.api.NotificationDelivery.Receipt {
    private String baseGroupId;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_SENT = "sent";
    public static final String STATUS_FAILED = "failed";

    @Id
    private String id;

    @Indexed(name = "notification_outbox_sync_key", unique = true, sparse = true)
    private String syncKey;

    private String notificationType;
    private String entityType;
    private String entityId;
    private String recipientScope;
    private List<String> recipientUserIds = new ArrayList<>();
    // Null identifies records queued before delivery decisions were snapshotted.
    private Boolean firebaseDeliveryRequested;
    // User -> device -> token fingerprint. Provider tokens stay in users.devices.
    private Map<String, Map<String, String>> firebaseDevices;
    private String deliveryStrategy;
    private String topicName;
    private String title;
    private String body;
    private String clickUrl;
    private String iconUrl;
    private String badgeUrl;
    private String tag;
    private Map<String, String> payload = new LinkedHashMap<>();
    private String payloadHash;
    private String collapseKey;
    private String status;
    private Boolean pendingCounterTracked;
    private Integer retryCount;
    private String nextRetryAt;
    private String lockedUntil;
    private String lastError;
    private Integer tokenSuccessCount;
    private Integer tokenFailureCount;
    private String startedDate;
    private String sentDate;
    private String createdDate;
    private String createdUser;
    private String updatedDate;
    private String updatedUser;

    public void setRecipientUserIds(List<String> recipientUserIds) {
        this.recipientUserIds = recipientUserIds == null ? new ArrayList<>() : new ArrayList<>(recipientUserIds);
    }

    public void setFirebaseDevices(Map<String, Map<String, String>> devices) {
        this.firebaseDevices = devices == null ? null : new LinkedHashMap<>(devices);
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload == null ? new LinkedHashMap<>() : new LinkedHashMap<>(payload);
    }

}
