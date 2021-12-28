package com.example.hello_plugin_package_example;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;

public class MainActivity extends FlutterActivity {
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        flutterEngine
                .getPlatformViewsController()
                .getRegistry()
                .registerViewFactory("plugins.felix.angelov/textview", new FlutterTextViewFactory(flutterEngine.getDartExecutor().getBinaryMessenger()));
    }
}
