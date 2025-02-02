/*
 * Copyright (c) 2023 nosqlbench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nosqlbench.virtdata.library.basics.shared.unary_string;

import io.nosqlbench.api.content.NBIO;
import io.nosqlbench.api.errors.BasicError;
import io.nosqlbench.virtdata.api.annotations.Categories;
import io.nosqlbench.virtdata.api.annotations.Category;
import io.nosqlbench.virtdata.api.annotations.Example;
import io.nosqlbench.virtdata.api.annotations.ThreadSafeMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Function;

/**
 * Provides a single line of text from a target file provided.
 */
@ThreadSafeMapper
@Categories({Category.general})
public class TextOfFile implements Function<Object, String> {
    private static final Logger logger = LogManager.getLogger(TextOfFile.class);
    private final String text;

    public String toString() {
        return getClass().getSimpleName();
    }

    @Example({"TextOfFile()", "Provides the first line of text in the specified file."})
    public TextOfFile(String targetFile) {

        try {
            final List<String> lines = NBIO.readLines(targetFile);
            logger.info("TextOfFile() reading: {}", targetFile);
            if (lines.isEmpty()) {
                throw new BasicError(String.format("Unable to locate content for %s", this));
            }
            text = lines.get(0);
        } catch (Exception ex) {
            throw new BasicError(String.format("Unable to locate file %s: ", targetFile), ex);
        }
    }

    @Override
    public String apply(Object obj) {
        return text;
    }

}
