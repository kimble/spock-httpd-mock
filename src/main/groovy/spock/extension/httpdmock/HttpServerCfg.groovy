package spock.extension.httpdmock;

import java.lang.annotation.*
import org.spockframework.runtime.extension.ExtensionAnnotation
import spock.extension.httpdmock.plugin.simple.SimpleRequestPlugin

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ExtensionAnnotation(HttpServerExtension.class)
public @interface HttpServerCfg {
    
    int port() default 23019
    
    Class[] plugins() default [ SimpleRequestPlugin ]
    
}