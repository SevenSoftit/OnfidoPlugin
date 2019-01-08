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
    private static final String TAG = "OnfidoIdCheck";

    private Onfido client;
    private OnfidoAPI onfidoAPI;
    private Activity context;
    private String applicantId;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing MyCordovaPlugin");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("startSdk")) {

            setWelcomeScreen();
            /*
            context = this.cordova.getActivity();
            client = OnfidoFactory.create(context).getClient();

            final FlowStep[] defaultStepsWithWelcomeScreen = new FlowStep[]{
                    new MessageScreenStep("Welcome", "In the following steps you will be asked to perform a verification check", "Start"),//FlowStep.WELCOME,
                    new CaptureScreenStep(DocumentType.PASSPORT, CountryCode.SV),
                    FlowStep.CAPTURE_FACE,
                    new MessageScreenStep("Thank you", "We will use your captured document and face to perform a verification check", "Start Check")
            };

            createApplicant(new JSONObjectRequestListener() {
                //  showToast("createApplicant");

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String applicantId = response.getString("id");
                        showToast("onResponse");
                        showToast(applicantId);
                        OnfidoConfig onfidoConfig = OnfidoConfig.builder()
                                .withCustomFlow(defaultStepsWithWelcomeScreen)
                                .withToken("test_iCPCbZOQv01rBCSZ5xZt65JaqMj_et76")
                                .withApplicant(applicantId)
                                .build();

                        onfidoAPI = OnfidoApiUtil.createOnfidoApiClient(context, onfidoConfig);

                        client.startActivityForResult(context, 1, onfidoConfig);

                        // An example of returning data back to the web layer
                        final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
                        callbackContext.sendPluginResult(result);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("onResponse Unknown error");
                    }
                }

                @Override
                public void onError(ANError anError) {
                }
            });
            */
        }
        return true;
    }

    private void startCheck(Applicant applicant) {
        //Call your back end to initiate the check
        //completedCheck();
        completedCheck(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    showToast("onResponse");
                    String v = response.toString();
                    applicantId = response.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                ANError v = anError;
                showToast("onError startCheck");
            }
        });
    }

    private void setWelcomeScreen() {
        //setContentView(R.layout.activity_main);

        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                new MessageScreenStep("Welcome", "In the following steps you will be asked to perform a verification check", "Start"),
                new CaptureScreenStep(DocumentType.PASSPORT, CountryCode.SV),
                FlowStep.CAPTURE_FACE,
                new MessageScreenStep("Thank you", "We will use your captured document and face to perform a verification check", "Start Check")
        };

        startFlow(flowStepsWithOptions);
    }

    private void startFlow(final FlowStep[] flowSteps) {
        createApplicant(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    applicantId = response.getString("id");

                    OnfidoConfig.Builder onfidoConfigBuilder = OnfidoConfig.builder().withApplicant(applicantId).withToken("test_iCPCbZOQv01rBCSZ5xZt65JaqMj_et76");

                    if (flowSteps != null) {
                        onfidoConfigBuilder.withCustomFlow(flowSteps);
                    }

                    OnfidoConfig onfidoConfig = onfidoConfigBuilder.build();
                    client.startActivityForResult(context, 1, onfidoConfig);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                showToast("onError startFlow");
            }
        });
    }

    private void createApplicant(JSONObjectRequestListener listener) {
        try {
            String token = "test_BCoZn8ZVcYtYhTAq77Tt2h9u0I9OX75R";//getString(R.string.onfido_api_token);
            final JSONObject applicant = new JSONObject();
            applicant.put("first_name", "Theresa");
            applicant.put("last_name", "May");

            AndroidNetworking.post("https://api.onfido.com/v2/applicants")
                    .addJSONObjectBody(applicant)
                    .addHeaders("Accept", "application/json")
                    .addHeaders("Authorization", "Token token=" + token)
                    .build()
                    .getAsJSONObject(listener);

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Unknown error");
        }
    }

    private void completedCheck(JSONObjectRequestListener listener) {
        try {
            /*
            $ curl https://api.onfido.com/v2/applicants/YOUR_APPLICANT_ID/checks \
            -H 'Authorization: Token token=YOUR_API_TOKEN' \
            -d 'type=express' \
            -d 'reports[][name]=document' \
            -d 'reports[][name]=facial_similarity' \
            -d 'reports[][variant]=standard'
            */
            //showToast("completeCheck");

            String token = "test_BCoZn8ZVcYtYhTAq77Tt2h9u0I9OX75R";//getString(R.string.onfido_api_token);
            final JSONObject applicant = new JSONObject();
            applicant.put("type", "express");
            applicant.put("reports", "['name': 'document']");
            //applicant.put("reports", "['name': 'facial_similarity'']");
            applicant.put("reports", "['variant': 'standard']");

            AndroidNetworking.post("https://api.onfido.com/v2/applicants/" + this.applicantId + "/checks")
                    .addJSONObjectBody(applicant)
                    .addHeaders("Accept", "application/json")
                    .addHeaders("Authorization", "Token token=" + token)
                    .build()
                    .getAsJSONObject(listener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.handleActivityResult(resultCode, data, new Onfido.OnfidoResultListener() {
            @Override
            public void userCompleted(Applicant applicant, Captures captures) {
                startCheck(applicant);
            }

            @Override
            public void userExited(ExitCode exitCode, Applicant applicant) {
                showToast("User cancelled.");
            }

            @Override
            public void onError(OnfidoException e, Applicant applicant) {
                e.printStackTrace();
                showToast("Unknown error");
            }
        });
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


    */
}
