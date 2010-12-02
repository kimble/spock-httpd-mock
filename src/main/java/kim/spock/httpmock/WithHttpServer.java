package kim.spock.httpmock;

import java.lang.annotation.*;
import org.spockframework.runtime.extension.ExtensionAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtensionAnnotation(HttpServerExtension.class)
public @interface WithHttpServer {
	int port() default 11000;
}