package net.shoppier;

import net.shoppier.library.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MapLocator extends Activity {
	
	ImageView map; 
	String listID;
	ListsItem itemSelected;
	DatabaseHandler db; 
	

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		setTitle("Find Item");
		db = new DatabaseHandler(getApplicationContext());
		
		 /* get the item clicked */
		Intent extra = getIntent();
		if(extra != null){
			 listID = extra.getExtras().getString("selectedItem");
		}
		itemSelected = db.getListItem(listID);
		
		
		Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.jenisonmeijermap);
		Bitmap bitMapPoint = BitmapFactory.decodeResource(getResources(), R.drawable.red_dot);
		map = (ImageView) findViewById(R.id.mapView);
		
		/*Set the new map in view */
		map.setImageBitmap(placePin(bitMap, bitMapPoint, itemSelected.getxCord(), itemSelected.getyCord()));
	}
	
	/**
	 * place the pin on the map of the item selected
	 * 
	 * @param map - 	map of the store the item belongs to 
	 * @param point - 	icon representing the point on the map 
	 * @param x_cor - 	x coordinate of the item on the map
	 * @param y_cor - 	y coordinate of the item on the map
	 * 
	 * @return bmOverlay -	the new bitmap with the pin placed on the map
	 * */
    private Bitmap placePin(Bitmap map, Bitmap point, int x_cor, int y_cor) {
        Bitmap bmOverlay = Bitmap.createBitmap(map.getWidth(), map.getHeight(), map.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(map, new Matrix(), null);
        
        Matrix scaleMatrix = new Matrix();
		scaleMatrix .postTranslate(x_cor, x_cor);
        canvas.drawBitmap(point, scaleMatrix, null);
        return bmOverlay;
    }
	
	

}