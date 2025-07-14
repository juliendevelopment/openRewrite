package be.arte.openrewrite.open_rewrite_recipe;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

public class ConstantNameCapitalizationTest implements RewriteTest
{
	@Override
	public void defaults(RecipeSpec spec)
	{
		spec.recipe(new be.arte.openrewrite.open_rewrite_recipe.ConstantNamesAreCapitalizedWithUnderscores());
	}

	@Test
	void renameConst01()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String Test = "test";
							}
						""",
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String TEST = "test";
							}
						"""
				)
		);
	}

	@Test
	void renameConst02()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String tata = "tata";
							}
						""",
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String TATA = "tata";
							}
						"""
				)
		);
	}

	@Test
	void renameConst03()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String SuperTiti = "super_titi";
							}
						""",
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String SUPER_TITI = "super_titi";
							}
						"""
				)
		);
	}

	@Test
	void renameEnumValue01()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							public enum FooBar {
							     Test,
							     machin,
							     TrucTruc,
							     BIDULE
							     ;
							}
						""",
						"""
							package be.arte.openrewrite;

							public enum FooBar {
							     TEST,
							     MACHIN,
							     TRUC_TRUC,
							     BIDULE
							     ;
							}
						"""
				)
		);
	}

	@Test
	void renameEnumValue02()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							public enum FooBar {
							     tyui,
							     azer,
							     truc
							     ;
							}
						""",
						"""
							package be.arte.openrewrite;

							public enum FooBar {
							     TYUI,
							     AZER,
							     TRUC
							     ;
							}
						"""
				)
		);
	}

	@Test
	void dontTouch01()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String TOTO = "toto";
							}
						"""
				)
		);
	}

	@Test
	void dontTouch02()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
							     public static final String TEST_TEST = "test_test";
							}
						"""
				)
		);
	}

	@Test
	void dontTouch03()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							public enum FooBar {
							     MADNESS,
							     CRAZINESS,
							     NESS
							     ;
							}
						"""
				)
		);
	}
}
