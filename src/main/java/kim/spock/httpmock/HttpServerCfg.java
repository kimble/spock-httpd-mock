package kim.spock.httpmock;

import java.lang.annotation.*;
import org.spockframework.runtime.extension.ExtensionAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ExtensionAnnotation(HttpServerExtension.class)
public @interface HttpServerCfg {
	int port() default 11000;
}