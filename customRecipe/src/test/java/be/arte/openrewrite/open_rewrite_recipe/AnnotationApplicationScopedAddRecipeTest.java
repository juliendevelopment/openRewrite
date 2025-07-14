package be.arte.openrewrite.open_rewrite_recipe;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

public class AnnotationApplicationScopedAddRecipeTest implements RewriteTest
{
	@Override
	public void defaults(RecipeSpec spec)
	{
		spec.recipe(new AnnotationApplicationScopedAddRecipe());
	}

	@Test
	void addApplicationScoped01()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							import javax.inject.Inject;

							@Deprecated
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
						,
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.enterprise.context.ApplicationScoped;
							import javax.inject.Inject;

							@ApplicationScoped
							@Deprecated
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}

	@Test
	void addApplicationScoped02()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							import javax.inject.Inject;

							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
						,
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.enterprise.context.ApplicationScoped;
							import javax.inject.Inject;

							@ApplicationScoped
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}

	@Test
	void addApplicationScoped03()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							import javax.inject.Inject;
							import javax.management.MXBean;
				
							@Deprecated
							@MXBean
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
						,
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.enterprise.context.ApplicationScoped;
							import javax.inject.Inject;
							import javax.management.MXBean;

							@ApplicationScoped
							@Deprecated
							@MXBean
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}

	@Test
	void addApplicationScoped04()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Foo;
							import javax.inject.Inject;
							import javax.management.MXBean;
				
							@Deprecated
							@MXBean
							class FooBar {
							     public Foo foo;
							     @Inject
							     public Bar bar;
							}
						"""
						,
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Foo;
							
							import javax.enterprise.context.ApplicationScoped;
							import javax.inject.Inject;
							import javax.management.MXBean;

							@ApplicationScoped
							@Deprecated
							@MXBean
							class FooBar {
							     public Foo foo;
							     @Inject
							     public Bar bar;
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
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.enterprise.context.ApplicationScoped;
							import javax.inject.Inject;
							import javax.management.MXBean;

							@ApplicationScoped
							@Deprecated
							@MXBean
							class FooBar {
							     @Inject
							     public Bar bar;
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
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.inject.Inject;
							import javax.management.MXBean;

							@Deprecated
							@MXBean
							class FooBar {
							     public Bar bar;
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
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.ejb.EJB;
							import javax.inject.Inject;
							import javax.management.MXBean;

							@Deprecated
							@MXBean
							@EJB
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}

	@Test
	void dontTouch04()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.ejb.LocalHome;
							import javax.inject.Inject;

							@LocalHome
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}

	@Test
	void dontTouch05()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.enterprise.inject.Alternative;
							import javax.inject.Inject;

							@Alternative		
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}

	@Test
	void dontTouch06() {
		rewriteRun(
				java(
						"""
									package be.arte.openrewrite;
								
									import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
								
									import javax.inject.Inject;
								
									abstract class FooBar {
									     @Inject
									     public Bar bar;
									}
								"""
				)
		);
	}
	@Test
	void dontTouch07()
	{
		rewriteRun(
				java(
						"""
							package be.arte.openrewrite;
							
							import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Bar;
							
							import javax.enterprise.context.Dependent;
							import javax.inject.Inject;

							@Dependent
							class FooBar {
							     @Inject
							     public Bar bar;
							}
						"""
				)
		);
	}
}
