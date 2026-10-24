package com.raxim.myscoutee.domain.accounts.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userSessionActions")
@CompoundIndexes({
        @CompoundIndex(name = "user_session_action_user_created", def = "{'userId': 1, 'createdDate': -1}")
})
@lombok.Getter @lombok.Setter
public class UserSessionAction {
    @Id
    private String id;

    @Indexed(name = "user_session_action_sync_key", unique = true, sparse = true)
    private String syncKey;

    private String userId;
    private String action;
    private String metadata;
    private String createdDate;
    private String createdUser;
    private String updatedDate;
    private String updatedUser;

}
