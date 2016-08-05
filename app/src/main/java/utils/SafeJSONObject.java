package utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SafeJSONObject  {
	
	private JSONObject 	mObject;
	
	public SafeJSONObject() {
		mObject = new JSONObject();
	}
	
	public SafeJSONObject(JSONObject object) {
		mObject = object;	
	}
	
	public JSONObject getObject() {return mObject;}
	
	public SafeJSONObject(String value) {
		try {
			mObject = new JSONObject(value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SafeJSONObject getJSONObject(String key) {
		try {
			return new SafeJSONObject(mObject.getJSONObject(key));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public SafeJSONArray getJSONArray(String key) {
		try {
			return new SafeJSONArray(mObject.getJSONArray(key));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void putString(String key, String value) {
		try {
			mObject.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getString(String key) {
		String returnString = "";
		try {
			returnString = mObject.getString(key);
			if(returnString.equals("null"))
				return "";
			return mObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public int getInt(String key) {
		try {
			
			return mObject.getInt(key);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void putInt(String key, int value) {
		try {
			mObject.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putBoolean(String key, boolean value) {
		try {
			mObject.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void putSafeJSONObject(String key, SafeJSONObject jsonObject) {
		try {
			mObject.put(key, jsonObject.getObject());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void putSafeJSONArray(String key, SafeJSONArray jsonArray) {
		try {
			mObject.put(key, jsonArray.getObject());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double getDouble(String key) {
		try {
			return mObject.getDouble(key);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public boolean getBoolean(String key) {
		try {
			return mObject.getBoolean(key);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}