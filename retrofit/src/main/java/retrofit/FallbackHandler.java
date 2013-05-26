package retrofit;

import java.lang.reflect.Type;

/**
 * A hook allowing clients to customize error exceptions and callbacks for requests.
 *
 * @author Sam Beran sberan@gmail.com
 */
public interface FallbackHandler {
  /**
   * Called when errors occur during synchronous requests. The implementer
   * may choose to fallbackHandler to an instance of {@code type}, propagate a
   * custom exception, or propagate the original {@code error}.
   * <p/>
   * If the exception is checked, any thrown exceptions must be declared to be
   * thrown on the interface method.
   *
   * @param type the return type of the interface method which invoked this request
   * @param error the original RetrofitError exception
   * @return a fallbackHandler object to be returned from the client interface method
   * @throws Throwable an exception which will be thrown from the client interface method
   */
  Object fallbackOrPropagate(Type type, RetrofitError error) throws Throwable;

  FallbackHandler DEFAULT = new FallbackHandler() {
    @Override
    public Object fallbackOrPropagate(Type type, RetrofitError error) {
      throw error;
    }
  };
}
