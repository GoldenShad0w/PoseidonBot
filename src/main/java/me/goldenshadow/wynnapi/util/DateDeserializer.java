package me.goldenshadow.wynnapi.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {


    private static final SimpleDateFormat[] formats = {new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")};


    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String string = jsonElement.getAsString();
        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(string);
            } catch (ParseException ignored) {}
        }
        throw new JsonParseException("Couldn't parse date format: " + string);
    }
}
