package org.springframework.security.web.jackson2;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonDeserialize(using = WebAuthenticationDetailsDeserializer.class)
public abstract class WebAuthenticationDetailsMixin {
}
