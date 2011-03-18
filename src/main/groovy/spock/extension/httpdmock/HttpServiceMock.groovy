package spock.extension.httpdmock;

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpServiceMock {
    
    Class value()
    
}