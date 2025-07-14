package be.arte.openrewrite.open_rewrite_recipe;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class HelloWorldRecipeTest implements RewriteTest {
	@Override
	public void defaults(RecipeSpec spec) {
		spec.recipe(new HelloWorldRecipe("be.arte.openrewrite.FooBar"));
	}

	//!!! space != of tabulation for test
	@Test
	void addsHelloToFooBar() {
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
							}
						""",
						"""
							package be.arte.openrewrite;

							class FooBar {
							    public String hello() {
							        return "Hello from be.arte.openrewrite.FooBar!";
							    }
							}
						"""
				)
		);
	}

	@Test
	void doesNotChangeExistingHello() {
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class FooBar {
								public String hello() { return ""; }
							}
						"""
				)
		);
	}

	@Test
	void doesNotChangeOtherClasses() {
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;

							class Bash {
							}
						"""
				)
		);
	}
}