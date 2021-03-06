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

package com.gmail.walles.johan.numbershooter.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gmail.walles.johan.numbershooter.GameType;
import com.gmail.walles.johan.numbershooter.Medal;
import com.gmail.walles.johan.numbershooter.Medals;
import com.gmail.walles.johan.numbershooter.MedalsAdapter;
import com.gmail.walles.johan.numbershooter.ObjectiveSoundPool;
import com.gmail.walles.johan.numbershooter.R;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV3;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NonNls;

public class LevelClearedActivity extends MusicActivity {
    private GameType gameType;
    private int clearedLevel;

    private ObjectiveSoundPool soundPool;
    private ObjectiveSoundPool.SoundEffect tada;

    @NonNls private static final String GAME_TYPE_EXTRA = "gameType";
    @NonNls private static final String LEVEL_EXTRA = "clearedLevel";

    public static void start(Context context, GameType gameType, int level) {
        Intent intent = new Intent(context, LevelClearedActivity.class);
        intent.putExtra(GAME_TYPE_EXTRA, gameType.toString());
        intent.putExtra(LEVEL_EXTRA, level);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        soundPool = new ObjectiveSoundPool();
        tada = soundPool.load(this, R.raw.medal_earned_tada, "Medal-earned Tada!");

        gameType = GameType.valueOf(getIntent().getStringExtra(GAME_TYPE_EXTRA));
        clearedLevel = getIntent().getIntExtra(LEVEL_EXTRA, 0);
        if (clearedLevel <= 0) {
            throw new RuntimeException("Level not found: " + getIntent());
        }

        setContentView(R.layout.activity_level_cleared);

        TextView textView = findViewById(R.id.level_cleared_text);
        textView.setText(getString(R.string.level_n_cleared, clearedLevel));

        Button button = findViewById(R.id.next_level_button);
        button.setText(getString(R.string.level_n, clearedLevel + 1));
        button.setOnClickListener(
                v -> {
                    GameActivity.start(this, gameType, clearedLevel + 1);
                    finish();
                });

        listMedals();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        soundPool.close();
    }

    private void listMedals() {
        PlayerStateV3 playerState;
        try {
            playerState = PlayerStateV3.fromContext(this);

            if (playerState.medalsAlreadyAwarded(gameType)) {
                // Medals already awarded for this level, never mind
                return;
            }
            playerState.setMedalsAwarded(gameType);
        } catch (IOException e) {
            throw new RuntimeException("Accessing player state before listing medals failed", e);
        }

        List<Medal> medalsEarned = Medals.getLatest(getResources(), playerState, gameType);

        /*
        // NOTE: Test code, re-enable to get some medals after finishing a level
        medalsEarned.add(new Medal(Medal.Flavor.GOLD, "Test medal 1, gold"));
        medalsEarned.add(new Medal(Medal.Flavor.SILVER, "Test medal 2, silver"));
        medalsEarned.add(new Medal(Medal.Flavor.BRONZE, "Test medal 3, bronze"));
        */

        if (medalsEarned.isEmpty()) {
            return;
        }

        RecyclerView medalsList = findViewById(R.id.medalsList);
        medalsList.setVisibility(View.VISIBLE);

        int medalSize = 2 * getResources().getDimensionPixelSize(R.dimen.big_text_size);
        medalsList.setLayoutManager(new LinearLayoutManager(this));
        medalsList.setAdapter(new MedalsAdapter(this, medalSize, medalsEarned));

        showEarnedMedalDialog(medalsEarned.iterator());
    }

    private void showEarnedMedalDialog(Iterator<Medal> medalsIter) {
        if (!medalsIter.hasNext()) {
            return;
        }
        Medal medal = medalsIter.next();

        tada.play();

        Drawable medalDrawable = getResources().getDrawable(R.drawable.medal, null);
        medalDrawable.setColorFilter(medal.flavor.getColor(), PorterDuff.Mode.SRC_ATOP);

        new AlertDialog.Builder(this)
                .setMessage(medal.getDescription())
                .setNeutralButton(
                        R.string.ok,
                        (dialog, which) -> {
                            dialog.dismiss();
                            showEarnedMedalDialog(medalsIter);
                        })
                .setTitle(R.string.you_received_a_medal)
                .setIcon(medalDrawable)
                .show();
    }
}
