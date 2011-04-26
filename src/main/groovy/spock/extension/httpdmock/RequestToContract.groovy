package spock.extension.httpdmock;

import java.lang.annotation.*

/**
 * 
 * @author Kim A. Betti
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestToContract {
    Class value()    
}