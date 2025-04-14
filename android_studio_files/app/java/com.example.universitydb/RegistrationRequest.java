package com.example.universitydb;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistrationRequest extends StringRequest {
    private Map<String, String> params;
    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("RegistrationRequest", "Error: " + error.getMessage());
        }
    };

    public RegistrationRequest(String studentId, String name, String email, String password,
                               String department, String studentType, String url,
                               Response.Listener<String> listener) {
        super(Method.POST, url, listener, errorListener);
        params = new HashMap<>();
        params.put("student_id", studentId);
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("department", department);
        params.put("student_type", studentType);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}