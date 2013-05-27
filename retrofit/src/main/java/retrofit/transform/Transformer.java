/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit.transform;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

import java.lang.reflect.Type;

/**
 * Customizes the result of {@link retrofit.converter.Converter#fromBody(retrofit.mime.TypedInput, java.lang.reflect.Type) fromBody}.
 *
 * @param <F> {@code type} of {@link retrofit.converter.Converter#fromBody(retrofit.mime.TypedInput, java.lang.reflect.Type) fromBody}.
 * @author Adrian Cole (adrianc@netflix.com)
 */
public interface Transformer<F> {
  /**
   * Converts the result of {@link retrofit.converter.Converter#fromBody(retrofit.mime.TypedInput, java.lang.reflect.Type) fromBody},
   * taking into consideration the HTTP response.
   *
   * @param response      HTTP response.
   * @param fromConverter {@link retrofit.converter.Converter#fromBody(retrofit.mime.TypedInput, java.lang.reflect.Type) fromBody}.
   * @param to            Target object type.
   * @return Instance of {@code type} which will be cast by the caller.
   * @throws retrofit.transform.TransformationException if transformation was unable to complete.
   */
  Object transform(Response response, F fromConverter, Type to) throws TransformationException;
}
