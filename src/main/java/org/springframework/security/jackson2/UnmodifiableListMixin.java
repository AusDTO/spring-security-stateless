package org.springframework.security.jackson2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * This mixin class used to deserialize java.util.Collections$UnmodifiableList.
 * <pre>
 *     ObjectMapper objectMapper = new ObjectMapper();
 *     objectMapper.addMixIn(Collections.unmodifiableList(Collections.EMPTY_LIST).getClass(), UnmodifiableListMixin.class);
 * </pre>
 * <p>or
 * <pre>
 *     ObjectMapper objectMapper = new ObjectMapper();
 *     objectMapper.addMixInAnnotations(Collections.unmodifiableList(Collections.EMPTY_LIST).getClass(), UnmodifiableListMixin.class);
 * </pre>
 *
 * @author Halvard Skogsrud
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class UnmodifiableListMixin {

    @JsonCreator
    UnmodifiableListMixin(List list) {
    }
}
