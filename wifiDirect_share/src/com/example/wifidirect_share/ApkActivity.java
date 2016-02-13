package com.example.wifidirect_share;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

public class ApkActivity extends ListActivity {
	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;
	
	
    private String host_info; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apk_activity);
		Intent intent = getIntent();
		host_info = intent.getStringExtra("hostinfo");
		Log.d("hostinfo",host_info);
		packageManager = getPackageManager();

		new LoadApplications().execute();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}



	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ApplicationInfo app = applist.get(position);
		String src = app.sourceDir;
		Log.d("selected", src);
		File f= new File(src);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
	    this.onActivityResult(1,position,intent);
		
	}
	 @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         	Log.d("called","called");
	    	String flag1="apk";
	    	Log.d("data",data.toString());
	        Uri uri = data.getData();
	        Log.d(WifiDirectActivity.TAG, "Intent----------- " + uri +"----" );
	        Intent serviceIntent = new Intent(this, FileTransferService.class);
	        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
	        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
	        serviceIntent.putExtra(FileTransferService.type_file,flag1);
	        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
	        host_info);
	        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8987);
	        this.startService(serviceIntent);
	    	}
	 
	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
					if((info.flags & ApplicationInfo.FLAG_SYSTEM)!=1)
					applist.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}

	private class LoadApplications extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
			applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES));
			listadaptor = new ApplicationAdapter(ApkActivity.this,
					R.layout.apk_list, applist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(listadaptor);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(ApkActivity.this, null,
					"Loading applications");
			progress.setCancelable(true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
}