# myscoutee-base

Shared business modules for MyScoutee and its template, built with Gradle at one
compatibility version. UI and technological components remain in
`myscoutee-components`.

- [Numbered extraction plan](guides/development/extraction/SHARED_BUSINESS_MODULES_PLAN.md)
- [Existing source comparison](guides/development/extraction/20261017-template-myscoutee-source-comparison.md)
- [Implementation progress](guides/development/extraction/PROGRESS.md)

Shared backend modules preserve the existing Java packages and behavior.
The applications consume published binary dependencies; their builds do not
compile the base checkout or build microservices adapters.

From `server/`, run `./gradlew publishRelease` to test and stage the complete
compatible module set in `server/build/repository`. The current version is in
`server/gradle.properties`; `-Pversion=...` selects another release version for
the whole set. This stages artifacts locally and does not publish externally.

Both applications pin the base version in `server/gradle/libs.versions.toml`.
The default Maven repository is
`https://maven.pkg.github.com/fssrepository/myscoutee-base`. They import the
`platform` BOM and select individual modules from the same release.
No sibling source checkout or locally staged repository is required.
Set `MYSCOUTEE_BASE_REPOSITORY` or `-PmyscouteeBaseRepository=...` only for an
explicit alternative repository.

Store `gpr.user` and `gpr.key` in the private `~/.gradle/gradle.properties`, or
supply `GITHUB_ACTOR` and `GITHUB_TOKEN`. GitHub Packages requires a token with
package-read permission for consumers. Never commit this credential file.
Docker development mounts it read-only into the Gradle user home; E2E image
builds use a BuildKit secret. `MYSCOUTEE_GITHUB_PACKAGES_PROPERTIES` can select a
different private credential file. Credentials are not copied into image layers.

Push the annotated Git tag matching the shared version, for example `v1.5.0`,
to publish a release. The versioned GitHub Actions workflow checks tag identity,
runs the native module checks, publishes every module and the BOM to GitHub
Packages, and attaches all JARs, a Maven repository ZIP and the SHA-256 manifest
to the corresponding GitHub release. Earlier tags and published versions remain
immutable. `releaseArchive` also produces the same repository ZIP locally.
Runtime monolith/KVM qualification remains a separate checkpoint.

Sonar analysis is configured in `config/sonarqube/`. Run
`./gradlew :jacocoTestReport prepareSonar --parallel` from `server/`, then `./run.sh ../myscoutee-base scan backend` from
`../myscoutee-sonarqube`. This uses the same explicitly started SonarQube server
as the applications. Scanning never builds code or starts another server.
The Gradle report task runs the existing module tests, collects their native
JUnit XML in `server/build/sonar-test-results` and aggregates JaCoCo coverage in
`server/build/reports/jacoco.xml`. The versioned Sonar properties import both.
Generated reports remain ignored; no credentials or generated XML are committed.
