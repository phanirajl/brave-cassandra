/*
 * Copyright 2017-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package brave.cassandra.driver;

import brave.internal.Nullable;
import com.datastax.driver.core.Statement;

/**
 * Decides whether to start a new trace based on the cassandra statement.
 *
 * <p>Ex. Here's a sampler that only starts traces for bound statements
 *
 * <pre>{@code
 * cassandraClientTracingBuilder.serverSampler(new CassandraClientSampler() {
 *   @Override public <Req> Boolean trySample(Statement statement) {
 *     return statement instanceof BoundStatement;
 *   }
 * });
 * }</pre>
 */
// abstract class as it lets us make helpers in the future
public abstract class CassandraClientSampler {
  /** Ignores the request and uses the {@link brave.sampler.Sampler trace ID instead}. */
  public static final CassandraClientSampler TRACE_ID =
      new CassandraClientSampler() {
        @Override
        public Boolean trySample(Statement statement) {
          return null;
        }

        @Override
        public String toString() {
          return "DeferDecision";
        }
      };
  /** Returns false to never start new traces for cassandra client requests. */
  public static final CassandraClientSampler NEVER_SAMPLE =
      new CassandraClientSampler() {
        @Override
        public Boolean trySample(Statement statement) {
          return false;
        }

        @Override
        public String toString() {
          return "NeverSample";
        }
      };

  /**
   * Returns an overriding sampling decision for a new trace. Return null ignore the statement and
   * use the {@link brave.sampler.Sampler trace ID sampler}.
   */
  @Nullable
  public abstract Boolean trySample(Statement statement);
}
