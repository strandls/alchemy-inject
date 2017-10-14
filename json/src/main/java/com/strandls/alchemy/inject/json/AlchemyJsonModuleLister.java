/*
 * Copyright (C) 2015 Strand Life Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strandls.alchemy.inject.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.Module;
import com.google.inject.Injector;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.reflect.JavaTypeQueryHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides a list of Alchemy jackson json modules.
 *
 * @author Ashish Shinde
 */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AlchemyJsonModuleLister {
    /**
     * Guice injector for creating instances.
     */
    private final Injector injector;

    /**
     * For querying classes.
     */
    private final JavaTypeQueryHandler typeQueryHandler;

    /**
     * Get all guice {@link Module}s for a give environment.
     *
     * @param environment
     *            the environment to get modules for. Cannot be
     *            <code>null</code>.
     * @return list of json modules matching the environment.
     */
    public Collection<Module> getModules(@NonNull final Environment environment) {
        return getModules(environment, ".*");
    }

    /**
     * Get all guice {@link Module}s for a give environment.
     *
     * @param environment
     *            the environment to get modules for. Cannot be
     *            <code>null</code>.
     * @param packageRegex
     *            package regex to search for json modules in.
     * @return list of json modules matching the environment.
     */
    public Collection<Module> getModules(@NonNull final Environment environment,
            final String packageRegex) {
        // get all classes with Alchemy module marker
        final Set<Class<?>> classes =
                typeQueryHandler.getTypesAnnotatedWith(packageRegex, AlchemyJsonModule.class);
        final List<Module> modules = new ArrayList<Module>();

        log.debug("Looking for json modules in Environment: {}", environment);
        for (final Class<?> klass : classes) {
            final AlchemyJsonModule marker = klass.getAnnotation(AlchemyJsonModule.class);

            // match against desired environment
            if (environment.isCompatible(marker.value())) {
                log.debug("For Environment: {} found : {}", environment, klass);
                modules.add((Module) injector.getInstance(klass));
            }
        }
        return modules;
    }
}
