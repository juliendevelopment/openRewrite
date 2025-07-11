package be.arte.openrewrite.open_rewrite_recipe;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.openrewrite.ExecutionContext;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;

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
				if(migrationDetected(classDecl)){
					appliMigration(classDecl);
				}
			}
			return super.visitClassDeclaration(classDecl, ctx);
		}

		private void appliMigration(J.ClassDeclaration classDecl) {

		}

		private boolean migrationDetected(J.ClassDeclaration classDecl) {
			return false;
		}

	}
}
