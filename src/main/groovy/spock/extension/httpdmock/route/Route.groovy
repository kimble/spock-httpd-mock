package spock.extension.httpdmock.route

import java.util.regex.Matcher
import java.util.regex.Pattern;

/**
 * Support routes with named parameters like /api/user/@username
 * @author Kim A. Betti
 */
class Route {

    final Pattern pattern
    final List paramNames = [ ]
    
    public Route(String path) {
        String regex = path.replaceAll(/(\@[\w]+)/) { str, param ->
            paramNames << param.substring(1)
            return "([^/?&#]+)"
        }
        
        pattern = Pattern.compile("^" + regex + '$')
    }
    
    boolean matches(String path) {
        pattern.matcher(path).matches()
    }
    
    Map params(String path) {
        Map matchedParams = [ : ]
        Matcher m = pattern.matcher(path)
        if (m.matches()) {
            paramNames.eachWithIndex { name, index ->
                matchedParams[name] = m.group(index+1)
            }
        }
        
        return matchedParams
    }
    
}
