# openrewrite 

## run an open rewrite recipe

## plugin

usually running a recipe  is a one shot process. so we advise to use the maven plugin run mode

[rewrite-maven-plugin](https://docs.openrewrite.org/reference/rewrite-maven-plugin)

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.1.4:run \
   -Drewrite.recipeArtifactCoordinates=XXX \
   -Drewrite.activeRecipes=xxxx \
   -Drewrite.exportDatatables=true \
   -DskipMavenParsing=true
```

### script

due to the size of monolith application  it's not possible to run a recipe directly on the  root pom.xml . (memory issue)

you can use the script  scripts/RunOnSeparatedModule.sh to run on a specific folder or the root of the repo.

==> update the script to add your command

```shell
scripts/RunOnSeparatedModule.sh
```

### argument

for some reason openrewrite can't parse all the monolith maven to reconstruct the dependencies this is not blocking to run a recipe on the monolith  just skip the maven parsing with and argument

[rewrite-maven-plugin](https://docs.openrewrite.org/reference/rewrite-maven-plugin)

```
 -DskipMavenParsing=true
```

## Official resources

[Documentation officielle](https://docs.openrewrite.org/)

[Recipe catalog](https://docs.openrewrite.org/recipes)

## custom recipe

exemple of custom recipe : CustomOpenRewriteRecipe
