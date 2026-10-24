package com.raxim.myscoutee.domain.access.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accessPolicyDefinitions")
@lombok.Getter @lombok.Setter
public class AccessPolicyDefinition {
    @Id
    private String id;

    @Indexed(name = "access_policy_definition_sync_key", unique = true, sparse = true)
    private String syncKey;

    @Indexed(name = "access_policy_definition_key", unique = true)
    private String policyKey;

    private String permission;
    private String resourceType;
    private List<String> required;
    private List<String> anyOf;
    private String status;
    private String description;
    private String createdDate;
    private String updatedDate;
    private String updatedUser;

}
