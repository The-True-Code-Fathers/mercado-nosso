package com.mercadonosso.orders_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Registrar módulo para suporte a Java 8 date/time
        mapper.registerModule(new JavaTimeModule());
        
        // Módulo personalizado para ObjectId
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());
        
        mapper.registerModule(module);
        return mapper;
    }

    public static class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
        @Override
        public ObjectId deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            String objectIdString = p.getValueAsString();
            if (objectIdString != null && !objectIdString.isEmpty()) {
                try {
                    return new ObjectId(objectIdString);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid ObjectId format: " + objectIdString, e);
                }
            }
            return null;
        }
    }

    public static class ObjectIdSerializer extends JsonSerializer<ObjectId> {
        @Override
        public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value != null) {
                gen.writeString(value.toHexString());
            } else {
                gen.writeNull();
            }
        }
    }
}
