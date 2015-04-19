/*
 * Copyright (C) 2015 Alchemy Inject Authors
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

package com.strandls.alchemy.inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Collections2;
import com.google.inject.Module;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.reflect.CachingJavaTypeQueryHandler;
import com.strandls.alchemy.reflect.JavaTypeQueryHandler;

/**
 * Provides a list of Alchemy modules.
 *
 * @author ashish
 */
@Slf4j
public class AlchemyModuleLister {
    /**
     * For querying classes.
     */
    private static final JavaTypeQueryHandler typeQueryHandler;

    /**
     * For applying filter on the classes.
     */
    private static final AlchemyModuleFilterConfiguration filterConfiguration;

    static {
        // This class is used to create injectors all over. Hence the query type
        // handler cannot be injected here.
        typeQueryHandler = new CachingJavaTypeQueryHandler(100000, 1000);
        filterConfiguration = new AlchemyModuleFilterConfiguration();
    }

    /**
     * Get all guice {@link Module}s for a give environment.
     *
     * @param environment
     *            the environment to get modules for. Cannot be
     *            <code>null</code>.
     * @return
     */
    public Collection<Module> getModules(@NonNull final Environment environment) {
        // get all classes with Alchemy module marker
        final Set<Class<?>> classes =
                typeQueryHandler.getTypesAnnotatedWith(".*", AlchemyModule.class);
        final List<Module> modules = new ArrayList<Module>();

        log.debug("Looking for modules in Environment: {}", environment);
        for (final Class<?> klass : classes) {
            try {
                final AlchemyModule marker = klass.getAnnotation(AlchemyModule.class);

                log.debug("Found class {}", klass);
                // match against desired environment
                if (marker != null && environment.isCompatible(marker.value())) {
                    log.debug("For Environment: {} using : {}", environment, klass);
                    modules.add((Module) klass.newInstance());
                } else {
                    log.debug("Ignored class {}", klass);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        // apply the filter and return the result
        return Collections2.filter(modules,
                new AlchemyModuleFilter(filterConfiguration.getFilterConfiguration(environment)));
    }
}
