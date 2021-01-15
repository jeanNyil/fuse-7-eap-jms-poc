package org.wildfly.camel.examples.cdi;

import org.slf4j.MDC;
import javax.inject.Named;

/**
 * 
 * MDC logging helper 
 *
 */
@Named(value = "mdcHelper")
public class MDCHelper {

	/**
	 * Adds a key, value pair to the current MDC context
	 * @param mdcKey
	 * @param mdcKeyValue
	 */
	public void addToMDC(String mdcKey, String mdcKeyValue) throws Exception {
		MDC.put(mdcKey, mdcKeyValue);
	}

}
