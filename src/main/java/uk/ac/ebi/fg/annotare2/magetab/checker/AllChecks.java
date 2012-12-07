/*
 * Copyright 2012 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetab.checker;

import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetab.checks.idf.*;
import uk.ac.ebi.fg.annotare2.magetab.checks.sdrf.ListOfArrayDesignAttributesMustBeEmpty;
import uk.ac.ebi.fg.annotare2.magetab.checks.sdrf.ListOfLabeledExtractNodesMustBeEmpty;
import uk.ac.ebi.fg.annotare2.magetab.checks.sdrf.ListOfScanNodesMustNotBeEmpty;
import uk.ac.ebi.fg.annotare2.magetab.checks.sdrf.SdrfSimpleChecks;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AllChecks {

    private static final Logger log = LoggerFactory.getLogger(AllChecks.class);

    //TODO make it automatically detected
    private static final List<Class> methodBasedChecks = Arrays.<Class>asList(
            IdfSimpleChecks.class,
            SdrfSimpleChecks.class);

    //TODO make it automatically detected
    private static final List<Class> classBasedChecks = Arrays.<Class>asList(
            AtLeastOneContactMustBeSubmitter.class,
            AtLeastOneContactWithEmailRequired.class,
            AtLeastOneContactWithRolesRequired.class,
            AtLeastOneSubmitterMustHaveEmail.class,
            ListOfContactsMustBeNonEmpty.class,
            ListOfExperimentalDesignsShouldBeNonEmpty.class,
            ListOfExperimentalFactorsMustBeNonEmpty.class,
            ListOfQualityControlTypesShouldBeNonEmpty.class,
            ListOfReplicateTypesShouldBeNonEmpty.class,
            ListOfNormalizationTypesShouldBeNonEmpty.class,
            ListOfProtocolsMustBeNonEmpty.class,
            TermSourcesMustBeUniqueByName.class,

            ListOfScanNodesMustNotBeEmpty.class,
            ListOfArrayDesignAttributesMustBeEmpty.class,
            ListOfLabeledExtractNodesMustBeEmpty.class,
            LibraryConstructionProtocolRequired.class,
            SequencingProtocolRequired.class
    );

    private final Injector injector;

    public AllChecks(Injector injector) {
        this.injector = injector;
    }

    @SuppressWarnings("unchecked")
    public <T> List<CheckRunner<T>> getCheckRunnersFor(Class<T> itemClass, ExperimentType invType) {
        List<CheckRunner<T>> runners = new ArrayList<CheckRunner<T>>();

        for (Class clazz : classBasedChecks) {
            Class typeArg = getGlobalCheckTypeArgument(clazz);
            if (typeArg != null && (typeArg.isAssignableFrom(itemClass))) {
                MageTabCheck annot = (MageTabCheck) clazz.getAnnotation(MageTabCheck.class);
                if (isApplicable(annot, invType)) {
                    runners.add(new ClassBasedCheckRunner<T>(injector, (Class<? extends GlobalCheck<T>>) clazz));
                }
            }
        }

        for (Class clazz : methodBasedChecks) {
            for (Method method : clazz.getMethods()) {
                MageTabCheck annot = method.getAnnotation(MageTabCheck.class);
                if (annot == null) {
                    continue;
                }
                Class[] types = method.getParameterTypes();
                if (types != null && !types[0].isAssignableFrom(itemClass)) {
                    continue;
                }
                if (isApplicable(annot, invType)) {
                    runners.add(new MethodBasedCheckRunner<T>(injector, clazz, method));
                }
            }
        }
        return runners;
    }

    private static boolean isApplicable(MageTabCheck annot, ExperimentType type) {
        return annot != null && annot.application().appliesTo(type);
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable type.
     *
     * @param type the type
     * @return the underlying class
     */
    protected static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else {
            return null;
        }
    }

    /**
     * Returns {@link ParameterizedType} for {@link GlobalCheck} interface if the given class implements it;
     * otherwise <code>null</code>
     *
     * @param clazz the class to check
     * @return a {@link ParameterizedType} for {@link GlobalCheck} interface or <code>null</code>
     */
    protected static ParameterizedType getGlobalCheckInterface(Class<?> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type interf : interfaces) {
            Type rawType = ((ParameterizedType) interf).getRawType();
            if (rawType instanceof Class &&
                    rawType.equals(GlobalCheck.class)) {
                return (ParameterizedType) interf;
            }
        }
        return null;
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic base class.
     *
     * @param clazz the class to check
     * @return a list of the raw classes for the actual type arguments.
     */
    protected static Class<?> getGlobalCheckTypeArgument(Class<?> clazz) {
        Type type = clazz;
        ParameterizedType interf = null;

        while (interf == null && type != null && !getClass(type).equals(Object.class)) {
            if (type instanceof Class) {
                interf = getGlobalCheckInterface((Class) type);
                if (interf == null) {
                    type = ((Class) type).getGenericSuperclass();
                } else {
                    return getClass(interf.getActualTypeArguments()[0]);
                }
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                interf = getGlobalCheckInterface(rawType);
                if (interf == null) {
                    type = ((Class) rawType).getGenericSuperclass();
                } else {

                    String typeVarName = ((Class) interf.getRawType()).getTypeParameters()[0].getName();
                    Type t = interf.getActualTypeArguments()[0];
                    if (t instanceof Class) {
                        return getClass(t);
                    }

                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        if (typeParameters[i].getName().equals(typeVarName)) {
                            return getClass(actualTypeArguments[i]);
                        }
                    }
                }
            }
        }
        return null;
    }
}
