package kim.spock.httpmock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.MethodInfo;
import org.spockframework.runtime.model.SpecInfo;

public class HttpServerExtension extends AbstractAnnotationDrivenExtension<WithHttpServer> {

	@Override
	public void visitSpecAnnotation(WithHttpServer annotation, SpecInfo spec) {
		for (FeatureInfo feature : spec.getAllFeatures())
			addInterceptor(annotation, feature.getFeatureMethod());
	}

	@Override
	public void visitFeatureAnnotation(WithHttpServer httpServer, FeatureInfo feature) {
		MethodInfo featureMethod = feature.getFeatureMethod();
		addInterceptor(httpServer, featureMethod);
	}
	
	private void addInterceptor(WithHttpServer httpServer, MethodInfo featureMethod) {
		featureMethod.addInterceptor(new HttpServerInterceptor(httpServer));
	}
	
}
