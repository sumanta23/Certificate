package org.sumanta.test.it.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcsnasu on 9/7/2015.
 */
public class PropertyHolder {

    private static PropertyHolder singletonHolder;

    private static Map<String, String> propertyHolder = Collections
            .synchronizedMap(new HashMap<String, String>());

    /*
* A private Constructor prevents any other class from instantiating.
*/
    private PropertyHolder() {
    }

    /* Static 'instance' method */
    public static PropertyHolder getInstance() {
        if (singletonHolder == null) {
            singletonHolder = new PropertyHolder();
        }
        return singletonHolder;
    }

    /**
     * @return the fileholder
     */
    public Map<String, String> getPropertyHolder() {
        return propertyHolder;
    }

}


