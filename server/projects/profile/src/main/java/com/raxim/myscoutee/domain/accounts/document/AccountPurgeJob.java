package com.raxim.myscoutee.domain.accounts.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accountPurgeJobs")
@CompoundIndexes({
        @CompoundIndex(name = "account_purge_status_retry", def = "{'status': 1, 'nextRetryAtIso': 1, 'lockedUntilIso': 1}"),
        @CompoundIndex(name = "account_purge_hash", def = "{'userHash': 1}")
})
@lombok.Getter @lombok.Setter
public class AccountPurgeJob {
    private String baseGroupId;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";

    @Id
    private String id;

    @Indexed(name = "account_purge_user_id", unique = true, sparse = true)
    private String userId;

    private String userHash;
    private String status;
    private String stage;
    private List<String> ownedEventIds = new ArrayList<>();
    private List<String> ownedAssetIds = new ArrayList<>();
    private Integer retryCount;
    private String nextRetryAtIso;
    private String lockedUntilIso;
    private String startedAtIso;
    private String completedAtIso;
    private String lastError;
    private String createdDate;
    private String updatedDate;

    public void setOwnedEventIds(List<String> ownedEventIds) {
        this.ownedEventIds = ownedEventIds == null ? new ArrayList<>() : new ArrayList<>(ownedEventIds);
    }
    public void setOwnedAssetIds(List<String> ownedAssetIds) {
        this.ownedAssetIds = ownedAssetIds == null ? new ArrayList<>() : new ArrayList<>(ownedAssetIds);
    }
}
