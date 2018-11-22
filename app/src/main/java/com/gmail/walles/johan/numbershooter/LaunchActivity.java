/*
 * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.walles.johan.numbershooter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;

import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

import timber.log.Timber;

public class LaunchActivity extends MusicActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    private Button startMultiplicationButton;
    private Button startAdditionButton;

    @Override
    protected void onResume() {
        super.onResume();

        PlayerStateV2 playerState;
        try {
            playerState = PlayerStateV2.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get player state", e);
        }

        configureButton(startAdditionButton, playerState, GameType.ADDITION);
        configureButton(startMultiplicationButton, playerState, GameType.MULTIPLICATION);
    }

    private void configureButton(Button button, PlayerStateV2 playerState, GameType gameType) {
        // FIXME: Get format string from a resource
        // FIXME: Make the first char bigger
        button.setText(String.format(Locale.getDefault(), "%s\nLevel %d",
                gameType.buttonLabel, playerState.getLevel(gameType)));

        int startLevel = playerState.getLevel(gameType);
        button.setOnClickListener(v -> GameActivity.start(this,
                gameType, startLevel));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getDelegate().getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.credits) {
            LaunchActivity.this.showAboutDialog();
            return true;
        }

        if (item.getItemId() == R.id.view_source_code) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            @NonNls String uri = "https://github.com/walles/numbervaders?files=1";
            intent.setData(Uri.parse(uri));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings({"resource", "IOResourceOpenedButNotSafelyClosed"})
    private void showAboutDialog() {
        String credits;
        try (InputStream inputStream = getResources().openRawResource(R.raw.credits)) {
            @NonNls final String BEGINNING_OF_INPUT = "\\A";
            credits = new Scanner(inputStream, StandardCharsets.UTF_8.name())
                    .useDelimiter(BEGINNING_OF_INPUT)
                    .next();
        } catch (IOException e) {
            Timber.e(e, "Unable to load credits resource");
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(credits)
                .setCancelable(true)
                .setTitle(R.string.credits)
                .setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startMultiplicationButton = findViewById(R.id.startMultiplicationButton);
        assert startMultiplicationButton != null;

        startAdditionButton = findViewById(R.id.startAdditionButton);
        assert startAdditionButton != null;
    }
}
