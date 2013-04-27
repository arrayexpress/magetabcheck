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

package uk.ac.ebi.fg.annotare2.magetabcheck.checks;

import com.google.common.base.Predicate;
import com.google.common.collect.Range;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Check;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.Visit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Olga Melnichuk
 */
public class RangeCheck<T> {

    private int count;

    private final Predicate<T> predicate;

    private final Range<Integer> range;

    public RangeCheck(@Nonnull Range<Integer> range) {
        this(new Predicate<T>() {
            @Override
            public boolean apply(@Nullable T obj) {
                return true;
            }
        }, range);
    }

    public RangeCheck(@Nonnull Predicate<T> predicate, @Nonnull Range<Integer> range) {
        checkNotNull(predicate);
        checkNotNull(range);
        this.predicate = predicate;
        this.range = range;
    }

    @Visit
    public void visit(T t) {
        if (predicate.apply(t)) {
            count++;
        }
    }

    @Check
    public void check() {
        assertThat(range.contains(count), is(true));
    }
}
