package be.arte.openrewrite.open_rewrite_recipe;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.ChangeFieldName;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RenameVariable;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Renames constants to match the naming convention of all caps delimited by underscores.
 */
public class ConstantNamesAreCapitalizedWithUnderscores extends Recipe
{

	@Override
	public String getDisplayName()
	{
		return "Capitalize constant names and delimit with underscore";
	}

	@Override
	public String getDescription()
	{
		return "Rename constant values to match naming convention of all caps delimited by underscores.";
	}

	@Override
	public JavaIsoVisitor<ExecutionContext> getVisitor()
	{
		return new ConstantNamesAreCapitalizedWithUnderscores.ConstantNamesAreCapitalsVisitor();
	}

	private static class ConstantNamesAreCapitalsVisitor extends JavaIsoVisitor<ExecutionContext>
	{

		@Override
		public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx)
		{
			if (classDecl.getBody() != null)
			{
				visitApplicableVariableDeclarations(classDecl);
				visitApplicableEnums(classDecl);
			}
			return super.visitClassDeclaration(classDecl, ctx);
		}

		private void visitApplicableVariableDeclarations(J.ClassDeclaration classDecl)
		{
			final List<J.VariableDeclarations> variableDecls =
					classDecl.getBody().getStatements().stream()
							.filter(s -> s instanceof J.VariableDeclarations)
							.map(J.VariableDeclarations.class::cast)
							.filter(v -> v.hasModifier(J.Modifier.Type.Static) && v.hasModifier(J.Modifier.Type.Final))
							.collect(Collectors.toList());

			List<J.VariableDeclarations.NamedVariable> namedVariables = new ArrayList<>();
			for (J.VariableDeclarations variableDeclarations : variableDecls)
			{
				namedVariables.addAll(variableDeclarations.getVariables());
			}

			for (J.VariableDeclarations.NamedVariable namedVariable : namedVariables)
			{
				if (doesNotMatchNamingConvention(namedVariable.getSimpleName()))
				{
					doAfterVisit(
							new ChangeFieldName<>(
									//JavaType.Class.build(classDecl.getSimpleName()),
									JavaType.ShallowClass.build(classDecl.getSimpleName()).getClassName(),
									namedVariable.getSimpleName(),
									convertVarName(namedVariable.getSimpleName()))
					);
					doAfterVisit(
							new RenameVariable<>(namedVariable, convertVarName(namedVariable.getSimpleName()))
					);
				}
			}
		}

		private void visitApplicableEnums(J.ClassDeclaration classDecl)
		{
			final List<J.EnumValueSet> enumValueSets =
					classDecl.getBody().getStatements().stream()
							.filter(e -> e instanceof J.EnumValueSet)
							.map(J.EnumValueSet.class::cast)
							.collect(Collectors.toList());

			List<J.EnumValue> enumValues = new ArrayList<>();
			for (J.EnumValueSet enumValueSet : enumValueSets)
			{
				enumValues.addAll(enumValueSet.getEnums());
			}

			for (J.EnumValue enumValue : enumValues)
			{
				if (doesNotMatchNamingConvention(enumValue.getName().getSimpleName()))
				{
					doAfterVisit(
							new ChangeEnumValueName(
									//JavaType.Class.build(classDecl.getSimpleName()),
									JavaType.ShallowClass.build(classDecl.getType().getFullyQualifiedName()),
									enumValue.getName().getSimpleName(),
									//convertVarName(enumValue.getName().getSimpleName()))
									convertVarName(enumValue.getName().getSimpleName())).getVisitor()
					);
				}
			}
		}

		// Converts camel-case and snake-case to Java constant naming convention with all caps and underscores.
		private String convertVarName(String varName)
		{
			final String regex = "([a-z])([A-Z]+)";
			final String replacement = "$1_$2";
			return varName.replaceAll(regex, replacement).toUpperCase();
		}

		// Matches SonarCube rule RSPEC-115. Reference: https://rules.sonarsource.com/java/RSPEC-115
		private boolean doesNotMatchNamingConvention(String varName)
		{
			final String regex = "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$";
			final Pattern pattern = Pattern.compile(regex);
			return !pattern.matcher(varName).matches();
		}
	}
}