package com.example.wifidirect_share;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.example.wifidirect_share.PeerListFragment.DeviceActionListener;


public class FunctionalityFragment extends Fragment implements ConnectionInfoListener,SensorEventListener {

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	protected static final int CHOOSE_FILE_RESULT_CODE2 = 10;
    private View ContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    public TextView txtSpeechInput;
    ListView l;
	View v1;
	int position;
	long id;
	String src ;
	public final int VOICE_RECOGNITION_REQUEST_CODE=100;
	public static String daddress;
	private float X, Y, Z;// defining x,y,z axis
    private boolean signal;// generating a boolean signal variable
    private SensorManager Sensor_Manager;
    private Sensor mAccelerometer; // creating object accelerometer of class sensor 
    private final float NOISE = (float) 10;
    public static int flag=0;
    private static String flag1="default";
    public static int flag_sensor=0;
    PackageManager pm ;
   
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        signal = false;//setting signal as false
   		Sensor_Manager = (SensorManager)getActivity(). getSystemService(Context.SENSOR_SERVICE);
   		mAccelerometer = Sensor_Manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
   	    Sensor_Manager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
   	    
   	   
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ContentView = inflater.inflate(R.layout.device_detail, null);
        txtSpeechInput = (TextView) ContentView.findViewById(R.id.txtview1); 
      
        ContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	final PeerListFragment fragment = (PeerListFragment) getFragmentManager()
                                .findFragmentById(R.id.frame1);
                    	fragment.getView().setVisibility(View.VISIBLE);
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });
        ContentView.findViewById(R.id.go_back).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	final FunctionalityFragment fragment1 = (FunctionalityFragment) getFragmentManager()
                                .findFragmentById(R.id.frame2);
                    	fragment1.getView().setVisibility(View.GONE);
                    	final PeerListFragment fragment = (PeerListFragment) getFragmentManager()
                                .findFragmentById(R.id.frame1);
                    	fragment.getView().setVisibility(View.VISIBLE);
                    	
 
                    }
                });


        ContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	flag1="image";
                    	Log.d("flad value image button ", flag1);
                         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        Log.d("Intent",intent.toString()+"--");
                        
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                        
                        
                    }
                });
        ContentView.findViewById(R.id.music).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	  flag1="music";
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE2);
                      
                    }
                });
            ContentView.findViewById(R.id.voice).setOnClickListener(
	               new View.OnClickListener() {

	                   @Override
	                   public void onClick(View v) {
	                   flag=1;              
	                       Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	                       intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	                       intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak up!");
	                       startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	                 
	                   }
	               });
            ContentView.findViewById(R.id.apk).setOnClickListener(
            		
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                              	  Intent intent = new Intent(getActivity(),ApkActivity.class);
                        	      intent.putExtra("hostinfo",info.groupOwnerAddress.getHostAddress());
                        	  
                        	   startActivity(intent);
                        	  
                        }
                    });
            ContentView.findViewById(R.id.video).setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                        	  flag1="video";
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("video/*");
                            startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE2);
                          
                        }
                    });
            
            
        return ContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    	Log.d("called","called");
        switch (requestCode) {
        case VOICE_RECOGNITION_REQUEST_CODE: {
            if (resultCode == getActivity().RESULT_OK) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtSpeechInput.setText(result.get(0));
                String ga="Gallery";
                String mu="music";
                String app="application";
                String vi="video";
              if(result.get(0).equals(ga))
              {
            	  flag=0;
            	  flag1="image";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                
              }else if(result.get(0).equals(mu))
              {
            	  flag=0;
            	  flag1="music";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE2);
                
              }
              else if(result.get(0).equals(app))
              {
            	  flag1="apk";
            	  flag=0;Intent intent = new Intent(getActivity(),ApkActivity.class);
        	      intent.putExtra("hostinfo",info.groupOwnerAddress.getHostAddress());
            	  
        	      startActivity(intent);
        	  
              } 
              else if(result.get(0).equals(vi))
              {
            	  flag=0;
            	  flag1="video";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE2);
                
              }
              
            }
            break;
        }
        case CHOOSE_FILE_RESULT_CODE:
        {
        	if(resultCode == getActivity().RESULT_OK)
        	{
        	Uri uri = data.getData();
            TextView statusText = (TextView) ContentView.findViewById(R.id.status_text);
            statusText.setText("Sending: " + uri);
            Log.d(WifiDirectActivity.TAG, "Intent----------- " + uri +info.groupOwnerAddress.getHostAddress().toString());
            Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
            serviceIntent.putExtra(FileTransferService.type_file,flag1);
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
            info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8987);
            getActivity().startService(serviceIntent);
        	}
        }
        case CHOOSE_FILE_RESULT_CODE2:
        {
        	if(resultCode == getActivity().RESULT_OK)
        	{
        	Uri uri = data.getData();
            TextView statusText = (TextView) ContentView.findViewById(R.id.status_text);
            statusText.setText("Sending: " + uri);
            Log.d(WifiDirectActivity.TAG, "Intent----------- " + uri +info.groupOwnerAddress.getHostAddress().toString());
            Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
            serviceIntent.putExtra(FileTransferService.type_file,flag1);
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
            info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8987);
            getActivity().startService(serviceIntent);
        	}
        }
    
        }
    
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
       
        TextView view = (TextView) ContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

 
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        if (info.groupFormed && info.isGroupOwner) {

            ContentView.findViewById(R.id.btn_disconnect).setVisibility(View.VISIBLE);
            flag1="default";
            new FileServerAsyncTask(getActivity(), ContentView.findViewById(R.id.status_text))
                    .execute();
        } else if (info.groupFormed) {
  
            ContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ContentView.findViewById(R.id.music).setVisibility(View.VISIBLE);
            ContentView.findViewById(R.id.btn_disconnect).setVisibility(View.VISIBLE);
            ContentView.findViewById(R.id.voice).setVisibility(View.VISIBLE);
            ContentView.findViewById(R.id.apk).setVisibility(View.VISIBLE);
            ContentView.findViewById(R.id.video).setVisibility(View.VISIBLE);
            
            ((TextView) ContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }

        ContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
        ContentView.findViewById(R.id.go_back).setVisibility(View.GONE);
        
    }

    
     public void showDetails(WifiP2pDevice device) {
        this.device = device;
        flag_sensor =1;
        this.getView().setVisibility(View.VISIBLE);
      

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
    
        flag_sensor=0;
    	ContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        ContentView.findViewById(R.id.go_back).setVisibility(View.VISIBLE);
      
        
        TextView view = (TextView) ContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) ContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        ContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        ContentView.findViewById(R.id.btn_disconnect).setVisibility(View.GONE);
        ContentView.findViewById(R.id.music).setVisibility(View.GONE);
        ContentView.findViewById(R.id.voice).setVisibility(View.GONE);
        ContentView.findViewById(R.id.apk).setVisibility(View.GONE);
        ContentView.findViewById(R.id.video).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
        
        
    }
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        public static String typeof;
		private Context context;
        private TextView statusText;
        public static String stringFromServer;
        

        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8987);
                Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WifiDirectActivity.TAG, "Server: connection done");
                File f = null;
                InputStream inputstream = client.getInputStream();
                ObjectInputStream oin = new ObjectInputStream(inputstream);
                try {
					stringFromServer = (String) oin.readObject();
					  Log.d("String from server", stringFromServer);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
      
                if( stringFromServer.equals("image"))
                {
              
             
                    f = new File(Environment.getExternalStorageDirectory() + "/"
                            + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                            + ".jpg");
               }
               if(stringFromServer.equals("music"))
                {

                   f = new File(Environment.getExternalStorageDirectory() + "/"
                            + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                            + ".mp3");
              }
               if(stringFromServer.equals("apk"))
               {

                  f = new File(Environment.getExternalStorageDirectory() + "/"
                           + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                           + ".apk");
             }
               if(stringFromServer.equals("video"))
               {

                  f = new File(Environment.getExternalStorageDirectory() + "/"
                           + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                           + ".mp4");
             }



                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(WifiDirectActivity.TAG, "server: copying files " + f.toString());
                
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(WifiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                if(stringFromServer.equals("image"))
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                if(stringFromServer.equals("music"))
                intent.setDataAndType(Uri.parse("file://" + result), "audio/*");
                if(stringFromServer.equals("apk"))
                    intent.setDataAndType(Uri.parse("file://" + result), "application/vnd.android.package-archive");
                if(stringFromServer.equals("video"))
                    intent.setDataAndType(Uri.parse("file://" + result), "video/*");
                context.startActivity(intent);
            }

        }

        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();
        
        try {
            while ((len = inputStream.read(buf)) != -1) {
            
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is : "+endTime);
            
        } catch (IOException e) {
            Log.d(WifiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
    public void onResume() 
    {
	   super.onResume();
	   Sensor_Manager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
     }
	public void onPause() 
	{
		super.onPause();
		Sensor_Manager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(flag_sensor==1)
		{
		float x = event.values[0];// acceleration force along x-axis
		float y = event.values[1];//acceleration force along y-axis
		float z = event.values[2];// acceleration force along y-axis
		System.out.println("x="+x);
		System.out.println("y="+y);
		System.out.println("z="+z);
		//flag_sensor=0;
		
		if (!signal) // if signal is not true so in very beginning it will initialise X,Y,Z here
		{
			X = x;
			Y = y;
			Z = z;
			signal = true; //set it as true
		}
		else 
		{
			float deltaX = Math.abs(X - x);// finding difference of old x-axis and new x-axis
			float deltaY = Math.abs(Y - y);
			float deltaZ = Math.abs(Z - z);
			if (deltaX < NOISE) deltaX = (float)0.0;// checking if change in new and old axis< 2.0 change it to zero
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			X = x;//updating X
			Y = y;
			Z = z;
			if (deltaY > deltaX || deltaZ > deltaX || deltaZ> deltaY) 
			{
				ContentView.findViewById(R.id.btn_connect);
				
			            WifiP2pConfig config = new WifiP2pConfig();
			              config.deviceAddress = device.deviceAddress;
			              config.wps.setup = WpsInfo.PBC;
			              flag_sensor=0;
			              
			              if (progressDialog != null && progressDialog.isShowing()) {
			                  progressDialog.dismiss();
			              }
			            
			              progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
			                      "Connecting to :" + device.deviceAddress, true, true, 
			                      new DialogInterface.OnCancelListener() {
			                          @Override
			                          public void onCancel(DialogInterface dialog) {
			                              ((DeviceActionListener) getActivity()).cancelDisconnect();
			                          }
			                      });
			              progressDialog.setCancelable(true);
			                
			              ((DeviceActionListener) getActivity()).connect(config);
			           
			}
			   
			else
			{
				ContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
			         @Override
			          public void onClick(View v) {
			              WifiP2pConfig config = new WifiP2pConfig();
			              config.deviceAddress = device.deviceAddress;
			              config.wps.setup = WpsInfo.PBC;
			              flag_sensor=0;
			              if (progressDialog != null && progressDialog.isShowing()) {
			                  progressDialog.dismiss();
			              }
			           
			              progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
			                      "Connecting to :" + device.deviceAddress, true, true,  
			                      new DialogInterface.OnCancelListener() {
			                          @Override
			                          public void onCancel(DialogInterface dialog) {
			                              ((DeviceActionListener) getActivity()).cancelDisconnect();
			                          }
			                      });
			             
			              // perform p2p connect upon user click the connect button, connect available handle when connection done.
			              ((DeviceActionListener) getActivity()).connect(config);
			              
			           
			}
			      });

				
				
			}

					}
		}
		
		}
	

	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}	
	

}
