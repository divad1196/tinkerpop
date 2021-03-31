/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.server.util;

import org.apache.tinkerpop.gremlin.server.handler.Rexster;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A special {@code ThreadPoolExecutor} which will construct {@link RexsterFutureTask} instances and inject the
 * current running thread into a {@link Rexster} instance if one is present.
 */
public class RexsterExecutorService extends ThreadPoolExecutor {

    public RexsterExecutorService(final int nThreads, final ThreadFactory threadFactory) {
        super(nThreads, nThreads,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        return new RexsterFutureTask<>(runnable, value);
    }


    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        if (r instanceof RexsterFutureTask)
            ((RexsterFutureTask<?>) r).getRexster().ifPresent(rex -> rex.setSessionThread(t));
    }
}
