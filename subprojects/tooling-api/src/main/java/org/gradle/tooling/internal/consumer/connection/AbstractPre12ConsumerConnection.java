/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.tooling.internal.consumer.connection;

import org.gradle.tooling.internal.adapter.CompatibleIntrospector;
import org.gradle.tooling.internal.adapter.ProtocolToModelAdapter;
import org.gradle.tooling.internal.consumer.converters.ConsumerPropertyHandler;
import org.gradle.tooling.internal.consumer.parameters.ConsumerConnectionParameters;
import org.gradle.tooling.internal.consumer.parameters.ConsumerOperationParameters;
import org.gradle.tooling.internal.consumer.versioning.VersionDetails;
import org.gradle.tooling.internal.protocol.ConnectionVersion4;

/**
 * A connection to a pre 1.2 provider.
 */
public abstract class AbstractPre12ConsumerConnection extends AbstractConsumerConnection {
    protected final ProtocolToModelAdapter adapter;

    public AbstractPre12ConsumerConnection(ConnectionVersion4 delegate, VersionDetails providerMetaData, ProtocolToModelAdapter adapter) {
        super(delegate, providerMetaData);
        this.adapter = adapter;
    }

    public void configure(ConsumerConnectionParameters connectionParameters) {
        new CompatibleIntrospector(getDelegate()).callSafely("configureLogging", connectionParameters.getVerboseLogging());
    }

    public <T> T run(Class<T> type, ConsumerOperationParameters operationParameters) throws UnsupportedOperationException, IllegalStateException {
        if (type.equals(Void.class)) {
            doRunBuild(operationParameters);
            return null;
        } else {
            Class<?> protocolType = getVersionDetails().mapModelTypeToProtocolType(type);
            Object model = doGetModel(protocolType, operationParameters);
            return adapter.adapt(type, model, new ConsumerPropertyHandler(getVersionDetails()));
        }
    }

    protected abstract Object doGetModel(Class<?> protocolType, ConsumerOperationParameters operationParameters);

    protected void doRunBuild(ConsumerOperationParameters operationParameters) {
        getDelegate().executeBuild(operationParameters, operationParameters);
    }
}
