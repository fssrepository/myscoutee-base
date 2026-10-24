# MyScoutee Base agent instructions

Read and apply the existing sibling
[`myscoutee-skills` development rules](../myscoutee-skills/instructions/myscoutee-development.md)
before engineering work, including the CPU scheduling and single-owner rules.
Do not automatically pull or replace that checkout.

Follow [the numbered extraction plan](guides/development/extraction/SHARED_BUSINESS_MODULES_PLAN.md). Base owns
shared business functionality; `myscoutee-components` owns UI/technology building
blocks. Keep the existing-style source hierarchy and Java namespaces.

During extraction, move existing common implementations and rewire consumers.
Do not redesign business logic or refactor the main applications to fit the
library. Frontend implementations remain in the consuming applications; do not
create a frontend/npm package in base. The user cancelled that extraction.

Every released base module shares one compatibility version. Applications use
versioned binary dependencies. Optional distributed-transport modules must not
enter monolith dependency/task graphs.

Treat stabilized business implementations as explicit maintenance boundaries.
An application-only task does not authorize modifying base internals or copying
them back into an application. A base behavior change requires task scope that
explicitly covers that shared responsibility and verification of its consumers.

For backend/template monolith or KVM qualification, inspect Git status/diffs and
the original source-revision/hash baseline before testing and before repairing
any failure. Correct extraction/dependency/path wiring errors at their source.
Do not change application behavior, add fallbacks or loosen tests to make this
technical extraction pass. Report pre-existing defects separately for later work.

The user creates workflow backup commits. Include the relevant committed
extraction changes when comparing with the recorded pre-extraction baseline;
a clean working tree does not prove correctness. Preserve user backup commits
and do not reset, amend, squash or discard them.

After each extraction, remove the emptied original source directories in both
consumers from the filesystem as well as removing source copies. Git status
cannot show an empty directory skeleton. Preserve non-empty inputs and actual
build/private-state owners.
