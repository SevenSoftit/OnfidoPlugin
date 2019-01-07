package cordova.plugin.onfido;

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
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureStep;
import com.onfido.android.sdk.capture.ui.camera.face.FaceCaptureVariant;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.android.sdk.capture.utils.OnfidoApiUtil;
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

public class OnfidoIdCheck extends CordovaPlugin {
    private static final String TAG = "OnfidoIdCheck";

    private Onfido client;
    private OnfidoAPI onfidoAPI;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing MyCordovaPlugin");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("startSdk")) {
            Activity context = this.cordova.getActivity();
            client = OnfidoFactory.create(context).getClient();

            final FlowStep[] defaultStepsWithWelcomeScreen = new FlowStep[]{
                    FlowStep.WELCOME,
                    new CaptureScreenStep(DocumentType.NATIONAL_IDENTITY_CARD, CountryCode.GB),//FlowStep.CAPTURE_DOCUMENT,
                    FlowStep.CAPTURE_FACE,
                    FlowStep.FINAL
            };

            OnfidoConfig onfidoConfig = OnfidoConfig.builder()
                    .withCustomFlow(defaultStepsWithWelcomeScreen)
                    .withToken("YOUR_MOBILE_TOKEN")
                    .withApplicant("YOUR_APPLICANT_ID")
                    .build();

            onfidoAPI = OnfidoApiUtil.createOnfidoApiClient(context, onfidoConfig);

            client.startActivityForResult(context, 1, onfidoConfig);

            // An example of returning data back to the web layer
            final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
            callbackContext.sendPluginResult(result);
        }
        return true;
    }
/*
    private Applicant getTestApplicant() {
        return Applicant.builder()
                .withFirstName("Ionic")
                .withLastName("User")
                .withToken("YOUR_MOBILE_TOKEN")
                .build();
    }

    private OnfidoConfig.Builder getTestOnfidoConfigBuilder() {
        return OnfidoConfig.builder()
                .withApplicant(getTestApplicant());
    }
/*
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("add")) {
            this.add(args, callbackContext);
            return true;
        } else if (action.equals("multiply")) {
            this.multiply(args, callbackContext);
            return true;
        } else if (action.equals("divide")) {
            this.divide(args, callbackContext);
            return true;
        } else if (action.equals("substract")) {
            this.substract(args, callbackContext);
            return true;
        } else if (action.equals("startSdk")) {
            final Activity context = this.cordova.getActivity();
            client = OnfidoFactory.create(context).getCliente();
            this.startSdk(args, callbackContext);
            return true;
        }
        return false;
    }

    private void add(JSONArray args, CallbackContext callbackContext) {
        if (args != null) {
            try {
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callbackContext.success("" + (p1 + p2));
            } catch (Exception e) {
                callbackContext.error("Invalid add operation");
            }

        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void multiply(JSONArray args, CallbackContext callbackContext) {
        if (args != null) {
            try {
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callbackContext.success("" + (p1 * p2));
            } catch (Exception e) {
                callbackContext.error("Invalid multiply operation");
            }

        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void substract(JSONArray args, CallbackContext callbackContext) {
        if (args != null) {
            try {
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callbackContext.success("" + (p1 - p2));
            } catch (Exception e) {
                callbackContext.error("Invalid substract operation");
            }

        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void divide(JSONArray args, CallbackContext callbackContext) {
        if (args != null) {
            try {
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callbackContext.success("" + (p1 / p2));
            } catch (Exception e) {
                callbackContext.error("Invalid divide operation");
            }

        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void startSdk(JSONArray args, CallbackContext callbackContext) {
        try {


            final FlowStep[] defaultStepsWithWelcomeScreen = new FlowStep[]{
                    FlowStep.WELCOME,
                    FlowStep.CAPTURE_DOCUMENT,
                    FlowStep.CAPTURE_FACE,
                    FlowStep.FINAL
            };

            OnfidoConfig onfidoConfig = OnfidoConfig.builder()
                    .withToken("YOUR_MOBILE_TOKEN")
                    .withApplicant("YOUR_APPLICANT_ID")
                    .build();

            onfidoAPI = OnfidoApiUtil.createOnfidoApiClient(context, onfidoConfig);

            client.startActivityForResult(context, 1, onfidoConfig);

            // An example of returning data back to the web layer
            final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
            callbackContext.sendPluginResult(result);
        }
        catch (Exception e) {
            callbackContext.error("Invalid divide operation");
        }
    }
    /*
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing OnfidoIdCheck");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("startSdk")) {
            Activity context=this.cordova.getActivity();
            client = OnfidoFactory.create(context).getClient();

            final FlowStep[] defaultStepsWithWelcomeScreen = new FlowStep[]{
                    FlowStep.WELCOME,
                    FlowStep.CAPTURE_DOCUMENT,
                    FlowStep.CAPTURE_FACE,
                    FlowStep.FINAL
            };

            OnfidoConfig onfidoConfig = OnfidoConfig.builder()
                    .withToken("YOUR_MOBILE_TOKEN")
                    .withApplicant("YOUR_APPLICANT_ID")
                    .build();

            onfidoAPI = OnfidoApiUtil.createOnfidoApiClient(context, onfidoConfig);

            client.startActivityForResult(context, 1, onfidoConfig);

            // An example of returning data back to the web layer
            final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
            callbackContext.sendPluginResult(result);
        }
        return true;
    }
    */
}
