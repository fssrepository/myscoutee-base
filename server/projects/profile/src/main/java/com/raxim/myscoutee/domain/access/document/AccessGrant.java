package com.raxim.myscoutee.domain.access.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accessGrants")
@CompoundIndexes({
        @CompoundIndex(name = "access_grant_subject_permission_resource", def = "{'subjectUserId': 1, 'permission': 1, 'resourceType': 1, 'resourceId': 1}"),
        @CompoundIndex(name = "access_grant_resource_permission", def = "{'resourceType': 1, 'resourceId': 1, 'permission': 1}")
})
@lombok.Getter @lombok.Setter
public class AccessGrant {
    @Id
    private String id;

    @Indexed(name = "access_grant_sync_key", unique = true, sparse = true)
    private String syncKey;

    private String subjectUserId;
    private String resourceType;
    private String resourceId;
    private String permission;
    private String effect;
    private String expiresAtIso;
    private String createdByUserId;
    private String createdDate;
    private String updatedDate;
    private String updatedUser;

}
