Modify CHANGELOG.md

```sh
# Generate next CHANGELOG, and modify CHANGELOG.md
$ ghch --format=markdown --next-version=...
$ git add CHANGELOG.md && git commit -m "Modify CHANGELOG.md"
```

Release by `sbt release`

```sh
$ sbt
sbt:joda-time-fake> release
```
