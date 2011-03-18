package spock.extension.httpdmock;

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.spockframework.runtime.extension.ExtensionAnnotation

import spock.extension.httpdmock.server.HttpServerExtension

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ExtensionAnnotation(HttpServerExtension.class)
public @interface HttpServerCfg {
    
    int value() default 23019
    
}