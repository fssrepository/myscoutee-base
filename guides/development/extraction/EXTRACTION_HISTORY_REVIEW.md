# Extraction source and backup-history review

Date: 2026-10-23. Scope: the retained server-side technical extraction and its
backup commits. Frontend extraction was cancelled and removed.

## Baselines and method

The review indexed all available refs in the eleven participating repositories.
Changes belonging to this extraction were compared along their actual ancestry;
unrelated older feature branches were not treated as extraction regressions.
User backup commits were preserved. No reset, amend, squash, commit or push was
performed by this review.

The user confirmed that earlier parallel Sonar-driven source changes must stay.
The reference below is therefore **after those authorized changes and before
the base extraction**, not an arbitrary earlier backup. Comparing with the
October 22 afternoon snapshot also finds Lombok conversions and shared helpers
already present before these references. Those differences are not treated as
extraction defects and have not been reverted. The extraction comparison must
not undo the separately authorized Sonar work.

| Source owner | Pre-extraction revision |
| --- | --- |
| Backend Java | `8ecf37b25f67a9d7566d05cfb4d12a7b2c7b91f0` |
| Template Java | `d9a0b0d653a655388cb80b15889b86412ae935d4` |

The Java snapshot review reaches backend `b0846e24bdb6`,
template `61e7fc8d0a14` and base `485db9a99701`.

Java comparisons use exact Git blobs, including relocated files. Existing
unrelated QA work was not rolled back.

## Findings and correction

| Area | Result |
| --- | --- |
| Production Java in backend, template and base | No content changes found in the extraction snapshots: 10 backend, 9 template and 10 base commits after initialization. All 238 production Java files in base retain their original bytes. |
| Current application-owned optional Java | All 994 backend and 164 template Java files are byte-identical relocations, including their existing tests. |
| Java test exception | Each application's `OperatorUpdateReleaseServiceTest.java` follows the moved packaging signer and passes its product compatibility-policy path. This is test/tool wiring; production Java is unchanged. |
| First runtime-support batch | 30 of 32 canonical files retain exact original bytes. The other two change monitoring mount paths and compose shared/product chart inputs in build output. Previously recorded generated-output parity remains the evidence for those wiring changes. |
| Subsequent runtime/build relocation | Reviewed changes select the product configuration/build roots and the relocated optional Java units. Existing test source selections moved to product configuration; their effective source sets were checked separately. |
| Extracted script functions | 22 of 23 retain exact original function text. The remaining function reads the existing migration allowlists from product configuration. All 23 match their recorded extraction-result hashes; available shared-runtime backup snapshots contain no additional variants. |
| Sonar | Single-server selection and analysis paths follow the explicitly requested ownership changes. These are tooling changes, not application implementation changes. |
| Packaging | The first signing/audit batch is implemented, not the whole packaging extraction. Explicit project-root and compatibility-policy inputs replace the old file-relative context; the shared mechanisms and focused parity checks are recorded in `../myscoutee-packaging/EXTRACTION.json`. |

The retry allowlists above belong to the **explicit lab seed/migration tool**.
They specify which new collections/catalog keys that migration may import.
They were previously literals in the Python tool. Application retry/job settings
still come from MongoDB; no application configuration lookup was replaced with
a file lookup. Template does not implicitly enable the backend migration.

Detailed local comparison results and the all-ref indexes are under the ignored
`server/build/extraction-history-audit/` directory. Original source maps/hashes
remain in `LOCALIZATION_EXTRACTION_BASELINE.json`,
`OPTIONAL_JAVA_RELOCATION.json`,
`MICROSERVICES_SUPPORT_EXTRACTION.json`,
`MICROSERVICES_CONTEXT_RELOCATION.json` and
`SCRIPT_MECHANISM_EXTRACTION.json`.
