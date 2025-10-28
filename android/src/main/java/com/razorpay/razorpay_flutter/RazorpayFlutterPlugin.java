package com.razorpay.razorpay_flutter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * RazorpayFlutterPlugin — migrated to Flutter embedding v2
 */
public class RazorpayFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private static final String CHANNEL_NAME = "razorpay_flutter";

    private MethodChannel channel;
    private RazorpayDelegate razorpayDelegate;
    private Activity activity;
    private Context context;

    public RazorpayFlutterPlugin() {
        // Default constructor
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        context = binding.getApplicationContext();
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
        context = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (razorpayDelegate == null) {
            result.error("NO_ACTIVITY", "Plugin not attached to an Activity", null);
            return;
        }

        switch (call.method) {
            case "open":
                razorpayDelegate.openCheckout((Map<String, Object>) call.arguments, result);
                break;
            case "resync":
                razorpayDelegate.resync(result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    // ------------------ ActivityAware methods ------------------

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        razorpayDelegate = new RazorpayDelegate(activity);
        binding.addActivityResultListener(razorpayDelegate);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (razorpayDelegate != null && activity != null) {
            // No manual removal needed; handled by Flutter automatically
            razorpayDelegate = null;
        }
        activity = null;
    }
}
