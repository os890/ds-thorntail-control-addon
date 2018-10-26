package org.os890.cdi.addon.test.thorntail;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class SimpleBean {
  public int getValue() {
    return 42;
  }
}
