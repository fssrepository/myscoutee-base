package com.raxim.myscoutee.domain.accounts.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "userSessionStates")
@lombok.Getter @lombok.Setter
public class UserSessionState {
    @MongoId(FieldType.STRING)
    private String id;

    private String userId;
    private List<ActiveSession> activeSessions = new ArrayList<>();
    private String createdDate;
    private String updatedDate;

    public void setActiveSessions(List<ActiveSession> activeSessions) {
        this.activeSessions = activeSessions == null ? new ArrayList<>() : new ArrayList<>(activeSessions);
    }

    @lombok.Getter @lombok.Setter
    public static class ActiveSession {
        private String sessionId;
        private String clientLabel;
        private String provider;
        private String createdAtIso;
        private String lastSeenAtIso;
        private String mediaTokenHash;

    }
}
