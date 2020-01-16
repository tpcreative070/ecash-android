package vn.ecpay.ewallet.common.base;

public class BaseEventBus {
  public String mRouter;
  public String mEventName;

  public BaseEventBus(String router, String eventName) {
    mRouter = router;
    mEventName = eventName;
  }

  public String getRouter() {
    return mRouter;
  }

  public void setRouter(String mRouter) {
    this.mRouter = mRouter;
  }

  public String getEventName() {
    return mEventName;
  }
}
