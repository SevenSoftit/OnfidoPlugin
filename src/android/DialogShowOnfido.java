package cordova.plugin.onfido;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.onfido.android.sdk.capture.DocumentType;
import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.errors.OnfidoException;
import com.onfido.android.sdk.capture.ui.BaseActivity;
import com.onfido.android.sdk.capture.ui.options.CaptureScreenStep;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.ui.options.MessageScreenStep;
import com.onfido.android.sdk.capture.upload.Captures;
import com.onfido.android.sdk.capture.utils.CountryCode;
import com.onfido.api.client.data.Applicant;

import org.json.JSONException;
import org.json.JSONObject;

import javax.tools.Diagnostic;

public class DialogShowOnfido extends BaseActivity {

    private Onfido client;
    private String applicantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OnfidoFactory.create(this).getClient();
        setWelcomeScreen();
    }

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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
                showToast("onError");
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
                    client.startActivityForResult(DialogShowOnfido.this, 1, onfidoConfig);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
            }
        });
    }

    private void createApplicant(JSONObjectRequestListener listener) {
        try {

            /*
            $ curl https://api.onfido.com/v2/applicants \
              -H 'Authorization: Token token=YOUR_API_TOKEN' \
              -d 'first_name=Theresa' \
              -d 'last_name=May'
             */
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
            applicant.put("reports","['name': 'document']");
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
}

