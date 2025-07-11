package be.arte.openrewrite.open_rewrite_recipe;

import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.TypeUtils;

public class ChangeEnumValueName extends Recipe
{
	private final JavaType.ShallowClass classType;
	private final String hasName;
	private final String toName;

	public ChangeEnumValueName(JavaType.ShallowClass classType, String hasName, String toName)
	{
		assert !StringUtils.isBlank(hasName);
		assert !StringUtils.isBlank(toName);

		this.classType = classType;
		this.hasName = hasName;
		this.toName = toName;
	}

	@Override
	public String getDisplayName()
	{
		return "Change enum value names";
	}

	@Override
	public String getDescription()
	{
		return "Change the name of an enum value from hasName with toName.";
	}

	@Override
	public JavaIsoVisitor<ExecutionContext> getVisitor()
	{
		return new ChangeEnumValueName.ChangeEnumValueNameVisitor();
	}

	private class ChangeEnumValueNameVisitor extends JavaIsoVisitor<ExecutionContext>
	{

		@Override
		public J.EnumValue visitEnumValue(J.EnumValue _enum, ExecutionContext ctx)
		{
			J.EnumValue e = super.visitEnumValue(_enum, ctx);
			Cursor cursor = getCursor();
			while (!(cursor.getValue() instanceof J.ClassDeclaration))
			{
				cursor = getParent(cursor);
			}

			if (TypeUtils.isOfClassType(((J.ClassDeclaration)cursor.getValue()).getType(), classType.getFullyQualifiedName()))
			{
				if (_enum.getName().getSimpleName().equals(hasName))
				{
					//e = e.withName(e.getName().withName(toName));
					e = e.withName(e.getName().withSimpleName(toName));
				}
			}
			return e;
		}

		@Override
		public J.FieldAccess visitFieldAccess(J.FieldAccess fieldAccess, ExecutionContext ctx) {
			J.FieldAccess f = super.visitFieldAccess(fieldAccess, ctx);
			if (matchesClass(fieldAccess.getTarget().getType())
				&& fieldAccess.getSimpleName().equals(hasName)) {
				//f = f.getPadding().withName(f.getPadding().getName().withElement(f.getPadding().getName().getElement().withName(toName)));
				f = f.getPadding().withName(f.getPadding().getName().withElement(f.getPadding().getName().getElement().withSimpleName(toName)));
			}
			return f;
		}

		private Cursor getParent(Cursor cursor) {
			return cursor.dropParentUntil(J.class::isInstance);
		}

		private boolean matchesClass(@Nullable JavaType test) {
			JavaType.Class testClassType = TypeUtils.asClass(test);
			return testClassType != null && testClassType.getFullyQualifiedName().equals(classType.getFullyQualifiedName());
		}
	}
}