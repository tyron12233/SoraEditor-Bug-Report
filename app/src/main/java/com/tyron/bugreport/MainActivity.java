package com.tyron.bugreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.theme.TextMateColorScheme;
import io.github.rosemoe.sora.textmate.core.internal.theme.reader.ThemeReader;
import io.github.rosemoe.sora.textmate.core.theme.IRawTheme;
import io.github.rosemoe.sora.widget.CodeEditor;

public class MainActivity extends AppCompatActivity {

    //language=JAVA
    private static final String DUMMY_TEXT = "package test;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        \n" +
            "    }\n" +
            "}";
    private static final String COLOR_SCHEME_NAME = "darcula.json";
    private static final String COLOR_SCHEME_PATH = "textmate/" + COLOR_SCHEME_NAME;
    private static final String GRAMMAR_NAME = "java.tmLanguage.json";
    private static final String GRAMMAR_PATH = "textmate/java/syntaxes/" + GRAMMAR_NAME;

    private static final ExecutorService mService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CodeEditor codeEditor = new CodeEditor(this);

        mService.submit(() -> {
            try (InputStream is = getAssets().open(COLOR_SCHEME_PATH)) {
                final IRawTheme rawTheme = ThemeReader.readThemeSync(COLOR_SCHEME_NAME, is);
                final TextMateColorScheme scheme = TextMateColorScheme.create(rawTheme);
                Language language;
                try (InputStream grammarIs = getAssets().open(GRAMMAR_PATH)) {
                    language = TextMateLanguage.create(GRAMMAR_NAME, grammarIs, rawTheme);
                }

                runOnUiThread(() -> {
                    codeEditor.setColorScheme(scheme);
                    codeEditor.setEditorLanguage(language);
                    codeEditor.setText(DUMMY_TEXT);
                });
            } catch (Exception e) {
                throw new RuntimeException(Log.getStackTraceString(e));
            }
        });
        setContentView(codeEditor);
        codeEditor.setText(DUMMY_TEXT);
    }
}