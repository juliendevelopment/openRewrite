package be.arte.openrewrite.open_rewrite_recipe.customRecipe.object;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import be.arte.openrewrite.open_rewrite_recipe.class_exemple.Signaletic;
import be.arte.openrewrite.open_rewrite_recipe.class_exemple.SignaleticApi;

@ApplicationScoped
public class After {

	@Inject
	private SignaleticApi signaleticApi;

	public String getLastName(long dossierId) {
		Signaletic signaletic = signaleticApi.getSignaletic(dossierId);
		return signaletic.getLastName();
	}
}
