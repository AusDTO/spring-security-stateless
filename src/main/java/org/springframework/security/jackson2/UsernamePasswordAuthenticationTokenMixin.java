/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.jackson2;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This mixin class is used to serialize / deserialize
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}. This class register
 * a custom deserializer {@link UsernamePasswordAuthenticationTokenDeserializer}.
 *
 * In order to use this mixin you'll need to add 3 more mixin classes.
 * <ol>
 *     <li>{@link UnmodifiableSetMixin}</li>
 *     <li>{@link SimpleGrantedAuthorityMixin}</li>
 *     <li>{@link UserMixin}</li>
 * </ol>
 *
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.addMixIn(Collections.unmodifiableSet(Collections.EMPTY_SET).getClass(), UnmodifiableSetMixin.class);
 *     mapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
 *     mapper.addMixIn(User.class, UserMixin.class);
 *     mapper.addMixIn(UsernamePasswordAuthenticationToken.class, UsernamePasswordAuthenticationTokenMixin.class);
 * </pre>
 * @author Jitendra Singh
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonDeserialize(using = UsernamePasswordAuthenticationTokenDeserializer.class)
public abstract class UsernamePasswordAuthenticationTokenMixin {
}
