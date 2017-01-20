package com.bitso.helpers;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Helpers {

    public static final DateTimeFormatter dateTimeFormatterZOffset = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
    public static final DateTimeFormatter dateTimeFormatterXOffset = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private static final List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    public static final String fieldPrinter(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============");
        List<Field> fields = getAllFields(new ArrayList<Field>(), obj.getClass());
        for (Field f : fields) {
            try {
                Object o = f.get(obj);
                sb.append('\n');
                sb.append(f.getName());
                sb.append(": ");
                sb.append(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("\n==============\n");
        return sb.toString();
    }

    public static final void printStackTrace(PrintStream out) {
        StringBuilder sb = new StringBuilder();
        sb.append("Printing Stack Trace\n");
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            sb.append('\t');
            sb.append(ste);
            sb.append('\n');
        }
        out.print(sb);
    }

    public static final void printStackTrace() {
        printStackTrace(System.err);
    }

    public static JSONObject parseJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            System.err.println("Unable to parse json: " + json);
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray parseJsonArray(String json) {
        if (json == null) {
            return null;
        }
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            System.err.println("Unable to parse json array: " + json);
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(JSONObject o, String key) {
        if (o.has(key)) {
            return o.getString(key);
        } else {
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static BigDecimal getBD(JSONObject o, String key) {
        if (o.has(key)) {
            return new BigDecimal(o.getString(key));
        } else {
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static Integer getInteger(JSONObject o, String key){
        if(o.has(key)){
            return o.getInt(key);
        }else{
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static ZonedDateTime getZonedDatetime(JSONObject o, String key){
        if(o.has(key)){
            try {
                return ZonedDateTime.parse(o.getString(key), dateTimeFormatterZOffset);
            } catch (DateTimeException e) {
                return ZonedDateTime.parse(o.getString(key), dateTimeFormatterXOffset);
            }
        }else{
            System.err.println("No " + key + ": " + o);
            Helpers.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> getUnstructuredJSONObjectValues(JSONObject o){
        Iterator<String> iterator = o.keys();
        Map<String, Object> map = new HashMap<String, Object>();
        String currentKey = "";
        Object currentObject;
        while(iterator.hasNext()){
            currentKey = iterator.next();
            currentObject = o.get(currentKey);
            if(currentObject instanceof JSONObject){
                map.put(currentKey,
                        getUnstructuredJSONObjectValues((JSONObject) currentObject));
            }else{
                map.put(currentKey, currentObject);
            }
        }
        return map;
    }

    public static JSONObject expectJSONObject(JSONObject o, String key, Object senderClass){
        if((o.get(key) instanceof JSONObject)){
            return o.getJSONObject(key);
        }
        System.out.println("On " + senderClass.getClass().getName() + ", key: " + key
                + " is not a JSONObject as expected"
                + " returning null");
        return null;
    }

    public static String[] parseJSONArray(JSONArray arrray){
        int totalElements = arrray.length();
        String[] elements =  new String[totalElements];
        for(int i=0; i<totalElements; i++){
            elements[i] = arrray.getString(i);
        }
        return elements;
    }
}
