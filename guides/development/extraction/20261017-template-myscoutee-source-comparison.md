# Template and MyScoutee source comparison

2026-10-17. Production sources only; frontend assets and tests excluded.
Files are matched by relative path. Line counts include comments and blank lines.

## Backend

Java sources under `server/projects/profile/src/main/java/com/raxim/myscoutee/domain`.

- Template: **345 files, 35,445 lines, 12 domains**.
- MyScoutee: **1,187 files, 167,143 lines, 22 domains**.
- **257 identical files (74.5% of template files)**, 82 changed, 6 template-only.
- MyScoutee adds 848 files: 614 in additional domains and 234 in shared domains.

| Shared domain | Template files | MyScoutee files | Identical | Changed |
| --- | ---: | ---: | ---: | ---: |
| access | 27 | 39 | 21 | 6 |
| accounts | 81 | 181 | 61 | 20 |
| administration | 8 | 25 | 2 | 6 |
| chat | 36 | 63 | 29 | 7 |
| email | 16 | 43 | 15 | 1 |
| help | 11 | 15 | 6 | 5 |
| integrations | 45 | 68 | 32 | 13 |
| localization | 9 | 13 | 9 | 0 |
| media | 18 | 20 | 14 | 4 |
| notifications | 25 | 35 | 14 | 11 |
| platform | 63 | 71 | 54 | 9 |

`localization` and the `email` foundation are the closest shared-JAR candidates.
`media` needs retry behavior aligned. Accounts, integrations, access and chat
require product dependencies separated. The existing Gradle `contracts` module
already packages API/DTO sources.

## Frontend

Application sources under `frontend/src`; shared library under `components/src`.

- Template application: **240 files, 38,027 lines**.
- MyScoutee application: **859 files, 282,943 lines**.
- **98 identical files (40.8% of template files)**, 106 changed,
  36 template-only and 655 MyScoutee-only.
- Including unchanged portions of modified files, **29,554 template lines match
  (77.7%)** at shared paths.
- Both use the same `myscoutee-components` **1.4.1** commit:
  **176 identical files, 48,944 lines**.
- With that library included: **274 identical files out of 416 template files
  (65.9%)**, versus 1,035 MyScoutee files.

| Application area | Template files | MyScoutee files | Identical | Changed |
| --- | ---: | ---: | ---: | ---: |
| shared/core | 138 | 410 | 57 | 64 |
| shared/ui | 41 | 192 | 23 | 17 |
| profile | 14 | 30 | 5 | 9 |
| operator | 12 | 15 | 3 | 3 |
| entry | 7 | 14 | 4 | 0 |
| home | 3 | 15 | 3 | 0 |

Identical foundations include session/Firebase handling, i18n, notifications,
chat calling/encryption, HTTP interceptors, the document viewer and setup stores.
Most differences are in user services, IndexedDB/seed data, profile/workspace
stores and activity contracts. Further library extraction needs configuration
and product dependencies separated.

Compared backend revisions: MyScoutee `3d2bfe5c4`, template `bd7b924`.
Frontend: MyScoutee `2b1cc0dc5`, template `a28c76a`; components `ce3a0c3`.
