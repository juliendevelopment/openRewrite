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

public class AnnotationApplicationScopedAddRecipe extends Recipe
{

	@Override
	public @NlsRewrite.DisplayName String getDisplayName()
	{
		return "Add @javax.enterprise.context.ApplicationScoped with specific condition";
	}

	@Override
	public @NlsRewrite.Description String getDescription()
	{
		return "Add @javax.enterprise.context.ApplicationScoped to class which use @javax.inject.Inject but doesn't have @ApplicationScoped or alternative.";
	}

	@Override
	public JavaIsoVisitor<ExecutionContext> getVisitor()
	{
		return new AnnotationApplicationScopedAddRecipe.AnnotationApplicationScopedAddVisitor();
	}

	private static class AnnotationApplicationScopedAddVisitor extends JavaIsoVisitor<ExecutionContext>
	{
		private final String importComponent = "javax.enterprise.context.ApplicationScoped";
		String[] split = this.importComponent.split("\\.");
		String className = split[split.length - 1];
		String packageName = this.importComponent.substring(0, this.importComponent.lastIndexOf("."));
		String interfaceAsString = String.format("package %s\npublic @interface %s {}", packageName, className);
		private final JavaTemplate componentAnnotationTemplate = JavaTemplate.builder("@ApplicationScoped")
				.javaParser(JavaParser.fromJavaVersion()
									.dependsOn(interfaceAsString))
				.imports(importComponent).build();

		@Override
		public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx)
		{
			J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, ctx);
			if (classDecl.getBody() != null)
			{
				if (doesInjectAnnotationExist(classDecl))
				{
					if (!doesApplicationScopedExist(classDecl) && !doesAnAlternativeAnnotationExist(classDecl) && !isClassAbstract(classDecl))
					{

						maybeAddImport(importComponent);
						cd = componentAnnotationTemplate.apply(getCursor(), cd.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName)));
						cd = maybeAutoFormat(classDecl, cd, cd.getName(), ctx, getCursor().getParent());
					}
				}
			}
			return cd;
		}

		private boolean doesInjectAnnotationExist(J.ClassDeclaration classDecl)
		{
			return classDecl.getBody().getStatements().stream()
							.filter(s -> s instanceof J.VariableDeclarations)
							.map(J.VariableDeclarations.class::cast)
							.anyMatch(variableDeclarations -> variableDeclarations.getLeadingAnnotations().stream().anyMatch(annotation -> annotation.getSimpleName().equals("Inject")));
		}

		private boolean doesApplicationScopedExist(J.ClassDeclaration classDecl)
		{
			return classDecl.getLeadingAnnotations().stream().anyMatch(annotation -> annotation.getSimpleName().equals("ApplicationScoped"));
		}

		private boolean doesAnAlternativeAnnotationExist(J.ClassDeclaration classDecl)
		{
			boolean flag = false;
			List<String> packageToExclude = Arrays.asList(
					"javax.ejb.",
					"javax.enterprise.inject.",
					"javax.enterprise.context."

			);
			List<String> javaxEjbAnnotations = Arrays.asList(
														//javax.ejb. part
															 "ActivationConfigProperty",
															 "ApplicationException",
															 "EJB",
															 "EJBs",
															 "Init",
															 "Local",
															 "LocalHome",
															 "MessageDriven",
															 "PostActivate",
															 "PrePassivate",
															 "Remote",
															 "RemoteHome",
															 "Remove",
															 "Stateful",
															 "Stateless",
															 "Timeout",
															 "TransactionAttribute",
															 "TransactionManagement",
														//javax.enterprise.inject. part
															"Any",
															"Default",
															"Disposes",
															"Instance",
															"New",
															"Produces",
															"Specializes",
															"Typed",
															"Alternative",
															"BeanManager",
															"Interceptor",
															"Vetoed",
														//javax.enterprise.context. part
															"ConversationScoped",
															"Dependent",
															"Initialized",
															"Destroyed",
															"NormalScope",
															"RequestScoped",
															"SessionScoped"
			);
			for (J.Annotation annotation : classDecl.getLeadingAnnotations())
			{
				if (javaxEjbAnnotations.contains(annotation.getSimpleName()))
				{
					flag = true;
				}
			}
			return flag;
		}

		private boolean isClassAbstract(J.ClassDeclaration classDecl)
		{
			boolean flag = false;
			for (J.Modifier modifier : classDecl.getModifiers())
			{
				if (modifier.toString().equals("abstract")) {
					flag = true;
					break;
				}
			}
			return flag;
		}
	}
}
