# CustomOpenRewriteRecipe

## example of a custom Recipe Created

[AnnotationApplicationScopedAddRecipe](./src/main/java/be/fgov/sfpd/capoe/open_rewrite_recipe/AnnotationApplicationScopedAddRecipe.java)

## documentation :

[rewrite-recipe-starter](https://github.com/moderneinc/rewrite-recipe-starter) stater pack github

[recipes concepts and explanations](https://docs.openrewrite.org/concepts-and-explanations/recipes)

[Recipe development environment](https://docs.openrewrite.org/authoring-recipes/recipe-development-environment) 

[Writing a Java refactoring recipe](https://docs.openrewrite.org/authoring-recipes/writing-a-java-refactoring-recipe)

## Export a custom recipe from the dev environnement :

### Add for graddle (in custom recipes project):
`id 'maven-publish'`

and

```
publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
        }
    }
}
```
### export the jar to your maven local repository

WARNING sfpd proxy can mess up

```shell
./gradlew publishToMavenLocal
```

## Use Custom Recipes in Theseos

Just use the following command and adapt it :

`mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.1.4:run -Drewrite.recipeArtifactCoordinates=be.arte.openrewrite:open_rewrite_recipe:1.2.0 -Drewrite.activeRecipes=be.arte.openrewrite.open_rewrite_recipe.AnnotationApplicationScopedAddRecipe -Drewrite.exportDatatables=true -Drevision=develop-SNAPSHOT -DskipMavenParsing=true`

