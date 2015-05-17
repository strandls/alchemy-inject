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

package com.strandls.alchemy.reflect;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.google.inject.ImplementedBy;

/**
 * Handles queries on java types.
 *
 * @author Ashish Shinde
 *
 */
@ImplementedBy(CachingJavaTypeQueryHandler.class)
public interface JavaTypeQueryHandler {
    /**
     * Get the all subtypes of input type from pacakges matching the
     * packageRegex.
     *
     * @param packageRegex
     *            the regular expression for the packages to search for classes
     *            in.
     * @param type
     *            the required super type.
     * @return
     */
    <T> Set<Class<? extends T>> getSubTypesOf(final String packageRegex, final Class<T> type);

    /**
     * Get types annotated with a given annotation, both classes and annotations
     * <p>
     * {@link java.lang.annotation.Inherited} is honored.
     * <p>
     * When honoring @Inherited, meta-annotation should only effect annotated
     * super classes and its sub types
     * <p>
     * <i>Note that this (@Inherited) meta-annotation type has no effect if the
     * annotated type is used for anything other than a class. Also, this
     * meta-annotation causes annotations to be inherited only from
     * superclasses; annotations on implemented interfaces have no effect.</i>
     * <p/>
     */
    Set<Class<?>> getTypesAnnotatedWith(final String packageRegex,
            final Class<? extends Annotation> annotation);
}
