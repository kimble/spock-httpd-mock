package kim.spock.httpmock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.FieldInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

/**
 * A test http server will be started for every feature 
 * method in a spec with a test server field.
 * @author Kim A. Betti
 */
public class HttpServerExtension extends AbstractAnnotationDrivenExtension<HttpServerCfg> {
	
	@Override
	public void visitFieldAnnotation(HttpServerCfg annotation, FieldInfo field) {
		SpecInfo spec = field.getParent();
		for (FeatureInfo feature : spec.getAllFeatures()) 
			addInterceptor(annotation, feature.getFeatureMethod(), field);
	}

	private void addInterceptor(HttpServerCfg httpServer, MethodInfo featureMethod, FieldInfo serverField) {
		featureMethod.addInterceptor(new HttpServerInterceptor(httpServer, serverField));
	}
	
}