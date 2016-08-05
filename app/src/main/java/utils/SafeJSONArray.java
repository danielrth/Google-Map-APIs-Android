package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SafeJSONArray {
	private JSONArray mObject;
	
	public SafeJSONArray() {
		mObject = new JSONArray();
	}
	
	public JSONArray getObject() {
		return mObject;
	}
	
	public SafeJSONArray(JSONArray object) {
		mObject = object;
	}
	
	public SafeJSONArray(String value) {
		try {
			mObject = new JSONArray(value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ReplaceJsonArray(ArrayList<SafeJSONObject> list){
		mObject = new JSONArray();
		for (int idx = 0; idx < list.size(); idx++) {
			mObject.put(list.get(idx).getObject());
		}
	}

	public SafeJSONArray(ArrayList<SafeJSONObject> list) {
		mObject = new JSONArray();
		for (int idx = 0; idx < list.size(); idx++) {
			mObject.put(list.get(idx).getObject());
		}
	}
	
	public SafeJSONObject getJSONObject(int idx) {
		try {
			return new SafeJSONObject(mObject.getJSONObject(idx));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public SafeJSONArray getJSONArray(int idx) {
		try {
			return new SafeJSONArray(mObject.getJSONArray(idx));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getString (int idx) {
		try {
			return mObject.getString(idx);
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public int getInt (int idx) {
		try {
			return mObject.getInt(idx);
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}
	
	public int length() {
		if (mObject == null) return 0;
		return mObject.length();
	}
	
	public void sortArray(Comparator<SafeJSONObject> comparator) {
		List<SafeJSONObject> safeJsonValues = new ArrayList<SafeJSONObject>();
		for (int idx = 0; idx < mObject.length(); idx++) {
			safeJsonValues.add(getJSONObject(idx));
		}
		
		Collections.sort(safeJsonValues, comparator);
		
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int idx = 0; idx < safeJsonValues.size(); idx++) {
			jsonValues.add(safeJsonValues.get(idx).getObject());
		}
		
		mObject = new JSONArray(jsonValues);
	}
	
	public ArrayList<String> toStringArray() {
		try {
			ArrayList<String> list = new ArrayList<String>();
			for (int idx = 0; idx < mObject.length(); idx++) {
				list.add(mObject.getString(idx));
			}
		
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<JSONObject> toJSONObjectArray() {
		try {
			ArrayList<JSONObject> list = new ArrayList<JSONObject>();
			for (int idx = 0; idx < mObject.length(); idx++) {
				list.add(mObject.getJSONObject(idx));
			}
			
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public ArrayList<SafeJSONObject> toSafeJSONObjectArray() {
		ArrayList<SafeJSONObject> list = new ArrayList<SafeJSONObject>();
		for (int idx = 0; idx < mObject.length(); idx++) {
			list.add(getJSONObject(idx));
		}
		
		return list;
				
	}
	
	public void addJSONObject(SafeJSONObject jsonObject) {
		if (mObject != null)
			mObject.put(jsonObject.getObject());
	}
	
	public void addString(String string) {
		if (mObject != null)
			mObject.put(string);
	}
	
	public String toString() {
		return mObject.toString();
	}
	
	public void put(JSONObject jsonObject) {
		mObject.put(jsonObject);
	}
	
	public void put(String string) {
		mObject.put(string);
	}
	
	public void setJSONObject(int index, SafeJSONObject jsonObject) {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		for (int i=0;i<mObject.length();i++){ 
			try {
			   list.add((JSONObject)mObject.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		list.remove(index);
		list.add(index, jsonObject.getObject());
		mObject = new JSONArray(list);
	}
	
	public void removeJSONObject(int index) {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		for (int i=0;i<mObject.length();i++){ 
			try {
			   list.add((JSONObject)mObject.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		list.remove(index);
		mObject = new JSONArray(list);
	}

	public void concatUserArray(SafeJSONArray newArray) { // use for only users array data
		JSONArray newObject = newArray.getObject();
		for (int idx = 0; idx < newObject.length(); idx++) {
			try {
				if (!findObjectByKey("userid", newObject.getJSONObject(idx).getInt("userid"))){
                    try {
                        mObject.put(newObject.get(idx));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public boolean findObjectByKey (String key, int value) {
		for (int idx = 0; idx < mObject.length(); idx++) {
			try {
				if (mObject.getJSONObject(idx).getInt(key) == value) {
                    return true;
                }
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
