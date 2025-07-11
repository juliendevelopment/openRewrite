package be.arte.openrewrite.open_rewrite_recipe;

import org.openrewrite.ExecutionContext;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;

import java.util.Collections;

public class CustomFrameworkRecipe extends Recipe
{

	@Override
	public @NlsRewrite.DisplayName String getDisplayName()
	{
		return "update usage of custom framework to new API ";
	}

	@Override
	public @NlsRewrite.Description String getDescription()
	{
		return "remove usage of old framweork to use the new API instead .";
	}

	@Override
	public JavaIsoVisitor<ExecutionContext> getVisitor()
	{
		return new CustomFrameworkRecipe.AnnotationApplicationScopedAddVisitor();
	}

	private static class AnnotationApplicationScopedAddVisitor extends JavaIsoVisitor<ExecutionContext>
	{

		@Override
		public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx)
		{
			if (classDecl.getBody() != null)
			{
				if(migrationDetected(classDecl, ctx)){
					classDecl = appliMigration(classDecl, ctx);
				}
			}
			return super.visitClassDeclaration(classDecl, ctx);
		}

		private J.ClassDeclaration appliMigration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
			return (J.ClassDeclaration) new UpdateClassVisitor().visit(classDecl, ctx);
		}

		private boolean migrationDetected(J.ClassDeclaration classDecl, ExecutionContext ctx) {
			return new UsesType<>("be.arte.openrewrite.open_rewrite_recipe.class_exemple.Person", false).visit(classDecl, ctx) != classDecl;
		}

	}

	private static class UpdateClassVisitor extends JavaIsoVisitor<ExecutionContext> {
		private final JavaTemplate template = JavaTemplate.builder("@Inject private SignaleticApi signaleticApi;").build();

		@Override
		public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
			classDecl = (J.ClassDeclaration) super.visitClassDeclaration(classDecl, executionContext);
			classDecl = classDecl.withBody(classDecl.getBody().withStatements(template.apply(getCursor(), classDecl.getBody().getCoordinates().firstStatement())));
			return classDecl;
		}

		@Override
		public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext executionContext) {
			method = super.visitMethodInvocation(method, executionContext);
			if (method.getMethodType() != null && method.getMethodType().getName().equals("getModelRoot")) {
				JavaTemplate template = JavaTemplate.builder("signaleticApi.getSignaletic(#{any(long)})").build();
				return template.apply(getCursor(), method.getCoordinates().replace(), method.getArguments().get(1));
			}
			return method;
		}
	}
}

