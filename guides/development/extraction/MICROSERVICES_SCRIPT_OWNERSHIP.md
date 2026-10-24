# Microservices script ownership review

Review date: 2026-10-23. Ownership depends on implementation responsibility and
actual dependencies, never solely on file presence or identical-file intersection.
The full original tree inventory is in MICROSERVICES_CONTEXT_RELOCATION.json;
script/function provenance and input-only substitutions are in
SCRIPT_MECHANISM_EXTRACTION.json. Both original microservices trees are absent.

## Shared execution already owned once

`myscoutee-microservices` owns assembly/build/transport validation, runtime/config,
Terraform/Helm/VM/host/lifecycle/monitoring, workloads and Mongo replica execution.
The current follow-up also owns seed/bootstrap and additive retry migration,
Mongo stdin fixtures, UUID-owned media cleanup, native sessions/HTTP, public
checks, resource counters/stress recording and transport/STUN/deployment probes.
Product migration allowlists, fixture identities, route/status matrices, scenario
choices and reporting policy stay in config/microservices. Template's missing
scripts do not exclude it from these mechanisms: it now declares its native
account reads, template-read stress profile and media-fixture ownership pattern.
Backend landing/campaign/assets/Registry/payment assumptions are not its defaults.

## Retained scenario code and explicitly scheduled tooling follow-up

| File | Owner / next step | Dependency decision |
| --- | --- | --- |
| `check-admin-identity.py` | Backend scenario + checks/admin-identity.json | Validates the administrator dashboard identity and client-API contract; shared HTTP/native-session execution comes from the runtime owner. Template has a smaller administrator surface, so the backend matrix is not enabled implicitly. |
| `check-campaign-chat.py` | Backend scenario + checks/campaign-chat.json | Campaign membership/rating rejection scenarios and canonical work-profile fixtures; template has no campaigns domain. Execution reuses the common native-session helper. |
| `check-business.py` | Backend scenario + checks/contact-fixture.js | Disposable contact-journal/recovery assertions coupled to backend contact collections and workers. Mongo, session, deployment ownership and memory execution are common; fixture bytes and expected business transitions stay product-owned. |
| `check-profile-selection.py` | Backend scenario | Asserts event/account/chat counter and account-eligibility contracts plus MCP settings. Common session execution is reused; event fixtures/surface do not exist in template. |
| `check-load.py` | Backend load profile | Assets replica routing and per-route metrics plus backend administrator read matrix. Template has media but no assets deployment. Resource sampling, deployment guards and memory reads have shared owners; the profile remains product input for step 4. |
| `check-support.py` | Backend supporting-service scenario + checks/support.json | Registry/payment-simulator initialization and receipt identity, MCP ingress, and monitoring configuration. Template has neither Registry nor payment simulator. Transport-scope and STUN implementations are common. |
| `check-current-monolith.sh` | Backend historical qualification workflow; step 4 follow-up | MSC120 comparison/image/source-ref sequence with backend E2E mutation/reset and stress profiles. Review its generic Compose orchestration with the packaging/tools owner; do not run it during extraction. |
| `contact_fault.py` | Existing backend fault profile; step 4 follow-up | Communication Service selector/UID ownership, contact-permission rejection route and durable fault receipt. The proxy mechanism is reusable with a declared service/fault profile; backend contact behavior is not a template default. |
| `managed-browser-manifest.mjs` | Shared E2E reader candidate; step 4 follow-up | Generic private launcher-manifest reader. Move through the existing shared E2E package in step 4 with its existing safety tests; do not create another launcher or operate browsers. |
| `check-browser.mjs` | Backend delegated QA scenario | MSC121 checkpoint, backend native actor, menu/dialog and business assertions. Uses the existing managed launcher; no browser execution is part of extraction. |
| `check-demo-selection.mjs` | Backend delegated QA scenario | Specific checkpoint/actor and local-adapter loading/DOM performance evidence. Preserve acceptance criteria and manual-QA boundary. |
| `check-uk-entry.mjs` | Backend delegated QA scenario | Canonical GB country geometry, a particular existing demo actor, region eligibility and checkpoint UI sequence. Preserve the explicit scenario rather than enabling it on different template fixtures. |
| `test_infra.py` | Backend fixture/contract tests with shared helper owners | Host/VM/Mongo safety cases are reusable candidates for step 4; credential/consumer/worker expectations are tied to backend composition. Retain all assertions; no test duplication is required in template. |
| `test_browser_manifest.py` | Existing E2E reader safety cases; step 4 follow-up | Generic private directory/file/ownership/symlink/size checks; move together with their reader instead of making a second implementation. |

## Verification and limits

Twelve seed/retry function originals were recorded. Eleven retain their exact
function bytes; retry delta selection changes only the source of the exact
allowed key sets. Session/resource/probe helper function bodies retain bytes.
Seed CLI help matches both originals. Focused seed tests pass (14 backend,
3 template); resource cases pass (3). Backend session and public request traces
and reports each match all 20 original requests under mocks. Contact fixture
bytes and its formatted Mongo command match the original; actor/route matrices
match original AST literals. Source Java hashes and effective Gradle source
membership match the extraction baseline. No VM, app, fixture mutation, browser,
image build or stress profile was executed for these moves.

The E2E transport consumes a staged versioned npm artifact with lockfile
integrity. It is not externally published. Shared runtime Sonar configuration
uses the existing server; its valid saved analysis credential received HTTP 403
for new-project registration, so registration/analysis of the new key is pending.
Follow the central numbered plan: frontend next, packaging, remaining tools, then
final runtime qualification. Deferred reusable helpers above remain open work.
