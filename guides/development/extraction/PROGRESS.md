# Shared business modules: implementation status

Updated: 2026-10-23

## Current position

The backend extraction is **partial**. Follow the order in
[SHARED_BUSINESS_MODULES_PLAN.md](SHARED_BUSINESS_MODULES_PLAN.md): backend and
both `microservices/` directories, packaging, remaining tools, then
final qualification. Apply each extraction to backend and template together.
Use focused compilation/static/unit checks while moving code; do not repeat
full monolith/KVM qualification before the whole cleanup is complete.
Each eventual backend/template monolith/KVM check requires Git/diff and original
source-baseline inspection; failures authorize extraction/wiring corrections,
not application behavior patches. Pre-existing defects are reported separately.

The source/backup review is recorded in
[EXTRACTION_HISTORY_REVIEW.md](EXTRACTION_HISTORY_REVIEW.md). Earlier authorized
server-side Sonar changes are retained. Frontend extraction has been cancelled
and rolled back; no frontend source, npm package or analysis module remains in base.

## Implemented business-code extraction

Both applications consume the locally staged binary set `0.1.0-dev.8`.
All 37 dev.8 Java JARs are byte-identical to dev.7; compilation remained up to
date. The Java compatible set remains unchanged by the frontend rollback.
There are 37 Java modules plus the compatibility platform, including 28 regular
modules and 9 optional transport/bootstrap modules. The original 238 production
Java files and 6 tests have one owner in base, with their bytes and namespaces
preserved. Their consumer copies are absent. Exact paths and hashes are in
[LOCALIZATION_EXTRACTION_BASELINE.json](LOCALIZATION_EXTRACTION_BASELINE.json).
Dependency-closed inventories and module maps are retained alongside the plan.
Files coupled to differing application contracts have not been rewritten to
force extraction.

Applications consume versioned JARs, with strict compatible-set alignment.
Monolith graphs exclude optional transport/bootstrap modules and do not compile
base source. Existing ownership checks reject stale consumer class copies.
The 30 previous module JARs are byte-identical between dev.6 and dev.7; only the
7 new optional modules needed compilation. Local release manifests record
checksums/provenance under ignored `server/build/releases/`; the binary staging
repository is `server/build/repository/`. Nothing has been published externally.

## Implemented microservices runtime-support batch

The first 32 shared runtime-support files now live once in `myscoutee-microservices`:
host lifecycle, pinned Terraform/Helm and guest infrastructure, container recipes
monitoring assets, 11 common Helm chart assets and the existing deployment renderer. Both consumers' copies were removed and their existing
callers rewired. Runtime/assembly configuration, generated outputs, VM identity,
private state and provider caches remain project-specific. The exact move list
and verification are in
[MICROSERVICES_SUPPORT_EXTRACTION.json](MICROSERVICES_SUPPORT_EXTRACTION.json).

- 30 moved files retain their original bytes. The Compose file changes only
  mount-source interpolation; the existing renderer gains project selection and
  chart staging so each source owner is consumed without cloned source.
- Pre/post Dockerfile/monitoring output is byte-identical: 14 generated files
  for backend and 10 for template.
- Resolved monitoring Compose configuration is identical for both consumers.
- Existing focused Python cases: backend 16 passed, template 11 passed.
- Backend chart staging reproduces all 16 original files and all 65 rendered
  Kubernetes objects byte-for-byte; template reproduces all 15 files and 45
  objects. Product chart inputs moved unchanged to `config/microservices/chart/`.
- Sonar Helm-input metadata test: 1 passed.
- Consumer Python/Bash syntax checks passed. No VM, application or image build
  was started for this technical move.

Both original top-level `microservices/` directories are now absent from the
filesystem. The subsequent whole-context move owns another 23 shared build/tool
paths once, retains product assembly/chart/runtime/binding inputs under
`config/microservices/` and isolates generated build/cache/evidence under
`server/build/microservices/`. Product scenarios remain under
`tools/microservices/`; original docs followed the separate documentation move
to `guides/development/microservices/`. Exact ownership and original hashes are
in [MICROSERVICES_CONTEXT_RELOCATION.json](MICROSERVICES_CONTEXT_RELOCATION.json).

The physical bootstrap-tests directory is gone. Its still-used logical test
aggregation consumes one shared Gradle convention and each product's unchanged
source include configuration. All 10 backend / 6 template effective Java source
sets match their pre-move snapshots. Post-move focused localization/access JUnit
checks passed again: 16 in each consumer. The first run exposed the repository
default's old root assumption; comparing the backup commit identified that path
wiring error. The existing policy now uses the selected consumer root, retaining
explicit property/environment overrides. No Java implementation was changed.
Common runtime/configuration Python checks passed 11 per consumer. Generated
image/monitoring/chart inputs and all 65 / 45 Helm objects retain their bytes.

The first script/configuration review is recorded in
[MICROSERVICES_SCRIPT_OWNERSHIP.md](MICROSERVICES_SCRIPT_OWNERSHIP.md), including
actual scenario dependencies and generic helper follow-ups in step 4. Absence
from template is not evidence of product specificity. The common seed engine and native-session
runner now have one runtime owner. Retry migration execution also belongs there;
backend declares its exact allowed collection/catalog keys in seed configuration.
Template's existing catalog formats/notification outbox were inspected, but its
sources do not include this specific new collection set, so the migration is not
enabled implicitly. Session/resource/probe helper bodies moved unchanged.
The full backend session-check request trace (20 requests) and report match the
original with mocks. Seed CLI help is byte-identical for both consumers; 14
backend / 3 template seed cases and 3 resource cases pass without a VM/database.
The shared Mongo fixture preserves each product's existing error-reporting policy.
See [SCRIPT_MECHANISM_EXTRACTION.json](SCRIPT_MECHANISM_EXTRACTION.json).

The updated E2E helper is staged as an npm tarball `1.4.7-dev.1` with integrity
in both consumer lockfiles; no installed-copy-only patch or external publication.
Its two transport tests and independent installed-package check pass.

## Application-owned optional Java relocation

The remaining optional distributed source has moved unchanged to each owning
application's domain/common optional source units. Backend: 933 production and
61 test files; template: 146 production and 18 test files. All 1,158 files match
both the recorded SHA-256 and the original backup commit's Git blob. Original
Java copies under `microservices/` are absent. Exact source/destination paths
and evidence are in [OPTIONAL_JAVA_RELOCATION.json](OPTIONAL_JAVA_RELOCATION.json).
These application-bound adapters are not claimed as newly shared base code.

Both existing distributed builds consume the relocated units. Selected existing
localization/access JUnit classes pass: 16 cases in each product. Compilation is
up to date with the same effective source inputs. Monolith compilation is up to
date and its Gradle source set contains exactly the original 1,072 backend / 194
template business files; every original file's bytes are unchanged, and optional
units/tasks remain excluded. All 193 / 36 transport selections and 8 / 4 service
capability sets are unchanged. All 8 / 4 regenerated assembly-import Java files
are byte-identical. Sonar roots and input paths follow their relocated owners;
14 existing shared-tool tests passed directly and the remaining metadata case
passed after correcting its temporary fixture directory setup. No application
behavior or Java implementation was patched.

The earlier temporary isolation-check script used an incorrect Gradle project
path; it was corrected to the existing `:projects:profile`. This was a check
harness correction, not an application change. Full runtime qualification is
still deferred. Remaining work in step 1 is completion of the script/configuration
ownership review; both original microservices directories have retired.

## Earlier verification of dev.7

These checks were completed before the instruction to defer expensive runtime
qualification; they are recorded evidence, not a reason to repeat them now:

- Base: 25 tests passed.
- Distributed bootstrap/HTTP suites: backend 466 and template 111 passed;
  ownership/duplicate audits passed for all 8 backend and 4 template assemblies.
- Focused standard consumer tests: backend 17 and template 12 passed.
  Backend embedded idle-worker case: 1 passed.
- Both monolith JARs built with 28 regular base libraries and no optional
  transport/bootstrap libraries or microservices classes.
- Isolated monolith HTTP checks passed in both consumers: health UP,
  English/Hungarian bundles match every canonical fixture value (backend 9,174
  keys, template 1,816), no-store caching, and no data for a matching version.
- KVM qualification has not been completed. The attempted startup failed at
  memory preflight before VM startup; further runs are deferred.

## Frontend rollback and remaining tools

Frontend extraction was cancelled on 2026-10-23. Both applications again own
all original frontend implementations, tests and popup HTML/SCSS resources.
The original Git blobs were restored exactly for 90 backend and 78 template
paths. Generated HU geometry remains local in each application. Complete
working-tree byte comparisons found only the requested HU change/tests and
the previously requested four CPU-affinity script aliases beyond the original
Git versions. Both dependency lockfiles match their pre-extraction Git blobs.
Focused tests passed (43 backend, 16 template); both production builds passed.
Base has no frontend directory, npm artifact, frontend-specific inventory/report
or frontend Sonar configuration. Detailed rollback proof remains in the backend
frontend's ignored `dist/frontend-rollback-evidence/`, outside base.

The first Debian packaging batch moved five shared release-signing/archive-audit
implementations and two existing tests to `myscoutee-packaging`; both original
copies were removed. Product compatibility policies moved unchanged to
`config/packaging/release/`. Explicit project-root/policy inputs replace the old
file-relative context. Archive/key fixture parity, both signing/bundle suites,
seven compatibility cases and nine focused Java consumer cases per application
passed. Exact provenance and verification are in the packaging owner's
`EXTRACTION.json`. Broader packaging work, including applicable Registry image
payloads, remains incomplete. Frontend extraction is no longer in scope.
Inspect actual dependencies before introducing any language-specific exception.
Remaining shared tools follow their contextual owners; no generic copied
common-tools tree is planned.

## Sonar and operational state

One existing Sonar server is selected by the shared `myscoutee-sonarqube` tool.
Existing application/base/components analysis registrations are preserved;
registration does not mean an analysis has run. Base Java analysis completed
on that server. Frontend analysis remains with the applications; the cancelled
base frontend module was removed from local configuration. Shared runtime has
a source-only configuration and runner support on that same server. Its registration
attempt using the valid saved analysis credential returned HTTP 403; new-key
registration is not claimed. The 16 shared-tool checks passed before the final
runtime report-polling allowlist correction; that focused runner check passed (9 cases).

Build scheduling is unrestricted: no affinity or fixed worker cap. Independent
work may overlap when memory and mutable-output ownership permit. Reuse existing
watchers when running. Application stacks and both KVM VMs are stopped; only
the existing Sonar server/database remain running. No commits or pushes were
made by this extraction step.
