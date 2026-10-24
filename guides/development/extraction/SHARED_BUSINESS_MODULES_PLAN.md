# Shared business modules extraction plan

Date: 2026-10-23

Status: Implementation in progress. See [PROGRESS.md](PROGRESS.md) for the
current extraction batch and verification limits.

Current user decision (2026-10-23): cancel the frontend extraction. Restore the
two frontend implementations and their original resource/import layout from
the post-Sonar, pre-extraction Git revisions. Preserve the country partition
implementation, including generated HU geometry, locally in both applications.
Remove the base frontend package and its consumer dependency. Keep the Java,
packaging, microservices and separately authorized Sonar work. This decision
excludes frontend extraction from the remaining scope.

## Objective and agreed constraints

Make `myscoutee-base` the single source of shared server-side business functionality used by
the template and MyScoutee applications. Remove cloned implementations and give
stabilized functionality an explicit maintenance boundary.

The base repository should broadly follow the existing application directory
structure. Backend domain directories become Gradle compilation units inside one
multi-project build. All released base Java modules share one compatibility version.
Frontend implementations remain in the original consuming applications.

The template and MyScoutee retain their existing application structure, behavior,
deployment choices, routes and frontend chunk boundaries. Necessary changes are
limited to removing extracted source copies, consuming published dependencies
and wiring existing product-specific implementations. This is not a conversion
of the consuming applications into new multi-project workspaces.

The active implementation round is strictly move-and-rewire. Preserve existing
business source and verify its bytes when moving it. Any behavior change,
structural redesign or normalization of product differences belongs to a later
round, handled individually. Apply every extraction to both backend and template:
move their common implementation once and rewire both consumers. Preserve their
own configuration and differing implementations.

The execution order is fixed below: **1. backend, including emptying both
`microservices/` directories; 2. frontend rollback; 3. packaging; 4. remaining tools;
5. final qualification**. Record new requirements in their owning step instead
of switching tasks. Use focused compilation, static checks and selected unit
tests during extraction. Full monolith/KVM qualification waits until the entire
extraction and cleanup are complete.

`myscoutee-components` remains the owner of shared UI and technological building
blocks. Base owns shared business functionality and uses components where needed;
this extraction does not move or duplicate the components library.

## Ownership by context

These boundaries follow the responsibility being shared, rather than the
implementation language or the directory in which a copy happens to live.

| Shared responsibility | Canonical owner | Consumer-owned input |
| --- | --- | --- |
| Business domains and their optional domain adapters | `myscoutee-base`, a compatible bundle of Gradle modules | Product-specific business implementations and configuration |
| UI and technology building blocks | Existing `myscoutee-components` | Application UI composition |
| Common support for running microservices: assembly/build/deployment mechanisms, lifecycle tools, infrastructure, monitoring and reusable runtime assets | `myscoutee-microservices`; Grafana/Prometheus are part of this context, not its entire scope | Each application's assembly grouping, bindings, runtime values, fixtures and other project-specific inputs under `config/microservices/` |
| Shared source analysis server and tooling | `myscoutee-sonarqube` | Source-owner analysis configuration under `config/sonarqube/` |
| Common Debian packaging and its lifecycle/build tools | `myscoutee-packaging` | Package identity, image/build inputs, migration files and deployment values under `config/packaging/` |

Do not put the whole microservices support implementation in base. A shared
domain adapter belongs with its domain; a reusable runtime/deployment mechanism
belongs in the microservices repository. Apply the same contextual split to the
similar backend and template structures. This changes ownership and wiring,
not application behavior or service grouping.

## Numbered implementation steps

Before executing each subtask, record its exact source/move list, dependency
boundary, common owner, required consumer wiring and verification. Inspect the
existing implementation first, then implement the planned mechanical change.
Choose boundaries from demonstrated dependencies; record any necessary exception
before implementing it.
After moving/removing the selected files, remove their now-empty original
source directories in both consumers, including on the actual filesystem.
Verify absence of the old files and empty source skeletons; Git alone does not
track empty directories. Preserve non-empty project configuration, intentional
versioned placeholder assets and active/generated output or private-state owners.
For any failed check during extraction, inspect the Git/source baseline before
proposing a fix. The authorized repair is a mechanical extraction/wiring
correction; a failing check does not authorize changing application behavior. This planning does not add an approval checkpoint for
already authorized technical extraction.

### 1. Finish shared backend extraction in both consumers

Status: partial. The shared Java modules exist; both application microservices
trees must still be split into their contextual owners and rewired.

#### 1.1. Use the completed source comparison as the extraction baseline

The existing
[2026-10-17 source comparison](20261017-template-myscoutee-source-comparison.md)
is the starting inventory. It already records backend domain and frontend area
tables, identical/changed file counts, source revisions and extraction findings.
Its canonical document now lives in this repository; the former backend copy
has been removed. Use those findings rather than starting a new general survey.

Before implementation, record current revisions and working-tree state for base,
template, MyScoutee, their frontends and components. Check changes since the
recorded comparison only as needed for the selected extraction batch. Retain the
comparison's original date and measured revisions; do not present its counts as
a new measurement.

For the selected candidate, resolve its existing API/DTO contracts, configuration,
dependencies and application-specific differences. Identical files are candidates,
not sufficient proof of an independent business boundary. Do not merge meaningful
product differences merely to increase the shared-code count.

Deliverable: the existing inventory linked to the selected batch's exact source
paths, relevant changes since measurement and behavior/build baseline.

#### 1.2. Define ownership across the complete shared extraction batch

Assign each shared server-side business responsibility to one base domain. Keep product-specific behavior with its current application owner.
Dependency direction must remain from the applications to base; base must not
import either application's concrete implementation.

Reuse existing domain contracts and configuration boundaries. Do not add new
application interfaces, proxy classes, constructor adapters or behavior changes
to make a source move possible. Keep existing project inputs with their owners
and change only the required dependency/path wiring. Do not create an extension
framework or product-name conditionals.

Use the completed comparison to order the extraction work:

- Start with localization as the pilot and the shared email foundation next,
  subject to checking their current dependencies.
- Resolve the recorded media retry difference before extracting that shared part.
- Separate the recorded product dependencies in accounts, integrations, access
  and chat before moving their common implementations.
- Keep the already shared components library with its existing owner.

This is a candidate order based on the recorded findings, not evidence that those
extractions are complete.

Deliverable: an explicit source-move list and dependency graph for the complete shared batch.

#### 1.3. Establish the Gradle multi-project build in the existing layout

Use Gradle, retaining the established wrapper, dependency policy and Java
toolchain conventions where applicable. Define domain subprojects in
`settings.gradle` and map them to the existing-style domain directories using
project directory/source-set configuration.

The intended logical layout is:

```text
myscoutee-base/
  server/
    settings.gradle                domain subproject definitions
    ...                            existing-style server hierarchy
    projects/profile/src/main/java/com/raxim/myscoutee/domain/
      localization/                Gradle module boundary
        api/                       existing domain contracts, where needed
        transport/                 optional microservices compilation unit
      email/                       Gradle module boundary
      ...
  config/
    ...                            deployment/composition configuration
```

This is a layout sketch, not a requirement to extract every domain or create
empty directories. Preserve Java package namespaces. Configure build output
outside Java source directories. The root build coordinates modules; it must
not become a second copy of the consuming application.

Each extracted backend domain produces a normal library JAR. Executable
application packaging stays with the existing application/assembly owners.
Separate API or transport artifacts only where an actual dependency boundary
requires them; do not mandate several artifacts for every domain.

Deliverable: a root build and one independently addressable domain module.

#### 1.4. Define one compatibility version for the entire base release

Use one authoritative release version `V` for every published base JAR.
Do not independently advance domain versions.

For example, a base release `1.5.0` contains accounts, chat and localization
artifacts at `1.5.0`, including modules whose implementation did not change.
Components keeps its own existing release lifecycle.

Define `V` to mean an explicitly verified compatible set, with its supported
consumer/framework versions and evidence recorded. A shared number expresses
the compatibility contract; it does not establish compatibility without checks.

Publish the complete set of modules included in that release. Record the source
revision, artifact coordinates, dependency versions and checksums in a release
manifest. Align backend dependencies with a Gradle-consumable platform/BOM or an
equivalent existing mechanism.
Consumers select the modules they need from one release without pulling every
base module into their runtime.

Deliverable: the shared version source, alignment rules and release manifest
format, including the policy for compatibility-breaking changes.

#### 1.5. Separate compilation, compatibility verification and publication

Allow targeted module compilation. Determine affected work from source changes,
resources, dependency changes, build configuration, generators and toolchain
inputs. Include affected dependents in verification; a public contract change
may require broader checks than an internal implementation change.

Use Gradle's existing incremental/build-cache mechanisms before introducing
custom artifact reuse. Reuse an unchanged module's previous compiled payload
only when its effective compilation inputs and dependency compatibility permit
it. A version embedded in generated source/resources, changed compiler options
or changed dependency inputs may require rebuilding it.

For a new release `V`, eligible unchanged payloads may be republished under `V`
without recompilation. Generate correct publication metadata and aligned
dependency references. Treat previously published releases as immutable.

A Git tag identifies the release source; it does not create the new artifact
coordinates. Reusing a binary also does not remove compatibility verification.
Record whether each artifact was rebuilt or reused, its original provenance,
and the checks supporting its inclusion in the new compatible set.

Deliverable: a release process that publishes the whole versioned set while
avoiding unnecessary compilation, with traceable reuse decisions.

#### 1.6. Extract the shared backend implementation once

Move the selected shared implementation into its base module. Remove the
corresponding production-source copies from both consuming applications and
replace them with the versioned dependency. Preserve package names and existing
behavior where possible. Move shared tests with their owner, retaining
application-specific integration checks in the applications.

Keep domain-specific resources and configuration with their actual owner and
verify their packaged paths. Check Spring discovery/wiring, persistence mapping
and initialization ownership so extraction does not introduce missing or
duplicate beans, workers or initializers.

Deliverable: both applications use one shared implementation, with no remaining
production-source clone for the extracted responsibility.

#### 1.7. Preserve monolith and distributed composition

One domain implementation must support both modes: as a local module in the
existing monolith and as business code inside an independently running service.
Use the same business JAR in both. The existing bootstrap, optional transport
and deployment configuration supply the distributed composition.

Migrate the current `microservices/` structure incrementally alongside each
domain extraction:

1. Identify that domain's existing adapters and their exact shared dependencies.
2. Move unchanged shared Java adapters into an optional compilation unit under
   the owning base domain. Preserve their existing Java packages and protocols.
3. Move required shared transport helpers once into their explicit common owner.
4. Rewire the existing service assembly and bootstrap tests to those published
   artifacts, retaining current grouping, configuration and bootstrap behavior.
5. Verify the monolith excludes those optional artifacts. Qualify the distributed
   HTTP paths at final qualification after all extraction and cleanup steps.

The existing top-level `microservices/` directory is transitional. Split its
contents by responsibility rather than retaining it as a second source tree:

| Current responsibility | Destination and owner |
| --- | --- |
| Shared domain transport Java and its tests | Base domain source hierarchy, in the domain's optional Gradle transport unit |
| Shared transport/bootstrap Java without a domain owner | An explicit optional common source module in base |
| Shared assembly/build/deployment tooling and reusable configuration | `myscoutee-microservices`, consumed through the existing assembly flow |
| Application assembly grouping, binding selection, deployment values and fixtures | That application's `config/microservices/`; retain product differences |
| Application-specific Java adapters | That application's existing domain source hierarchy, with an optional distributed compilation unit |

Move each existing implementation once and update path resolution, Gradle
settings, launchers, Compose/Helm inputs, image contexts, tests and documentation
to its canonical destination. Keep configuration and executable Java source in
their respective owners. Preserve the current service grouping and runtime
protocols; moving files does not authorize rewriting deployment behavior.

Retire the top-level `microservices/` directory after its contents and callers
have been rewired and verified. Do not retain a duplicate tree or permanent
forwarding shell merely to preserve the old path. Until that move is complete,
the existing directory remains the active deployment entry point.

Monolith consumers use the local business implementation and must not depend on
remote transport modules or execute microservices build/package tasks. A runtime
profile that merely disables an already included adapter is insufficient for
this build boundary.

Adapt the existing microservices assembly flow to consume the same base modules.
Preserve its assembly grouping and product-specific sources. Do not maintain an
additional source-copying flow or duplicate domain implementation. Preserve
domain API/DTO boundaries and the existing transport authorization semantics.

Deliverable: a checked dependency/task graph excluding microservices work from
the monolith, plus both consumers rewired to their shared runtime-support owner. Runtime qualification belongs to step 5.

##### 1.7.1. Optional transport extraction already implemented

Plan recorded before execution: 43 additional byte-identical Java files are
listed in [SHARED_TRANSPORT_INVENTORY.json](SHARED_TRANSPORT_INVENTORY.json).
Their dependency-closed candidate set uses already extracted base contracts,
framework libraries and other files in this same set. Keep adapters that still
require differing application contracts in their existing owner; do not change
those contracts to force extraction.

| Optional base unit | Existing production files to move |
| --- | ---: |
| `access-transport` | 2 |
| `accounts-transport` | 3 |
| `chat-transport` | 4 |
| `email-transport` | 8 |
| `notifications-transport` | 1 |
| `platform-transport` | 23 |
| `service-bootstrap` | 2 |

Move domain adapters under their domain's optional transport source unit;
place the two shared bootstrap configurations in an optional common source
unit. Preserve every Java package and file byte. Remove the consumer copies
and add the published optional dependencies to the existing distributed
compilation/test graph. Preserve the current explicit configuration imports,
service grouping and protocols. Monolith dependency graphs must exclude all
of these optional units.

Publish the complete compatible set at `0.1.0-dev.7`, verify unchanged-file
checksums, run the existing base and consumer bootstrap/HTTP tests plus archive
ownership checks. The source move and local `0.1.0-dev.7` publication are
implemented; the backend step remains incomplete while the original
`microservices/` trees and shared runtime-support copies remain. Existing build
and test evidence is recorded in PROGRESS.md. Do not repeat full bootstrap,
monolith or KVM runs now; continue the mechanical extraction and defer runtime
qualification to step 5.

##### 1.7.2. Move the first common microservices runtime-support batch

Plan recorded before execution. Both consumers have byte-identical copies of
these 20 files; the exact source hashes and revisions are recorded in
[MICROSERVICES_SUPPORT_EXTRACTION.json](MICROSERVICES_SUPPORT_EXTRACTION.json).
Move them once into `myscoutee-microservices` and remove both source copies:

- `microservices/deploy/images/service.Dockerfile.template`
- `microservices/deploy/release-simulator.Dockerfile`
- `microservices/deploy/stun/Dockerfile`
- `microservices/infra/guest/install-k3s.sh`
- `microservices/infra/guest/k3s.service`
- `microservices/infra/guest/observe.py`
- `microservices/infra/guest/resource-snapshot.py`
- `microservices/infra/helm-version.json`
- `microservices/infra/terraform.tfrc`
- `microservices/infra/terraform/.terraform.lock.hcl`
- `microservices/infra/terraform/main.tf`
- `microservices/infra/terraform/variables.tf`
- `microservices/infra/versions.json`
- `microservices/observability/compose.yaml`
- `microservices/observability/grafana/dashboards/services.json`
- `microservices/observability/grafana/provisioning/alerting/.gitkeep`
- `microservices/observability/grafana/provisioning/dashboards/services.yaml`
- `microservices/observability/grafana/provisioning/datasources/prometheus.yaml`
- `microservices/observability/grafana/provisioning/plugins/.gitkeep`
- `microservices/tools/host.py`

Dependency boundary: these are reusable host lifecycle, pinned Terraform/Helm
and guest infrastructure, container recipes and monitoring assets. They read
existing per-project runtime/assembly values. Keep `assemblies.json`,
`deploy/runtime.json`, image locks, chart-specific configuration and application
sources in their current owner during this subtask. Do not alter images,
versions, ports, resource policies, credentials or lifecycle semantics.

Consumer wiring: resolve the support repository from the existing backend root,
with an explicit `MYSCOUTEE_MICROSERVICES_SOURCE` path override. Rewire existing
Python/Bash callers to the canonical assets and host tool. Keep generated outputs,
private state, VM identity and Terraform backend/provider-cache paths isolated
per consuming project. Monitoring volume paths must still select that project's
existing generated observability directory. The existing templates/renderers
remain the implementation; this move does not introduce a new orchestration flow.

Verification: compare pre/post rendered Dockerfiles and monitoring output;
compare resolved Compose service/volume configuration; verify moved-file hashes
allowing only required Compose path interpolation; run relevant existing
runtime-config, host/observer and transport-selection unit cases for both
consumers, plus Python/Bash syntax checks. No image build, monolith startup,
KVM startup or complete bootstrap suite belongs to this subtask.

This is a partial backend step. Continue with common assembly/deployment tools,
chart composition, remaining optional Java source ownership and project config
moves until both original `microservices/` directories are empty. Do not switch
to frontend or call the backend extraction complete after this asset batch.

##### 1.7.3. Separate shared chart mechanisms from project chart inputs

Plan recorded before execution. Move the 11 byte-identical chart assets below
and the existing common deployment renderer once into `myscoutee-microservices`.
The exact shared and project-specific file hashes/moves are recorded under
`chartBatch` in MICROSERVICES_SUPPORT_EXTRACTION.json.

- `microservices/deploy/chart/.helmignore`
- `microservices/deploy/chart/files/public-upstream.conf`
- `microservices/deploy/chart/templates/assemblies.yaml`
- `microservices/deploy/chart/templates/frontend.yaml`
- `microservices/deploy/chart/templates/minio.yaml`
- `microservices/deploy/chart/templates/mongodb.yaml`
- `microservices/deploy/chart/templates/redis.yaml`
- `microservices/deploy/chart/templates/release-simulator-ca.yaml`
- `microservices/deploy/chart/templates/runtime-status.yaml`
- `microservices/deploy/chart/templates/secrets.yaml`
- `microservices/deploy/chart/templates/support.yaml`
- `microservices/tools/render-deployment.py`

Move each consumer's unchanged chart identity/default values, gateway content
and differing configuration template to its own `config/microservices/chart/`.
Keep those differences; do not normalize the two configuration templates.
Helm requires a complete chart directory, so extend the existing renderer to
assemble common assets and project inputs into that consumer's ignored build
output. Reject overlapping source ownership and remove obsolete generated files.
No source copies remain in either old `deploy/chart/` directory.

Rewire existing lab rendering, chart deployment and backend context checks to
that existing renderer and generated chart. Keep renderer selection explicit
through the existing backend-source context. Rewire Sonar's existing Helm-default
preparation to the project's relocated chart input; shared runtime analysis
ownership follows the canonical runtime sources when step 1 is complete.

Verification: record old complete chart hashes and offline Helm-rendered output
for both consumers using explicit placeholder inputs; compare every staged file
and rendered Kubernetes resource afterward. Recheck existing generated image and
monitoring bytes. Use cached pinned Helm only, no cluster/VM calls, no image
build and no full regression suite. Run affected Sonar preparation unit cases.

##### 1.7.4. Relocate remaining application-owned optional Java unchanged

Plan recorded before execution. Exact original/destination paths, source revisions
and hashes are in [OPTIONAL_JAVA_RELOCATION.json](OPTIONAL_JAVA_RELOCATION.json):
backend 933 production and 61 test files; template 146 production and 18 test
files. These remaining adapters still compile against application-owned concrete
contracts; do not rewrite those contracts or pretend they are already extracted
shared libraries. Previously extracted base adapters remain canonical in base.

Under the explicit microservices relocation exception, move domain adapters and
their tests into that application's existing domain hierarchy as optional
`transport/src/main/java` and `transport/src/test/java` source units. Move
application-bound common transport/security and bootstrap source into explicit
optional source units under `common/microservices/`. Preserve every Java byte,
package, protocol and service grouping. Remove the original Java copies.

Rewire the existing distributed Gradle assembly/bootstrap-test source sets to
those units; exclude nested optional units from canonical business-source scans
and monolith source sets. Keep the existing compilation/task boundaries: no
optional source or transport dependency enters monolith compilation. This does
not reorganize the ordinary business source or change any business contract.

Rewire source-analysis roots with their owners: monolith Sonar excludes optional
source units; each distributed analysis selects its relocated optional main/test
roots from the application repository base. Preserve analysis keys/history.
Update the existing scanner bytecode freshness and generated Helm-default input
paths without starting another server or running a new analysis here.

Verification: capture transport consumer selection before moving; check all
moved hashes and absence of original files; compare consumer/capability selection
and generated assembly imports; compile distributed bootstrap/transport tests and
run selected existing transport JUnit classes only. Compile each monolith and
check its source/task graph excludes optional units. No complete bootstrap suite,
image build, monolith startup or KVM startup belongs to this subtask.

##### 1.7.5. Review and finish both complete microservices trees

Plan recorded before execution. The full directory/tool inventory, dependencies,
source revisions, hashes and exact destinations are in
[MICROSERVICES_CONTEXT_RELOCATION.json](MICROSERVICES_CONTEXT_RELOCATION.json).
This covers every remaining tracked file in both trees, not just matching files
or the template's smaller domain set. Keep real product-specific behavior out of
the common runtime owner.

Treat most tools as candidates for a shared execution mechanism. A tool's absence
from template is not evidence of project specificity: inspect actual imports,
state, endpoints, domain/fixture assumptions and product configuration. Distinguish
reusable mechanisms, project input/data and genuinely product-specific policy.
Keep a tool's initial destination provisional where that separation is pending;
do not label a backend-only file product-specific solely because it has one copy.

Audit hard-coded values as well as imports. Move product identifiers, addresses,
paths, domain/service selections and scenario/fixture inputs into explicit
project configuration with the same current values. Preserve protocol constants
and actual execution semantics. A common runner must not silently acquire the
backend's domain set as a default for template. Check where template can consume
an existing shared mechanism through its own declared dependencies/configuration,
without adding undeclared business features or inventing expected test behavior.


Move the remaining identical runtime/build tools and the existing Gradle recipes
once to `myscoutee-microservices`. The inspected context/MinIO verification tools
also belong to reusable runtime support. Product business/browser scenarios,
fixture selection and migration policy remain project-owned. The service recipe
differs only in two source include patterns that match no template files; verify
its effective inputs instead of changing domain implementations.

The physical `bootstrap-tests/` source tree is no longer needed: tests already
belong to optional domain/common source units. Its dependency/test-task mechanism
is still used by cross-domain HTTP fixtures and `verifyAssemblies`. Move that
shared Gradle convention once to `build-logic/transport-tests.gradle`, retaining
only each product's exact canonical test-support include list in
`config/microservices/tests.json`. Keep the logical test aggregation/task path;
its descriptor/output is generated, not another source directory. Do not delete
coverage or redesign domain tests while retiring that physical directory.

Move project assembly grouping, bindings/OpenAPI inputs and deployment values to
`config/microservices/`; product QA/seed tools to `tools/microservices/`; retained
docs/audits to `guides/microservices/`. Keep the different product domains and
fixtures. Inspect seed/fixture implementation differences rather than hiding
both copies in common; record their common-function follow-up before frontend.

Reuse the existing backend-source selector and existing tools. Place generated
build/image/chart/monitoring output under `server/build/microservices/`, moving
the existing build and project cache only after their owners exit. Preserve
all Java bytes, private state, VM identity and runtime configuration. Remove old
copies, generated source bytecode caches and empty original directories; retain
no forwarding shell or symlink tree. Remove fixed build worker caps previously
rejected by the user.

Rewire actual packaging-test, E2E fixture, manual-QA runtime-inspection, Sonar and
operator-doc callers. This is path wiring, not permission to run browser actions.
Give the shared runtime sources their own source-only Sonar analysis on the one
existing server; preserve consumer keys/history and the global scan lock.

Verification: snapshot effective assembly/bootstrap source membership first;
compare after rewiring. Verify unchanged configuration/Java hashes, transport
consumers/capabilities, generated assembly imports, chart/monitoring/container
recipes, and absence of the retired trees. Run focused Python/JUnit and affected
existing E2E-fixture/Sonar unit checks; verify monolith source/task exclusion.
No image build, monolith startup, KVM startup or full regression run here.

Post-move wiring correction: the first focused distributed JUnit run could not
resolve the staged base platform because the existing repository default was
relative to the old application build root. The original backup commit confirms
that root assumption. Resolve this existing default from the selected consumer
root in both applications, preserving explicit property/environment overrides.
Also correct remaining product QA helper/report paths from configuration root
to their actual tool/output owners. Re-run only the failed focused cases.

##### 1.7.6. Separate reusable script execution from project inputs

Current per-script decisions and explicitly queued step 4 work are in
[MICROSERVICES_SCRIPT_OWNERSHIP.md](MICROSERVICES_SCRIPT_OWNERSHIP.md).

Plan recorded before execution. The seed/bootstrap implementations share ten
identical execution functions and their CLI except for backend's explicit
outbound-retry catalog migration. Move that existing engine/CLI once to
`myscoutee-microservices/tools/seed_mongo.py`, with one common `seed-mongo.py`
entry point. Inspection of the retry migration confirms that its Git-delta,
append-only validation, staging and receipt mechanism is reusable too: own it
once as `myscoutee-microservices/tools/seed_retry_catalog.py`. Preserve its
algorithm; externalize the exact allowed new collections and catalog keys in
backend `config/microservices/seed.json`. Template has the shared catalog formats
and notification outbox implementation, but not this migration's new collection
set. Keep that specific migration unconfigured there while making the common
runner available; do not infer specificity from a missing script. Preserve argument
validation, locking, canonical importer/allowlists, reset guards and errors.

The E2E Mongo stdin executor is also identical; move it once to the runtime
owner, preserving the different existing exception-reporting policy as a
project configuration value. Rewire E2E and the manual-QA fixture caller without
executing fixtures or browser flows. Move the existing identical seed-staging
tests to the engine owner and keep backend-specific migration tests in backend.
Record original file/function hashes and source revisions before moving. Verify
unchanged function bytes or exact input-only substitutions, CLI/help and request/command parity with mocks plus existing
focused unit cases; no VM, database, image build or application startup.

Continue the script review before accepting step 1: common session/HTTP helpers,
resource measurement and infrastructure probes must be owned once; route lists,
scenario choices, fixture identities and true product policy stay in product
configuration/source. Inspect template's actual endpoints/dependencies before
enabling its use. File absence alone never determines ownership.

Session subtask: move the existing native-login/request/logout and read-check
runner once to the runtime owner. Backend's seven assembly/read routes and
public-document inputs become `checks/session.json` with exactly the same
values. Template consumes the same runner for its existing native account read
endpoints, verified in its `AuthController`; do not copy backend's other domain
routes. Preserve the helper module's mutable `ORIGIN` API and request semantics.
Move the existing three resource-counter functions and transport/STUN,
deployment-ownership and memory probe functions once; retain scenario execution and product fixtures with their
current owner until their own review. Compare old/new request traces with mocks
and run the existing resource-evidence cases; do not execute runtime probes.

E2E caller subtask: update the existing shared npm helper and its test, stage
a local `1.4.7-dev.1` tarball and consume that artifact from both applications.
Keep a real version/integrity for the changed helper instead of editing only
installed `node_modules` copies. External publication remains a later release
action. The common KVM helper receives the selected application context explicitly.

Additional reviewed cases: template's `e2e-media.py` is an owned-prefix cleanup
mechanism, not template-only behavior. Move it once, retaining its exact fixture
owner regex as template configuration; do not infer a backend fixture prefix.
`check-public.py` is a reusable ingress/catalog runner: move it once, keep the
exact backend status matrix in project configuration and give template only
routes supported by its own controllers. Preserve its canonical landing catalog
projection/asset checks. Stress recording is likewise common; read scenario
choices from each project's existing scenario descriptors. No probe/cleanup/
stress execution is part of this move. Verify helper bodies/input parity and
CLI setup with mocks and syntax checks.

Retained product scenarios: externalize the exact admin read/actor matrix,
campaign-chat actor/rejection fixtures and contact Mongo fixture script into
backend `config/microservices/checks/`. Keep their existing orchestration and
business assertions product-owned. These are actual scenario/contract choices,
not evidence that their shared session/Mongo/probe execution should be cloned.
Preserve fixture script bytes and each configured value; compare extracted
inputs with original AST literals before rewiring. Reusable fault-proxy/lab
helpers and browser-state readers remain explicit review items under step 4,
with no runtime or manual-QA execution authorized by this extraction.

##### 1.7.7. Correct the manual-QA Mongo package boundary

The user explicitly requested this wiring correction while stopping the unrelated
QA directory reorganization. The manual-QA checkpoint harness must consume its
QA package's Mongo transport, independently of E2E fixture entry points and the
microservices source checkout. The existing QA package has no Mongo adapter;
its browser/provider tools do not implement this transport.

Before editing, inspect the pre-extraction checkpoint caller, `e2e-mongo.py`,
`seed_mongo.mongo`, namespace ownership checks, SSH invocation and Mongo member
selection. Reuse that execution contract in the technology-owned QA `mongo/`
adapter: explicit project runtime configuration, private SSH state, recorded
namespace UID/label verification, DEMO-only database, bounded fixture stdin,
existing replica/deployment selection and JSON result. Read project configuration
and existing lab receipts directly; do not import E2E or microservices Python
modules or create/start/adopt a lab. Keep product fixture JavaScript unchanged.

Rewire only the existing backend KVM checkpoint branch. Inspect template's own
checkpoint runner; retain its existing Docker/local paths rather than enabling
a new KVM workflow there. Include the adapter in the independent QA npm package
and verify it from a packed/isolated package, with mocked SSH and unchanged
fixture payloads. Cover wrong database, missing/changed namespace ownership,
replica/single-member command selection and bounded input. Do not execute manual
QA, start a VM or change checklist criteria. Resume packaging step 3 afterwards;
QA directory/checklist organization belongs to the other workstream.

Implemented: QA owns `mongo/kvm-fixture.py`, included in its npm package; the
backend KVM branch invokes it. Seven focused isolation/ownership tests pass.
For backend and template configuration with both StatefulSet and Deployment
Mongo targets, every SSH argument, timeout, authenticated fixture payload and
JSON result matches the existing runtime helper. The packed QA adapter works
from an isolated directory without either sibling codebase or `PYTHONPATH`.
No fixture, browser, database or VM was executed. Template's runner is unchanged.

### 2. Restore frontend ownership to the applications

The user cancelled frontend extraction on 2026-10-23. Restore the existing
implementations, tests, imports and popup resources from their original Git
revisions. Keep the generated country partitions, including HU, locally in both
consumers. Remove the base frontend package, its configuration and consumer
npm dependencies. Do not resume frontend extraction or redesign its build.

### 3. Move common Debian packaging to `myscoutee-packaging`

Execute after backend/microservices extraction and frontend rollback; final runtime
qualification remains in step 5. Use the existing sibling
`myscoutee-packaging` repository as the canonical owner of shared Debian packaging,
maintainer scripts, release assembly and packaging-specific tools/tests.

This scope crosses implementation languages: include Java-based applications
and Go-based repositories that produce Debian packages, including
`myscoutee-registry`. At the packaging boundary the payload is a Dockerized
application: its internal implementation may be Java, Go or another language.
Use the same packaging pipeline for those image payloads, with project
configuration describing image references/build inputs, package identity and
deployment data. Do not introduce a Java/Go branch in Debian packaging merely
because the code inside the image uses a different language. Reuse existing
image-build operations through configured inputs rather than cloning packaging
implementations per language or rewriting native build behavior. Debian assembly,
metadata, signing/auditing and lifecycle packaging converge in the packaging repository.
Do not assume every repository produces a `.deb`.

Distinguish project-specific input from the common execution mechanism. For
example, Mongo migration files and database/deployment values may remain
project-owned inputs, while the existing Mongo migration runner can be shared
regardless of whether its application is implemented in Java or Go. Preserve
the files, ordering and execution behavior. Inspect actual runner dependencies
and lifecycle differences before deciding what is shared; image opacity alone
does not prove that every packaging step is identical.

As shared mechanisms move into the packaging repository, retain project-specific
configuration and inputs under the application's eventual `config/packaging/`
boundary. The existing `packaging/` directory may progressively empty and then
disappear through those moves and caller rewiring. This is a destination for the
queued packaging extraction, not an immediate consumer-directory restructure.

Reuse the existing release-project policy and packaging flow. Compare their
current implementations before moving files. Keep application identity, managed
image inventory, UID/GID, deployment values, fixtures and other actual product
differences in explicit configuration. The target is one packaging implementation
consumed by both applications, with configuration describing their differences.
Do not rewrite installers or alter release signing, credential checks, migration,
upgrade, recovery or data-preservation behavior during this move.

Deliverable: one shared packaging implementation in the packaging repository, consumers
rewired to it, product configuration retained, and the existing packaging checks
passing for both products.

#### 3.1. Extract the existing release-signing and archive-audit mechanisms

Plan before execution: the backend and template have byte-identical
`audit-nested-archive.py`, `private_key_material.py`,
`sign-release-artifacts.sh`, `sign-update-manifest.py`, `prepare-release.py`
and two release-signing/bundle tests. Move these source-only implementations
once to `myscoutee-packaging/release/` and `tests/`, then remove both originals.
Keep the existing product-specific compatibility requirements byte-for-byte
under `config/packaging/release/update-compatibility-policy.json`.

The signers use OpenSSL, dpkg/jq and the existing release-project policy loader;
none depends on Java or Go. Keep the established signing domains, canonical
metadata, key custody, artifact validation and rejection behavior. Supply the
trusted project root and compatibility policy explicitly now that source code
and product data have different owners. The shared bundle must use the selected
project's existing policy and client verifier, never candidate-supplied policy.
Rewire existing packaging, release-tool and focused Java-test callers directly;
do not leave forwarding copies or change application runtime code.

Verify original source hashes and unchanged function bodies, then run the
existing signature/rejection tests for both products, compatibility tests,
disposable complete-bundle tests and the focused existing Java consumer test.
Archive checks use disposable ZIP/JAR fixtures only. No image build, deployment,
package installation, real release key, network publication, VM or manual QA is
part of this subtask. Record source revisions, moved files, callers and checks
in a packaging-owned extraction manifest. Broader image assembly/runtime
packaging follows this batch; Registry's actual policy/contracts remain under
review, rather than introducing a language-based packaging branch.

### 4. Extract shared tools through their existing responsibility owners

Execute after packaging. Tools required for a prior extraction move with their
context in that prior step; this step handles the remaining shared tools.
Compare the existing tools across repositories and assign each shared tool to
the repository that owns its responsibility: packaging tools to `myscoutee-packaging`,
shared base build/domain tooling to base, Sonar tooling to `myscoutee-sonarqube`,
shared microservices runtime tools to `myscoutee-microservices`, and
application-specific QA, fixtures and workflows to their current application.

Move each common implementation once and rewire its callers. Preserve established
commands and path resolution where required by the consuming flow; do not copy
tools into every repository or create a generic tools framework. Keep real
project differences in configuration rather than product-name conditionals.

Deliverable: an audited ownership/move list, single canonical implementations,
updated callers and focused checks for every affected tool.
#### 4.1. Establish the maintenance boundary for stabilized modules

Document each module's supported public API and expected behavior. Applications
consume an explicit immutable release rather than silently following the base
working tree or a moving branch.

Keep the authorized server-side Sonar corrections. Base Java analysis remains
in `config/sonarqube/`; the original applications and components retain their
own analysis ownership. There is no base frontend source or frontend analysis
module. Preserve existing Sonar projects and their history.

One server and database are owned by `myscoutee-sonarqube`. Project scans submit
to that running instance and never start a second one. Reuse the global scan
lock and keep project report state separate. Analyze canonical base Java once;
keep application-specific code in its existing analysis. Do not scan installed
package distributions or retain source copies for old analysis boundaries.

Add repository guidance that application tasks use the released API. Changes to
stabilized base internals require a task explicitly covering that shared
responsibility, focused verification and a new compatible-set release. Do not
copy a released module into an application to work around this boundary.

Enforce dependency direction and agreed public entry points with focused checks
where the existing tooling supports them. Repository separation and instructions
reduce incidental AI edits; they do not technically prevent an agent with write
access from changing another checkout.

Deliverable: documented ownership/change rules and enforceable import/dependency
boundaries for the extracted modules.

### 5. Qualify the complete extraction after cleanup

Mandatory gate for **each backend and template monolith/KVM check**, and again
for any failure before changing code:

1. Inspect the relevant repositories' Git status and diffs, including staged
   changes, frontend/submodule changes and the corresponding shared owner.
2. Inspect the user's workflow backup commits as well as uncommitted changes.
   A technical extraction error may already be committed: clean status and an
   empty `git diff HEAD` are not evidence that the move preserved behavior.
   Compare the relevant pre-extraction baseline and the intervening backup
   commits, tracing moved/deleted paths across both consumers and shared owners.
   Preserve those commits; do not reset, amend, squash or discard them.
3. Compare moved files with the recorded original source revisions and hashes,
   using Git history for deleted paths. Check required dependency, source-set,
   resource, configuration, import and launcher-path wiring. Do not assume the
   tested source or generated artifact is the intended one.
4. Establish whether the extraction introduced the failure. If it did, correct
   the move/wiring or restore the original implementation and test that same
   boundary again. Preserve the original business behavior.
5. Do not patch business logic, loosen tests, introduce fallback behavior or
   normalize product differences to make extraction checks pass. A defect
   already present in the original implementation is reported separately and
   remains outside this technical extraction; it requires its own later scope.
6. Record the Git/baseline evidence and the exact mechanical correction with
   the check result. A passing check obtained through behavior changes does
   not qualify this extraction.

Complete steps 1–4, including backend, packaging and shared-tool cleanup,
using focused compilation and tests while editing. Only then exercise the retained
business behavior through both template and MyScoutee consumers. Validate the
monolith and KVM distributed deployment with isolated state and available memory. Include
focused runtime checks; compilation alone is insufficient. The frontend remains
in its original application ownership as recorded in step 2.

Follow the active application's resource and test instructions. Reuse running
watchers for compilation; use the authorized backend JUnit runner and frontend
test workflow where applicable. Do not start overlapping build owners or change
manual-QA browser authorization as part of this extraction.

Record exact revisions, artifact versions, passed checks and coverage limits.
Keep a coherent prior application revision/release set for rollback instead of
retaining a duplicate implementation in the current source tree.

Run the expensive monolith/KVM runtime qualification after the complete extraction,
not between technical moves, domains or contextual repositories.

Deliverable: a qualified shared batch demonstrating source ownership, publication,
consumer compatibility, deployment behavior and chunk preservation.

#### 5.1. Accept the extracted release as one compatible set

Apply the extraction pattern across the assessed common code in one coordinated
batch. Resolve actual dependency and product differences without rewriting the
applications. Localization/email establish the wiring; they do not limit the
authorized extraction to a pilot. Keep file moves and ownership changes auditable.

For each completed batch, confirm:

- Extracted business source has one canonical owner in base.
- Consumer application structure and product behavior remain intact.
- All included base artifacts carry the same release version and aligned metadata.
- Binary reuse has recorded provenance and valid effective inputs.
- Monolith builds exclude optional microservices build work.
- Both consumers have relevant compatibility evidence for the released set.

Unextracted code stays in its current location until its extraction batch.
Implementation was authorized on 2026-10-23 and expanded to the complete assessed
common-code extraction before the next expensive runtime qualification. Later
structural redesign remains outside this mechanical move-and-rewire task.
