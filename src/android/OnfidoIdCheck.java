package cordova.plugin.onfido;

import android.widget.Toast;
import android.content.Intent;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.DocumentType;

import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureStep;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariant;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.ui.options.CaptureScreenStep;
import com.onfido.android.sdk.capture.ui.options.MessageScreenStep;

import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.api.client.data.Applicant;

import com.onfido.android.sdk.capture.utils.OnfidoApiUtil;
import com.onfido.android.sdk.capture.utils.CountryCode;

import com.onfido.api.client.OnfidoAPI;
import com.onfido.api.client.data.Address;
import com.onfido.api.client.data.Applicant;
import com.onfido.api.client.data.Check;
import com.onfido.api.client.data.ErrorData;
import com.onfido.api.client.data.Report;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.util.Log;
import android.app.Activity;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class OnfidoIdCheck extends CordovaPlugin {
    private static final String TAG = "MyCordovaPlugin";
    private Onfido client;
    private OnfidoAPI onfidoAPI;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing MyCordovaPlugin");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("startSdk")) {
            // The intent expects as first parameter the given name for the activity in your plugin.xml
            Intent intent = new Intent("cordova.plugin.onfido.DialogShowOnfido");
            // Send some info to the activity to retrieve it later
            //intent.putExtra("app_id", ONEDRIVE_APP_ID);
            //intent.putExtra("link_mode", LINK_MODE);
            // Now, cordova will expect for a result using startActivityForResult and will be handle by the onActivityResult.
            cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
        }

        // Send no result, to execute the callbacks later
        PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true); // Keep callback

        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        /*
        if(resultCode == cordova.getActivity().RESULT_OK){
            Bundle extras = data.getExtras();// Get data sent by the Intent
            String information = extras.getString("data"); // data parameter will be send from the other activity.
            tolog(information); // Shows a toast with the sent information in the other class
            PluginResult resultado = new PluginResult(PluginResult.Status.OK, "this value will be sent to cordova");
            resultado.setKeepCallback(true);
            PUBLIC_CALLBACKS.sendPluginResult(resultado);
            return;
        }else if(resultCode == cordova.getActivity().RESULT_CANCELED){
            PluginResult resultado = new PluginResult(PluginResult.Status.OK, "canceled action, process this in javascript");
            resultado.setKeepCallback(true);
            PUBLIC_CALLBACKS.sendPluginResult(resultado);
            return;
        }
        */

        // Handle other results if exists.
        super.onActivityResult(requestCode, resultCode, data);
    }
}
