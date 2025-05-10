/**
 * LocaleHelper simplifies the process of launching a language selection screen (LanguageActivity)
 * and automatically handles locale changes by recreating the calling activity if a new locale is selected.
 *
 * <p>Instead of repeatedly writing boilerplate code for launching LanguageActivity and handling its result
 * in each activity, this class abstracts the logic into a reusable utility.
 *
 * <p>It uses Android's Activity Result API (`registerForActivityResult`) to register a launcher during
 * construction. When the locale is changed in LanguageActivity and it returns with RESULT_OK and an intent extra
 * `locale_changed = true`, the associated activity will automatically be recreated.
 *
 * <h2>How to Use</h2>
 *
 * <pre>{@code
 * public class MainActivity extends AppCompatActivity {
 *
 *     private LocaleHelper localeHelper;
 *
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *
 *         // Initialize the helper (do this once per activity where language change is allowed)
 *         localeHelper = new LocaleHelper(this);
 *
 *         // Attach to a button or any click listener
 *         findViewById(R.id.btn_change_language).setOnClickListener(v -> {
 *             localeHelper.showLanguageActivity();
 *         });
 *     }
 * }
 * }</pre>
 */

package com.example.smishingdetectionapp.ui.locale;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


public class LocaleHelper {

    private final Activity activity;
    private final ActivityResultLauncher<Intent> launcher;

    public LocaleHelper(ComponentActivity activity) {
        this.activity = activity;

        launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("locale_changed", false)) {
                            activity.recreate();
                        }
                    }
                }
        );
    }

    public void showLanguageActivity() {
        Intent intent = new Intent(activity, LanguageActivity.class);
        launcher.launch(intent);
    }
}
