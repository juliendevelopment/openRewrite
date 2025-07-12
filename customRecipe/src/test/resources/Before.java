import javax.enterprise.context.ApplicationScoped;

import be.arte.openrewrite.open_rewrite_recipe.class_exemple.HibernateMetaModelCache;
import be.arte.openrewrite.open_rewrite_recipe.class_exemple.IntegratedDossier;

@ApplicationScoped
public class Service {

	public String getLastName(long dossierId) {
		IntegratedDossier dossier =  new HibernateMetaModelCache().getModelRoot(IntegratedDossier.class, dossierId);
		return dossier.getPerson().getLastName();
	}
}
