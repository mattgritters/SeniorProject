package net.shoppier.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import net.shoppier.CompleteList;
import net.shoppier.ListsItem;
import net.shoppier.SearchableItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class UserFunctions {

	private JSONParser jsonParser;

	private static String loginURL = "http://jonnyklemmer.com/shoppier/Android_DB_API/";
	// private static String registerURL =
	// "http://jonnyklemmer.com/shoppier/Android_DB_API/";
	private static String barcode; 


	private static String login_tag = "login";
	private static String register_tag = "register";
	
	private static final String Array_List = "list";
	private static final String Tag_ListFK = "list_fk";
	private static final String Tag_LIST_ITEM_SEARCH_ID = "list_Search_item_id";
	private static final String Tag_LISTITEM_NAME = "list_item_text";
	private static final String Tag_LISTITEM_BRAND = "list_Item_brand";
	
	private static final String Array_Search_List = "items";
	
	private static final String Tag_Item_ID = "item_pk";
	private static final String Tag_Item_Name = "item_name";
	private static final String Tag_Item_Cat = "item_cat";
	private static final String Tag_Item_Brand = "item_brand";
	
	JSONArray userList = null;
	JSONArray searchList = null;
	JSONArray listIDs = null; 
	private DatabaseHandler db;

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
		
	}

	// constructor for login
	public UserFunctions(String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		jsonParser = new JSONParser(params);
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password) {
		try {
			JSONObject json = jsonParser.execute(loginURL).get();
			return json;
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} catch (ExecutionException e) {
			
			e.printStackTrace();
		}
		// Log.e("JSON", json.toString());
		return null;
	}
	
	//TODO figure out sending data back to database 
	public void Sync(Context context, String listId){
		this.db = new DatabaseHandler(context);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "syncList"));
		params.add(new BasicNameValuePair("userID", db.getUserDetails().get("uid"))); 
		params.add(new BasicNameValuePair("listID", listId)); 
		params.add(new BasicNameValuePair("list", listtoArray(db.getList(listId)))); 

		
		//TODO Create a Get All List Methodd
		jsonParser = new JSONParser(params);
		
		try {
			
			jsonParser.execute(loginURL).get();
			//TODO Add getting new items too. 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<SearchableItem> getSearchableItems(Context context) throws JSONException{
		this.db = new DatabaseHandler(context);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getSearchableItems"));
		
		ArrayList<SearchableItem> searchableList = new ArrayList<SearchableItem>();
		jsonParser = new JSONParser(params);
		
		try {
			JSONObject json = jsonParser.execute(loginURL).get();
			searchList = json.getJSONArray(Array_Search_List);
			for (int i = 0; i <= searchList.length() - 1; i++) {
				SearchableItem li = new SearchableItem();
				JSONObject l = searchList.getJSONObject(i);
				li.setItemBrand(l.getString(Tag_Item_Brand));
				li.setItemCat(l.getString(Tag_Item_Cat));
				li.setItemID(Integer.parseInt(l.getString(Tag_Item_ID)));
				li.setItemName(l.getString(Tag_Item_Name));
				searchableList.add(li);
				db.addSearchableItem(li);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return searchableList;
		
	}
	public ArrayList<ListsItem> getUserGrocList(Context context) {
		this.db = new DatabaseHandler(context);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getUsersList"));
		params.add(new BasicNameValuePair("userID", db.getUserDetails().get("uid"))); 

		jsonParser = new JSONParser(params);

		ArrayList<ListsItem> userGrocList = new ArrayList<ListsItem>();

		try {
			JSONObject json = jsonParser.execute(loginURL).get();
			try {
				userList = json.getJSONArray(Array_List);
				for (int i = 0; i <= userList.length() - 1; i++) {
					
					ListsItem li = new ListsItem();
					JSONObject l = userList.getJSONObject(i);
					li.setListFK(l.getInt(Tag_ListFK));
					li.setSearchItemId(l.getInt(Tag_LIST_ITEM_SEARCH_ID));
					li.setListsItemName(l.getString(Tag_LISTITEM_NAME));
					li.setListItemBrand(l.getString(Tag_LISTITEM_BRAND));
					userGrocList.add(li);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return userGrocList;
	}
	
	public void getListIDS(Context context){
		this.db = new DatabaseHandler(context);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getListIDs"));
		params.add(new BasicNameValuePair("userID", db.getUserDetails().get("uid"))); 

		jsonParser = new JSONParser(params);
		try {
			JSONObject json = jsonParser.execute(loginURL).get();
			try {
				listIDs = json.getJSONArray("listIds");
				for (int i = 0; i <= listIDs.length() - 1; i++) {
					JSONObject l = listIDs.getJSONObject(i);
					CompleteList tempList = new CompleteList();
					//TODO Change when routes are added
					tempList.setListName(l.getString("list_name"));
					tempList.setListPK(l.getInt("list_pk"));
					tempList.setChanged(false);
					db.addListID(tempList);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}
	
	private String listtoArray(ArrayList<ListsItem> list){
		
		JSONArray totalList = new JSONArray();
		for(int i = 0; i <= list.size() -1; i ++){
			JSONArray aryItem = new JSONArray();
			aryItem.put(list.get(i).getSearchItemId()).put(list.get(i).getListsItemName()).put(list.get(i).getListItemBrand());
		totalList.put(aryItem);
		}
		return totalList.toString();
	}
	
	public ListsItem getBarcodeProduct(String barcodeInput, String listID){
		barcode = barcodeInput;
		JSONObject json = null;
		ListsItem tempItem = new ListsItem();
		String barcodeURL = "https://api.scandit.com/v2/products/" + 
				barcode + "?key=BqfeU4NrpvcMbqVsZ5MtFPum45E9apDqiGwn70xNlL4";
		
		jsonParser = new JSONParser();
		try {
			 json = jsonParser.execute(barcodeURL).get();
			 JSONObject item = json.getJSONObject("basic");
			 String itemName = item.getString("name");
			 tempItem.setListFK(Integer.parseInt(listID));
			 tempItem.setListsItemName(itemName);	
			 tempItem.setSearchItemId(0);
			 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempItem;
		
	}
	
	public JSONObject sendCrowdSourceItem(String itemName, String itemBrand, int itemCatFK){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "sentCrowdSourcedItem"));
		params.add(new BasicNameValuePair("item_name", itemName)); 
		params.add(new BasicNameValuePair("item_brand", itemBrand)); 
		params.add(new BasicNameValuePair("item_catFK", Integer.toString(itemCatFK)));

		jsonParser = new JSONParser(params);
		
		JSONObject json = null;
		try {
			json = jsonParser.execute(loginURL).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	public ArrayList<ListsItem> routeList(ArrayList<ListsItem> list){
		
		Collections.sort(list, new Comparator<ListsItem>() {

			@Override
			public int compare(ListsItem item1, ListsItem item2) {
				return item1.getListsItemName().compareToIgnoreCase(item2.getListsItemName());
			}
	});
		return list;
		
	}

	/**
	 * function Register User
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * */ 
	public JSONObject registerUser(String name, String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		// JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return null;
	}

	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount("user");
		if (count > 0) {
			// user logged in
			return true;
		}
		return false;
	}

	/**
	 * Function to logout user Reset Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		return true;
	}

}