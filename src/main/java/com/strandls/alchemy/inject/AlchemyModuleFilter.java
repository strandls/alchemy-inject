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

import java.util.Set;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Predicate;
import com.google.inject.Module;

/**
 * Filters out moudles. This allows more granular control over modules for an
 * environment. The input to this module is a list of regexes / class names to
 * filter out. A module whose canonical class name matches <b>ANY</b> one of
 * the regex will be filtered out.
 *
 * @author Ashish Shinde
 *
 */
@RequiredArgsConstructor
@Slf4j
public class AlchemyModuleFilter implements Predicate<Module> {
    /**
     * List of regexs for class names to filter out.
     */
    @NonNull
    private final Set<String> toFilterOut;

    /*
     * (non-Javadoc)
     * @see com.google.common.base.Predicate#apply(java.lang.Object)
     */
    @Override
    public boolean apply(final Module input) {
        final String className = input.getClass().getName();
        for (final String classRegex : toFilterOut) {
            if (className.matches(classRegex)) {
                // matches the regex. This module should be filtered out.
                log.info("Filtered out module: {}", input);
                return false;
            }
        }
        // does not match any regex retain the module.
        return true;
    }
}
