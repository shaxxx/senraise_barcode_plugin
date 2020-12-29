package hr.integrator.senraise_barcode_plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** SenraiseBarcodePlugin */
public class SenraiseBarcodePlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {

  private MethodChannel channel;
  private Context applicationContext;
  private BroadcastReceiver broadcastReceiver;
  private MethodChannel methodChannel;
  private EventChannel eventChannel;
  private final String TAG = SenraiseBarcodePlugin.class.getSimpleName();
  private final String BARCODE_SCANNED_ACTION = "android.scanner.scan";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.applicationContext = applicationContext;
    methodChannel = new MethodChannel(messenger, "senraise_barcode_plugin/methods");
    eventChannel = new EventChannel(messenger, "senraise_barcode_plugin/events");
    eventChannel.setStreamHandler(this);
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private BroadcastReceiver createBarcodeScannedReceiver(final EventChannel.EventSink events) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
          if (events != null) {
            HashMap<String, Object> msg = new HashMap<String,Object>();
            msg.put("barcode", intent.getStringExtra("result"));
            msg.put("event", "barcode_scanned");
            events.success(msg);
          }
      }
    };
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    broadcastReceiver = createBarcodeScannedReceiver(events);
    IntentFilter filter = new IntentFilter();
    filter.addAction(BARCODE_SCANNED_ACTION);
    applicationContext.registerReceiver(broadcastReceiver, filter);
  }

  @Override
  public void onCancel(Object arguments) {
    applicationContext.unregisterReceiver(broadcastReceiver);
    broadcastReceiver = null;
  }

}