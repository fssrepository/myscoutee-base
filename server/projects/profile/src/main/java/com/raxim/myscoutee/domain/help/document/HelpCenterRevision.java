package com.raxim.myscoutee.domain.help.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "helpCenterRevisions")
@CompoundIndexes({
        @CompoundIndex(name = "help_revision_system_context", def = "{'documentType': 1, 'contextKey': 1, 'isSystem': 1}"),
        @CompoundIndex(name = "help_revision_group_document_lang_context_version", def = "{'baseGroupId': 1, 'documentType': 1, 'lang': 1, 'contextKey': 1, 'version': -1}", unique = true),
        @CompoundIndex(name = "help_revision_group_document_lang_context_active", def = "{'baseGroupId': 1, 'documentType': 1, 'lang': 1, 'contextKey': 1, 'active': 1, 'version': -1}")
})
public class HelpCenterRevision {
    @Id
    @lombok.Getter @lombok.Setter
    private String id;
    @lombok.Getter @lombok.Setter
    private String baseGroupId;

    @Indexed(name = "help_revision_document_type")
    @lombok.Getter @lombok.Setter
    private String documentType;

    @Indexed(name = "help_revision_lang")
    @lombok.Getter @lombok.Setter
    private String lang;

    @Indexed(name = "help_revision_context_key")
    @lombok.Getter @lombok.Setter
    private String contextKey;

    @Field("isSystem")
    private boolean isSystem;
    public boolean getIsSystem() { return isSystem; }
    public void setIsSystem(boolean value) { isSystem = value; }

    @lombok.Getter @lombok.Setter
    private String languageLabel;
    @lombok.Getter @lombok.Setter
    private Integer version;

    @Indexed(name = "help_revision_active")
    @lombok.Getter @lombok.Setter
    private Boolean active;

    @lombok.Getter @lombok.Setter
    private String title;
    @lombok.Getter @lombok.Setter
    private String summary;
    @lombok.Getter @lombok.Setter
    private String description;
    @lombok.Getter @lombok.Setter
    private String headerColor;
    @lombok.Getter @lombok.Setter
    private String presentation;
    @lombok.Getter
    private List<HelpCenterSection> sections = new ArrayList<>();
    @lombok.Getter @lombok.Setter
    private String createdDate;
    @lombok.Getter @lombok.Setter
    private String createdUser;
    @lombok.Getter @lombok.Setter
    private String updatedDate;
    @lombok.Getter @lombok.Setter
    private String updatedUser;

    @lombok.Getter @lombok.Setter
    public static class HelpCenterSection {
        @Field("id")
        private String id;
    private String baseGroupId;
        private String icon;
        private String title;
        private String blurb;
        private String contentHtml;
        private List<String> imageUrls = new ArrayList<>();
        private String panelSpan;
        private Boolean optional;
        private String guideStepId;

        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls == null ? new ArrayList<>() : new ArrayList<>(imageUrls);
        }

    }

    public void setSections(List<HelpCenterSection> sections) {
        this.sections = sections == null ? new ArrayList<>() : new ArrayList<>(sections);
    }

}
