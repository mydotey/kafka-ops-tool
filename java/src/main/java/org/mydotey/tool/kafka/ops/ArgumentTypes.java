package org.mydotey.tool.kafka.ops;

import java.util.ArrayList;
import java.util.List;

import org.mydotey.java.StringExtension;

import com.google.common.base.Splitter;

import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * @author koqizhao
 *
 * Feb 1, 2019
 */
public interface ArgumentTypes {

    ArgumentType<List<Integer>> LIST_INTEGER = (p, a, v) -> {
        if (StringExtension.isBlank(v))
            return null;

        List<Integer> results = new ArrayList<>();
        Splitter.on(',').omitEmptyStrings().splitToList(v.trim()).forEach(s -> results.add(Integer.valueOf(s)));
        return results;
    };

}
