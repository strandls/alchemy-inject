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

package com.strandls.alchemy.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking Alchemy modules.
 *
 * @author Ashish Shinde
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface AlchemyModule {
    /**
     * The environment the annotated module should be applied in.
     *
     * @author Ashish Shinde
     */
    public static enum Environment {
        /**
         * A wildcard for all/any environment.
         */
        All,
        /**
         * Production environment.
         */
        Prod,
        /**
         * The testing environment.
         */
        Test;

        /**
         * Indicates if the input environment is compatible with this
         * environment.
         *
         * @param env
         *            the input environment.
         * @return <code>true</code> if the input is compatible with this
         *         environment, <code>false</code> otherwise.
         */
        public boolean isCompatible(final Environment env) {
            return this == env || this == All || env == All;
        }

        /**
         * Indicates if this environment is compatible with any of the
         * input environments.
         *
         *
         * @param envs
         *            the input environments.
         * @return <code>true</code> if any of the input is compatible with this
         *         environment, <code>false</code> otherwise.
         */
        public boolean isCompatible(final Environment[] envs) {
            for (final Environment environment : envs) {
                if (isCompatible(environment)) {
                    return true;
                }
            }
            return false;
        }
    }

    Environment[] value();
}
