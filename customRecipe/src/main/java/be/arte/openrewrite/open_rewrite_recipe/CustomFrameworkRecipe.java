package be.arte.openrewrite.open_rewrite_recipe;

import org.openrewrite.ExecutionContext;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;

public class CustomFrameworkRecipe extends Recipe {

	@Override
	public @NlsRewrite.DisplayName String getDisplayName() {
		return "update usage of custom framework to new API ";
	}

	@Override
	public @NlsRewrite.Description String getDescription() {
		return "remove usage of old framweork to use the new API instead .";
	}

	@Override
	public JavaIsoVisitor<ExecutionContext> getVisitor() {
		return new CustomFrameworkRecipe.AnnotationApplicationScopedAddVisitor();
	}

	private static class AnnotationApplicationScopedAddVisitor extends JavaIsoVisitor<ExecutionContext> {

		@Override
		public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
			J.ClassDeclaration classDeclc = super.visitClassDeclaration(classDecl, ctx);
			if (isUsingLegacyFramework(classDecl, ctx)) {
				classDecl = addInjectOfService(classDecl);
				maybeAddImport("javax.inject.Inject");
				maybeAddImport("be.arte.openrewrite.open_rewrite_recipe.class_exemple.SignaleticApi");
			}
			return classDecl;
		}

		@Override
		public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
			method = super.visitMethodDeclaration(method, ctx);

			if (method.getBody() == null) {
				return method;
			}

			boolean needsMigration = method.getBody().getStatements().stream()
					.anyMatch(statement -> statement.print(getCursor()).contains("HibernateMetaModelCache"));

			if (needsMigration) {
				JavaTemplate newBodyTemplate = JavaTemplate.builder(
								"{\n" +
								"    Signaletic signaletic = signaleticApi.getSignaletic(#{any(long)});\n" +
								"    return signaletic.getLastName();\n" +
								"}\n"
						)
						.imports("be.arte.openrewrite.open_rewrite_recipe.class_exemple.Signaletic")
						.build();

				method = newBodyTemplate.apply(
						getCursor(),
						method.getBody().getCoordinates().replace(),
						method.getParameters().get(0)
				);

				maybeAddImport("be.arte.openrewrite.open_rewrite_recipe.class_exemple.Signaletic");
				maybeRemoveImport("be.arte.openrewrite.open_rewrite_recipe.class_exemple.HibernateMetaModelCache");
				maybeRemoveImport("be.arte.openrewrite.open_rewrite_recipe.class_exemple.IntegratedDossier");

			}
			return method;
		}

		private J.ClassDeclaration addInjectOfService(J.ClassDeclaration classDecl) {
			JavaTemplate template = JavaTemplate.builder("\t@Inject\n" +
														 "\tprivate SignaleticApi signaleticApi;"
			).build();

			classDecl = template.apply(
					getCursor(),
					classDecl.getBody().getCoordinates().firstStatement()
			);
			return classDecl;
		}

		private boolean isUsingLegacyFramework(J.ClassDeclaration classDecl, ExecutionContext ctx) {
			J.CompilationUnit cu = getCursor().firstEnclosing(J.CompilationUnit.class);
			return cu.getImports().stream()
					.anyMatch(imp -> {
						String importName = imp.getQualid().toString();
						return importName.equals("be.arte.openrewrite.open_rewrite_recipe.class_exemple.HibernateMetaModelCache");
					});
		}

	}
}

