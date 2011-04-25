package spock.extension.httpdmock

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.codehaus.groovy.transform.GroovyASTTransformationClass

/**
 * Used to annotate end-point implementations. 
 * For example @EndpointRoute("/user/@username")
 * @author Kim A. Betti
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndpointRoute {
    String value()
}
