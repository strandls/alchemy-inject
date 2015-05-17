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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.util.FilterBuilder;
import org.reflections.vfs.Vfs.File;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Caching implementation of {@link JavaTypeQueryHandler} that uses
 * {@link Reflections} behind the scenes. {@link Reflections} does not cache
 * results making it slow. This implementation will speed up class queries
 * because a lot of different classes would fire similar queries.
 *
 * @author Ashish Shinde
 *
 */
@Singleton
@Slf4j
public class CachingJavaTypeQueryHandler implements JavaTypeQueryHandler {
    /**
     * Represents a query for an Annotation.
     *
     * @author Ashish Shinde
     *
     */
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class AnnotationQuery {
        /**
         * The annotation being searched for.
         */
        private final Class<? extends Annotation> annotation;
        /**
         * The package pattern.
         */
        private final String packageRegex;
    }

    /**
     * Filter clases objects that are not safe to load.
     *
     * @author Ashish Shinde
     *
     */
    private static final class ClassObjectFilter {
        public boolean isSafe(final File file, final Object classObject,
                @SuppressWarnings("rawtypes") final MetadataAdapter metadataAdaptor) {
            @SuppressWarnings("unchecked")
            final String className = metadataAdaptor.getClassName(classObject);
            final String relativePath = file.getRelativePath();
            if (!className.replaceAll("\\.", "/").equals(relativePath.replaceAll("\\.class$", ""))) {
                log.warn("Class path file path mismatch. Ignoring {}", file);
                return false;
            }
            return true;
        }
    }

    /**
     * Represents a query for a subtype.
     *
     * @author Ashish Shinde
     */
    @EqualsAndHashCode
    @RequiredArgsConstructor
    @ToString
    private static class SubTypeQuery {
        /**
         * The package pattern.
         */
        private final String packageRegex;
        /**
         * The super type.
         */
        @SuppressWarnings("rawtypes")
        private final Class type;
    }

    /**
     * A scanner that ignore classes that cannot be loaded by the class loader.
     * The name has to be SubTypesScanner because reflections uses that as the
     * cache key.
     *
     * @author Ashish Shinde
     *
     */
    private static final class SubTypesScanner extends org.reflections.scanners.SubTypesScanner {
        /**
         * Filter for class objects.
         */
        private final ClassObjectFilter classObjectFilter;

        /**
         * Initialize the scanner.
         */
        public SubTypesScanner(final ClassObjectFilter classObjectFilter) {
            super();
            this.classObjectFilter = classObjectFilter;
        }

        /*
         * (non-Javadoc)
         * @see
         * org.reflections.scanners.AbstractScanner#scan(org.reflections.vfs
         * .Vfs.File, java.lang.Object)
         */
        @Override
        public Object scan(final File file, Object classObject) {
            if (classObject == null) {
                try {
                    classObject = getMetadataAdapter().getOfCreateClassObject(file);
                } catch (final Exception e) {
                    throw new ReflectionsException("could not create class object from file "
                            + file.getRelativePath());
                }
            }
            if (classObjectFilter.isSafe(file, classObject, getMetadataAdapter())) {
                scan(classObject);
                return classObject;
            }
            return null;
        }

    }

    /**
     * A scanner that ignore classes that cannot be loaded by the class loader.
     * The name has to be SubTypesScanner because reflections uses that as the
     * cache key.
     *
     * @author Ashish Shinde
     *
     */
    private static final class TypeAnnotationsScanner extends
    org.reflections.scanners.TypeAnnotationsScanner {

        /**
         * Filter for class objects.
         */
        private final ClassObjectFilter classObjectFilter;

        /**
         * Initialize the scanner.
         */
        public TypeAnnotationsScanner(final ClassObjectFilter classObjectFilter) {
            super();
            this.classObjectFilter = classObjectFilter;
        }

        /*
         * (non-Javadoc)
         * @see
         * org.reflections.scanners.AbstractScanner#scan(org.reflections.vfs
         * .Vfs.File, java.lang.Object)
         */
        @Override
        public Object scan(final File file, Object classObject) {
            if (classObject == null) {
                try {
                    classObject = getMetadataAdapter().getOfCreateClassObject(file);
                } catch (final Exception e) {
                    throw new ReflectionsException("could not create class object from file "
                            + file.getRelativePath());
                }
            }
            if (classObjectFilter.isSafe(file, classObject, getMetadataAdapter())) {
                scan(classObject);
                return classObject;
            }
            return null;
        }

    }

    /**
     * The parameter name for the cache size.
     */
    public static final String CLASS_CACHE_SIZE_PARAM =
            "com.strandls.alchemy.cdo.common.reflect.CachingJavaTypeQueryHandler"
                    + ".classCacheSize";

    /**
     * The parameter name for the cache timeout.
     */
    public static final String CLASS_CACHE_TIMEOUT_MILLIS_PARAM =
            "com.strandls.alchemy.cdo.common.reflect.CachingJavaTypeQueryHandler"
                    + ".classCacheTimeoutMillis";

    /**
     * Cache for annotation queries.
     */
    private final LoadingCache<AnnotationQuery, Set<Class<?>>> annotationQueryCache;

    private final ClassObjectFilter classObjectFilter;

    /**
     * Cache for sub type queries.
     */
    @SuppressWarnings("rawtypes")
    private final LoadingCache<SubTypeQuery, Set> subTypeQueryCache;

    /**
     * Create a new type handler.
     *
     * @param classQueryCacheTimeoutMillis
     *            the timeout for classes in the cache.
     * @param classQueryCacheSize
     *            the number of cache items.
     */
    @Inject
    public CachingJavaTypeQueryHandler(
            @Named(CLASS_CACHE_TIMEOUT_MILLIS_PARAM) final Integer classQueryCacheTimeoutMillis,
            @Named(CLASS_CACHE_SIZE_PARAM) final Integer classQueryCacheSize) {
        subTypeQueryCache =
                createSubTypeQueryCache(classQueryCacheTimeoutMillis, classQueryCacheSize);
        annotationQueryCache =
                createAnnotationQueryCache(classQueryCacheTimeoutMillis, classQueryCacheSize);
        classObjectFilter = new ClassObjectFilter();
    }

    /**
     * Create annotation query cache.
     *
     * @param classQueryCacheTimeoutMillis
     *            the timeout for classes in the cache.
     * @param classQueryCacheSize
     *            the number of cache items.
     * @return a newly creates cache.
     */
    private LoadingCache<AnnotationQuery, Set<Class<?>>> createAnnotationQueryCache(
            final Integer classQueryCacheTimeoutMillis, final Integer classQueryCacheSize) {
        return CacheBuilder.newBuilder().maximumSize(classQueryCacheSize)
                .expireAfterWrite(classQueryCacheTimeoutMillis, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<AnnotationQuery, Set<Class<?>>>() {
                    /*
                     * (non-Javadoc)
                     * @see
                     * com.google.common.cache.CacheLoader#load(java.lang.Object
                     * )
                     */
                    @Override
                    public Set<Class<?>> load(final AnnotationQuery query) throws Exception {
                        log.debug("Executing annotation query {}", query);
                        return getReflectionsObject(query.packageRegex).getTypesAnnotatedWith(
                                query.annotation);

                    }

                });
    }

    /**
     * Create subtype query cache.
     *
     * @param classQueryCacheTimeoutMillis
     *            the timeout for classes in the cache.
     * @param classQueryCacheSize
     *            the number of cache items.
     * @return a newly creates cache.
     */
    @SuppressWarnings("rawtypes")
    private LoadingCache<SubTypeQuery, Set> createSubTypeQueryCache(
            final Integer classQueryCacheTimeoutMillis, final Integer classQueryCacheSize) {
        return CacheBuilder.newBuilder().maximumSize(classQueryCacheSize)
                .expireAfterWrite(classQueryCacheTimeoutMillis, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<SubTypeQuery, Set>() {
                    /*
                     * (non-Javadoc)
                     * @see
                     * com.google.common.cache.CacheLoader#load(java.lang.Object
                     * )
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public Set load(final SubTypeQuery query) throws Exception {
                        log.debug("Executing sub type query {}", query);
                        return getReflectionsObject(query.packageRegex).getSubTypesOf(query.type);

                    }

                });
    }

    /**
     * Get the reflections object to use for a package prefix.
     *
     * @param packageRegex
     *            package prefix / regex.
     * @return
     */
    private Reflections getReflectionsObject(final String packageRegex) {
        final Reflections object =
                new Reflections(packageRegex, new SubTypesScanner(classObjectFilter),
                        new TypeAnnotationsScanner(classObjectFilter),
                        new FilterBuilder().includePackage(packageRegex));
        object.getStore().getOrCreate(SubTypesScanner.class.getSimpleName());
        return object;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.strandls.alchemy.cdo.common.model.JavaTypeQueryHandler#getSubTypesOf
     * (java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<Class<? extends T>>
    getSubTypesOf(final String packageRegex, final Class<T> type) {
        return subTypeQueryCache.getUnchecked(new SubTypeQuery(packageRegex, type));
    }

    /*
     * (non-Javadoc)
     * @see com.strandls.alchemy.cdo.common.model.JavaTypeQueryHandler#
     * getTypesAnnotatedWith(java.lang.String, java.lang.Class)
     */
    @Override
    public Set<Class<?>> getTypesAnnotatedWith(final String packageRegex,
            final Class<? extends Annotation> annotation) {
        return annotationQueryCache.getUnchecked(new AnnotationQuery(annotation, packageRegex));
    }

}
