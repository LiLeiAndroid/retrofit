package retrofit;

import java.lang.reflect.Method;

/**
 * A hook allowing clients to customize error exceptions and callbacks for requests.
 *
 * @author Sam Beran sberan@gmail.com
 */
public interface ErrorHandler {
  /**
   * Called when errors occur during synchronous requests. The implementer
   * may choose to propagate a custom exception, or return a fallback value.
   *
   * If the exception is checked, any thrown exceptions must be declared to be
   * thrown on the interface method.
   *
   * @param cause the original RetrofitError exception
   * @param interfaceMethod the client interface method which invoked this request
   * @return a fallback object to be returned from the client interface method
   * @throws Throwable an exception which will be thrown from the client interface method
   */
  Object propagateOrFallback(RetrofitError cause, Method interfaceMethod) throws Throwable;

  ErrorHandler DEFAULT = new ErrorHandler() {
    @Override   public Object propagateOrFallback(RetrofitError e, Method interfaceMethod) {
      throw e;
    }
  };
}
