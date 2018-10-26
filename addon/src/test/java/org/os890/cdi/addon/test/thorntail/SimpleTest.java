package org.os890.cdi.addon.test.thorntail;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(CdiTestRunner.class)
public class SimpleTest {
  @Inject
  private SimpleBean simpleBean;

  @Test
  public void thorntailInjection() {
    assertNotNull(simpleBean);
    assertEquals(42, simpleBean.getValue());
  }
}
