package com.example.smishingdetectionapp.ui.locale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;
import com.localazy.android.Localazy;
import com.localazy.android.LocalazyLocale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        ListView listView = findViewById(R.id.language_list);
        List<LocalazyLocale> locales = Localazy.getLocales();//Obj[0:{language='de', country='null', script='null', name='German', localizedName='Deutsch', complete=true},1:{language='es', country='null', script='null', name='Spanish', localizedName='Español', complete=true}]

        if (locales == null || locales.isEmpty()) {
            Log.d("error", "onCreate: localazy is not correctly. search this log text");
            // clean build
            // delete res/values/strings_localazy directory and files
            // comment  afterEvaluate { tasks.named("localazyDownloadStringsDebug") { dependsOn("uploadStrings") } } in build.gradle.kts(:app)
            // sync gradle
            // run ./gradlew uploadStrings
            // Run 'app'
            finish();
            return;
        }

        //created language names array
        List<String> languageNames = new ArrayList<>();
        for (LocalazyLocale locale : locales) {
            //add language names one by one in to language name array
            languageNames.add(locale.getLocalizedName());//[0:"English"] <- "Español" = [0:"English",1:"Español"] //
        }

        //add local array to adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languageNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            LocalazyLocale selectedLocale = locales.get(position);//{language='de', country='null', script='null', name='German', localizedName='Deutsch', complete=true}
            Locale locale = new Locale(selectedLocale.getLanguage());//"de"

            //set local
            Localazy.forceLocale(locale, true);

            //Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);

            //Go back with status_success and message
            Intent resultIntent = new Intent();
            resultIntent.putExtra("locale_changed", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}