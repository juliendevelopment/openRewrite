package be.arte.openrewrite.open_rewrite_recipe;

import static org.openrewrite.java.Assertions.java;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class CustomFrameworkTest implements RewriteTest {

	@Override
	public void defaults(RecipeSpec spec) {
		spec.recipe(new CustomFrameworkRecipe());
	}

	private String getSource(String path) throws IOException, URISyntaxException {
		return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(path).toURI())));
	}

	@Test
	void update_framework() throws IOException, URISyntaxException {
		rewriteRun(java(
				getSource("Before.java"),
				getSource("After.java")
		));
	}

	@Test
	void no_change() throws IOException, URISyntaxException {
		rewriteRun(
				java("""
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

}
