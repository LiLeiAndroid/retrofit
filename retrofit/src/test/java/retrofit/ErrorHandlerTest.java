package retrofit;

import org.junit.Before;
import org.junit.Test;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.http.GET;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ErrorHandlerTest {

  interface ExampleClient {
    @GET("/") String throwsCustomException() throws IllegalStateException;
  }


  /* An HTTP client which always returns a 400 response */
  static class MockInvalidResponseClient implements Client {
    @Override
    public Response execute(Request request) throws IOException {
      return new Response(400, "invalid request", Collections.<retrofit.client.Header>emptyList(), null);
    }
  }

  
  ExampleClient client;
  ErrorHandler errorHandler;

  @Before
  public void setup() {
    errorHandler = mock(ErrorHandler.class);

    client = new RestAdapter.Builder()
        .setServer("http://example.com")
        .setClient(new MockInvalidResponseClient())
        .setErrorHandler(errorHandler)
        .setExecutors(new Utils.SynchronousExecutor(), new Utils.SynchronousExecutor())
        .build()
        .create(ExampleClient.class);
  }


  @Test
  public void testCustomizedExceptionThrown() throws Throwable {
    when(errorHandler.propagateOrFallback(any(RetrofitError.class), any(Method.class)))
        .thenThrow(new IllegalStateException("invalid request"));

    try {
      client.throwsCustomException();
      fail();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("invalid request");
    }
  }


  @Test
  public void testFallbackValueReturned() throws Throwable {
    when(errorHandler.propagateOrFallback(any(RetrofitError.class), any(Method.class)))
        .thenReturn("42");

    String result = client.throwsCustomException();

    assertThat(result).isEqualTo("42");
    verify(errorHandler).propagateOrFallback(any(RetrofitError.class),
        eq(ExampleClient.class.getMethod("throwsCustomException")));
  }

}
