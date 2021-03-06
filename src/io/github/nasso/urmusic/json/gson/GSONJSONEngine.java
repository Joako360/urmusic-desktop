package io.github.nasso.urmusic.json.gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import io.github.nasso.urmusic.Urmusic;
import io.github.nasso.urmusic.json.JSONArray;
import io.github.nasso.urmusic.json.JSONEngine;
import io.github.nasso.urmusic.json.JSONObject;
import io.github.nasso.urmusic.json.JSONSerializable;

public class GSONJSONEngine implements JSONEngine {
	private JsonSerializer<JSONSerializable> serializer;
	
	private Gson gson;
	
	public GSONJSONEngine() {
		this.serializer = new JsonSerializer<JSONSerializable>() {
			public JsonElement serialize(JSONSerializable src, Type typeOfSrc, JsonSerializationContext context) {
				Object o = src.toJSON(GSONJSONEngine.this);
				
				if(o instanceof GSONJSONObject) return context.serialize(((GSONJSONObject) o).gsonObj);
				else if(o instanceof GSONJSONArray) return context.serialize(((GSONJSONArray) o).gsonArr);
				return context.serialize(o);
			}
		};
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(JSONSerializable.class, this.serializer);
		if(Urmusic.DEBUG) builder.setPrettyPrinting();
		this.gson = builder.create();
	}
	
	public String stringify(Object obj) {
		return this.gson.toJson(obj);
	}
	
	public JSONObject parse(String str) {
		return GSONJSONObject.get(this.gson.fromJson(str, JsonObject.class));
	}
	
	public JSONObject createObject() {
		return GSONJSONObject.get(new JsonObject());
	}
	
	public JSONArray createArray() {
		return GSONJSONArray.get(new JsonArray());
	}
	
	public String stringify(JSONObject jsonobj) {
		if(!(jsonobj instanceof GSONJSONObject)) return null;
		
		return this.gson.toJson(((GSONJSONObject) jsonobj).gsonObj);
	}
	
	public void write(JSONObject jsonobj, OutputStream out) {
		try {
			JsonWriter writer = this.gson.newJsonWriter(new OutputStreamWriter(out));
			this.gson.toJson(((GSONJSONObject) jsonobj).gsonObj, writer);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
