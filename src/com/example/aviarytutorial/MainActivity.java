package com.example.aviarytutorial;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity
{
	public static final int EDIT_IMAGE_CODE = 1000;
	public static final int REQUEST_IMAGE_CAPTURE = 101;
	
	@ViewById(R.id.resultImage)
	ImageView resultImage;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public void editImage(View view)
	{
		Intent newIntent = new Intent(this, FeatherActivity.class);
		newIntent
				.setData(Uri
						.parse("http://www.hdwidescreendesktop.com/wp-content/uploads/2014/03/football-wayne-rooney-2013-high-quality-widescreen-wallpaper-wayne-rooney-2013-youtube-wayne-rooney-2013-cleats-wayne-rooney-2013-wayne-rooney-2013-jersey-wayne-rooney-2013-news-wayne-rooney-2013-seas.jpg"));
		newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, getString(R.string.aviary_secret));
		startActivityForResult(newIntent, EDIT_IMAGE_CODE);
	}

	public void takePhoto(View view)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK) {
			Bundle extra = data.getExtras();
			switch (requestCode)
			{
			case EDIT_IMAGE_CODE:
				// output image path
				Uri mImageUri = data.getData();
				
				if (null != extra) {
					// image has been changed by the user?
					boolean changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
				}
				break;
			case REQUEST_IMAGE_CAPTURE:
				Bitmap imageBitmap = (Bitmap) extra.get("data");
				resultImage.setImageBitmap(imageBitmap);
			}
		}
	}

}
