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

package com.strandls.alchemy.reflect;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.inject.AlchemyModuleLister;

/**
 * Module that assembles testing modules.
 *
 * @author ashish
 *
 */
public class InjectTestModule extends AbstractModule {

    /*
     * (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // install all testing modules.
        for (final Module module : new AlchemyModuleLister().getModules(Environment.Test)) {
            install(module);
        }
        install(new GuiceBerryModule());
        bind(Environment.class).toInstance(Environment.Test);
    }
}
