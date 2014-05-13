package com.example.aviarytutorial;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.library.utils.DecodeUtils;
import com.aviary.android.feather.library.utils.ImageSizes;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	public static final int EDIT_IMAGE_CODE = 1000;
	public static final int REQUEST_IMAGE_CAPTURE = 101;
	private String currentPhotoPath;
	private String url = "http://www.hdwidescreendesktop.com/wp-content/uploads/2014/03/football-wayne-rooney-2013-high-quality-widescreen-wallpaper-wayne-rooney-2013-youtube-wayne-rooney-2013-cleats-wayne-rooney-2013-wayne-rooney-2013-jersey-wayne-rooney-2013-news-wayne-rooney-2013-seas.jpg";

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
		if (currentPhotoPath != null)
		{
			newIntent.setData(Uri.parse(currentPhotoPath));
		} else
		{
			newIntent.setData(Uri.parse(url));
		}

		newIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, getString(R.string.aviary_secret));
		startActivityForResult(newIntent, EDIT_IMAGE_CODE);
	}

	public void takePhoto(View view)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getPackageManager()) != null)
		{
			File photoFile = null;
			try
			{
				photoFile = createImageFile();
			} catch(Exception e)
			{
				Toast.makeText(getApplicationContext(), "Cannot create file", Toast.LENGTH_SHORT).show();
			}

			if (photoFile != null)
			{
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{

			switch (requestCode)
			{
				case EDIT_IMAGE_CODE:
					// output image path
					Uri mImageUri = data.getData();
					Bundle extra = data.getExtras();
					if (null != extra)
					{
						// image has been changed by the user?
						boolean changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
						decodeEditedPic(mImageUri);
						
					}
					break;
				case REQUEST_IMAGE_CAPTURE:
					galleryAddPic();
					decodePic(currentPhotoPath);
					break;
			}
		} else
		{
			Toast.makeText(getApplicationContext(), "Cannot get data", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException
	{
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
		currentPhotoPath = imageFile.getAbsolutePath();
		return imageFile;
	}

	void decodePic(String currentPhotoPath)
	{
		Log.d("TAG", "Path: " + currentPhotoPath);
		int targetW = resultImage.getWidth();
		int targetH = resultImage.getHeight();
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		Bitmap bitmapImage = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		Log.e("TAG", "TARGET: " + targetH);
		resultImage.setImageBitmap(bitmapImage);
	}
	
	void decodeEditedPic(Uri uri)
	{
		int targetW = resultImage.getWidth();
		int targetH = resultImage.getHeight();
		ImageSizes sizes = new ImageSizes();
		Bitmap bitmapImage = DecodeUtils.decode( MainActivity.this, uri, targetW, targetH, sizes );
		resultImage.setImageBitmap(bitmapImage);
	}

	private void galleryAddPic()
	{
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(currentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}
}
