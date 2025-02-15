package me.redstoner2019.statserver.stats.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.json.JSONObject;

@Converter(autoApply = true)
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {

    @Override
    public String convertToDatabaseColumn(JSONObject jsonObject) {
        return jsonObject != null ? jsonObject.toString() : null;
    }

    @Override
    public JSONObject convertToEntityAttribute(String json) {
        return json != null ? new JSONObject(json) : null;
    }
}

