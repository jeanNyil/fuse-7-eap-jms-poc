package org.wildfly.camel.examples.cdi;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.MDCUnitOfWork;
import org.apache.camel.spi.UnitOfWork;
import org.slf4j.MDC;

/**
 * Custom persistent MDCUnitOfWork with accountId and orgId in addition to default Camel MDC keys
 */
public class CustomMDCUnitOfWork extends MDCUnitOfWork {
	
	public static final String MDC_ACCOUNTID = "accountId";
	private String accountId;

	public CustomMDCUnitOfWork(Exchange exchange) {
		super(exchange);
		this.accountId = MDC.get(MDC_ACCOUNTID);		
	}
	
	@Override
    public UnitOfWork newInstance(Exchange exchange) {
		return new CustomMDCUnitOfWork(exchange);
	}
	
	@Override
	public void clear() {
	    super.clear();

	    if (accountId != null) {
	        MDC.put(MDC_ACCOUNTID, accountId);
	    } else {
	        MDC.remove(MDC_ACCOUNTID);
	    }
	}

	@Override
	public void stop() throws Exception {
	    super.stop();
	    clear();
	}
	
	@Override
	public AsyncCallback beforeProcess(Processor processor, Exchange exchange, AsyncCallback callback) {
	    return new CustomMDCCallback(callback);
	}
	
	/**
	 ** {@link AsyncCallback} which preserves {@link org.slf4j.MDC} when the
	 * asynchronous routing engine is being used. * This also includes the
	 * default camel MDCs.
	 */
	private static final class CustomMDCCallback implements AsyncCallback {
	    private final AsyncCallback delegate;
	    private final String breadcrumbId;
	    private final String exchangeId;
	    private final String messageId;
	    private final String correlationId;
	    private final String routeId;
	    private final String camelContextId;
	    private final String accountId;

	    private CustomMDCCallback(AsyncCallback delegate) {
	    	this.delegate = delegate;
	    	this.exchangeId = MDC.get(MDC_EXCHANGE_ID);
	    	this.messageId = MDC.get(MDC_MESSAGE_ID);
			this.breadcrumbId = MDC.get(MDC_BREADCRUMB_ID);
			this.correlationId = MDC.get(MDC_CORRELATION_ID);
			this.camelContextId = MDC.get(MDC_CAMEL_CONTEXT_ID);
			this.routeId = MDC.get(MDC_ROUTE_ID);
			this.accountId = MDC.get(MDC_ACCOUNTID);
	    }

	    public void done(boolean doneSync) {
	    	try {
	    		if (!doneSync) {
					// when done asynchronously then restore information from
					// previous thread
	    			checkAndPut(breadcrumbId, MDC_BREADCRUMB_ID);
	    			checkAndPut(exchangeId, MDC_EXCHANGE_ID);
	    			checkAndPut(messageId, MDC_MESSAGE_ID);
	    			checkAndPut(correlationId, MDC_CORRELATION_ID);
	    			checkAndPut(camelContextId, MDC_CAMEL_CONTEXT_ID);
			        checkAndPut(accountId, MDC_ACCOUNTID);

				}
				// need to setup the routeId finally
	    		checkAndPut(routeId, MDC_ROUTE_ID);
			} finally {
				// must ensure delegate is invoked
				delegate.done(doneSync);
		    }
	    	
		}
	    
	    private void checkAndPut(String value, String fieldName) {
            if (value != null) {
                MDC.put(fieldName, value);
            }
        }
	
		@Override
		public String toString() {
			return delegate.toString();
		}
	}
}
