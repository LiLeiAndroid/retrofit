package retrofit;

import org.junit.Test;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.http.Fallback;
import retrofit.http.GET;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class FallbackHandlerTest {

  static interface CustomExceptionClient {
    @GET("/")
    String throwsCustomException() throws IllegalStateException;
  }

  @Test
  public void testCustomizedExceptionThrown() {

    class InvalidRequestClient implements Client {
      public Response execute(Request request) throws IOException {
        return new Response(400, "invalid request", Collections.<retrofit.client.Header>emptyList(), null);
      }
    }

    class Coerce400ToISE implements FallbackHandler {
      public Object fallbackOrPropagate(Type type, RetrofitError error) throws Throwable {
        if (error.getResponse().getStatus() == 400)
          throw new IllegalStateException("invalid request");
        throw error;
      }
    }

    CustomExceptionClient client = new RestAdapter.Builder()
        .setServer("http://example.com")
        .setClient(new InvalidRequestClient())
        .setFallbackHandler(new Coerce400ToISE())
        .setExecutors(new Utils.SynchronousExecutor(), new Utils.SynchronousExecutor())
        .build()
        .create(CustomExceptionClient.class);

    try {
      client.throwsCustomException();
      fail();
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("invalid request");
    }
  }

  static class TrueOn404 implements FallbackHandler  {
    public Object fallbackOrPropagate(Type type, RetrofitError error) throws Throwable {
      if (error.getResponse().getStatus() == 404 && type == boolean.class)
        return true;
      throw error;
    }
  }

  static class FalseOn404 implements FallbackHandler  {
    public Object fallbackOrPropagate(Type type, RetrofitError error) throws Throwable {
      if (error.getResponse().getStatus() == 404 && type == boolean.class)
        return false;
      throw error;
    }
  }

  static interface FallbackClient {
    @GET("/")
    boolean globalFallback();

    @GET("/")
    @Fallback(FalseOn404.class)
    boolean methodFallback();
  }

  @Test
  public void testFallBack() {

    class NotFoundClient implements Client {
      public Response execute(Request request) throws IOException {
        return new Response(404, "Not Found", Collections.<retrofit.client.Header>emptyList(), null);
      }
    }

    FallbackClient client = new RestAdapter.Builder()
        .setServer("http://example.com")
        .setClient(new NotFoundClient())
        .setFallbackHandler(new TrueOn404())
        .setExecutors(new Utils.SynchronousExecutor(), new Utils.SynchronousExecutor())
        .build()
        .create(FallbackClient.class);

    assertTrue(client.globalFallback());
    assertFalse(client.methodFallback());
  }
}
