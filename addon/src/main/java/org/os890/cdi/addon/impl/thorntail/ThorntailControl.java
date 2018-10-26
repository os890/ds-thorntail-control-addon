package org.os890.cdi.addon.impl.thorntail;

import io.thorntail.Thorntail;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.ContextControl;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.apache.deltaspike.core.util.ExceptionUtils.throwAsRuntimeException;

public class ThorntailControl implements CdiContainer {
  private ContextControl ctxCtrl = null;
  private Bean<ContextControl> ctxCtrlBean = null;
  private CreationalContext<ContextControl> ctxCtrlCreationalContext = null;

  @Override
  public void boot() {
    try {
      Thorntail.run();
    } catch (Exception e) {
      throw throwAsRuntimeException(e);
    }
  }

  @Override
  public void boot(Map<?, ?> map) {
    boot();
  }

  @Override
  public void shutdown() {
    if (ctxCtrl != null) {
      try {
        // stops all built-in contexts except for ApplicationScoped as that one is handled by Weld
        ctxCtrl.stopContext(ConversationScoped.class);
        ctxCtrl.stopContext(RequestScoped.class);
        ctxCtrl.stopContext(SessionScoped.class);
        ctxCtrlBean.destroy(ctxCtrl, ctxCtrlCreationalContext);
      } catch (Exception e) {
        // contexts likely already stopped
      }
    }

    try {
      Thorntail.current().stop();
    } catch (Exception e) {
      throw throwAsRuntimeException(e);
    } finally {
      ctxCtrl = null;
      ctxCtrlBean = null;
      ctxCtrlCreationalContext = null;
    }
  }

  @Override
  public BeanManager getBeanManager() {
    return Thorntail.current().getBeanManager();
  }

  @Override
  public synchronized ContextControl getContextControl() {
    if (ctxCtrl == null) {

      BeanManager beanManager = getBeanManager();

      if (beanManager == null) {
        Logger.getLogger(getClass().getName()).warning("If the CDI-container was started by the environment, you can't use this helper." +
            "Instead you can resolve ContextControl manually " +
            "(e.g. via BeanProvider.getContextualReference(ContextControl.class) ). " +
            "If the container wasn't started already, you have to use CdiContainer#boot before.");

        return null;
      }
      Set<Bean<?>> beans = beanManager.getBeans(ContextControl.class);
      ctxCtrlBean = (Bean<ContextControl>) beanManager.resolve(beans);

      ctxCtrlCreationalContext = getBeanManager().createCreationalContext(ctxCtrlBean);

      ctxCtrl = (ContextControl)
          getBeanManager().getReference(ctxCtrlBean, ContextControl.class, ctxCtrlCreationalContext);
    }
    return ctxCtrl;
  }
}
