package org.wildfly.camel.examples.cdi;

import org.apache.camel.Exchange;
import org.apache.camel.spi.UnitOfWork;
import org.apache.camel.spi.UnitOfWorkFactory;
import javax.inject.Named;

@Named(value = "customUnitOfWorkFactory")
public class CustomUnitOfWorkFactory implements UnitOfWorkFactory {

  @Override
  public UnitOfWork createUnitOfWork(Exchange exchange) {
    return new CustomMDCUnitOfWork(exchange);
  }

}
