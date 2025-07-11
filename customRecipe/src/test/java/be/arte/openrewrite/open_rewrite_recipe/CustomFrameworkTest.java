package be.arte.openrewrite.open_rewrite_recipe;

import static org.openrewrite.java.Assertions.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class CustomFrameworkTest implements RewriteTest {

	@Override
	public void defaults(RecipeSpec spec) {
		spec.recipe(new CustomFrameworkRecipe());
	}

	private String getSource(String path) throws IOException, URISyntaxException {
		try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
			return new BufferedReader(new InputStreamReader(inputStream))
					.lines()
					.collect(Collectors.joining("\n"));
		} catch (IOException e) {
			return null;
		}
	}

	@Test
	void update_framework() throws IOException, URISyntaxException {
		rewriteRun(java(
				getSource("/Before.java"),
				getSource("/After.java")
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
