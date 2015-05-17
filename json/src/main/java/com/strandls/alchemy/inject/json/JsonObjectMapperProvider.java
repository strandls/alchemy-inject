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

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

/**
 * A {@link Provider} for jackson {@link ObjectMapper} that ensure all required
 * {@link Module} for the configured {@link Environment} are installed.
 *
 * <p>
 * For configuring the {@link Feature} bind an instance of {@link JsonFactory}.
 * </p>
 *
 * @author Ashish Shinde
 *
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
@Slf4j
public class JsonObjectMapperProvider implements Provider<ObjectMapper> {
    /**
     * Injected desired environment.
     */
    private final Environment environment;

    /**
     * Inject the lister.
     */
    private final AlchemyJsonModuleLister moduleLister;

    /*
     * (non-Javadoc)
     * @see com.google.inject.Provider#get()
     */
    @Override
    @Singleton
    public ObjectMapper get() {
        final ObjectMapper mapper = new ObjectMapper();
        final Collection<Module> modules = moduleLister.getModules(environment);
        for (final Module module : modules) {
            mapper.registerModule(module);
            log.debug("For mapper {} Register module {}", mapper, module);
        }
        log.debug("For env {} returned mapper {}", environment, mapper);
        return mapper;
    }

}
