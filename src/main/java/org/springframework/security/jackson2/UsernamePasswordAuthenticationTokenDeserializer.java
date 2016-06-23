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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Custom deserializer for {@link UsernamePasswordAuthenticationToken}. At the time of deserialization
 * it will invoke suitable constructor depending on the value of <b>authenticated</b> property.
 * It will ensure that the token's state must not change.
 * <p>
 * This deserializer is already registered with {@link UsernamePasswordAuthenticationTokenMixin} but
 * you can also registered it with your own mixin class.
 *
 * @author Jitendra Singh
 * @author Halvard Skogsrud
 * @see UsernamePasswordAuthenticationTokenMixin
 */
public class UsernamePasswordAuthenticationTokenDeserializer extends JsonDeserializer<UsernamePasswordAuthenticationToken> {

    /**
     * This method construct {@link UsernamePasswordAuthenticationToken} object from serialized json.
     */
    @Override
    public UsernamePasswordAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        UsernamePasswordAuthenticationToken token;
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
        Boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal;
        if (principalNode.isObject()) {
            Class principalClass = LinkedHashMap.class;
            if (principalNode.hasNonNull("@class")) {
                try {
                    principalClass = Class.forName(principalNode.get("@class").asText());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Could not load principal class [" + principalNode.get("@class").asText() + "]", e);
                }
            }
            principal = mapper.readValue(principalNode.toString(), principalClass);
        } else {
            principal = principalNode.asText();
        }
        Object credentials = readJsonNode(jsonNode, "credentials").asText();
        List<GrantedAuthority> authorities = mapper.readValue(
                readJsonNode(jsonNode, "authorities").toString(), new TypeReference<List<GrantedAuthority>>() {
                });
        if (authenticated) {
            token = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
        } else {
            token = new UsernamePasswordAuthenticationToken(principal, credentials);
        }
        token.setDetails(readJsonNode(jsonNode, "details"));
        return token;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
