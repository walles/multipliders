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
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gmail.walles.johan.numbershooter.Medal;
import com.gmail.walles.johan.numbershooter.Medals;
import com.gmail.walles.johan.numbershooter.MedalsAdapter;
import com.gmail.walles.johan.numbershooter.R;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV3;
import java.io.IOException;
import java.util.List;

public class MedalsActivity extends MusicActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, MedalsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        PlayerStateV3 playerState;
        try {
            playerState = PlayerStateV3.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get player state", e);
        }

        List<Medal> medals = Medals.get(getResources(), playerState);
        if (medals.isEmpty()) {
            TextView textView = findViewById(R.id.textView);
            textView.setText(R.string.no_medals_yet);
        }

        /*
        // NOTE: Re-enable these to test various kinds of medals
        medals.add(0, new Medal(Medal.Flavor.GOLD, "Test: Gold medal"));
        medals.add(
                1,
                new Medal(
                        Medal.Flavor.SILVER,
                        "Test: Silver medal with a lot of text so that the text in the description has to wrap and fill many lines"));
        medals.add(2, new Medal(Medal.Flavor.BRONZE, "Test: Bronze medal"));
        */

        int medalSize = 2 * getResources().getDimensionPixelSize(R.dimen.big_text_size);

        RecyclerView medalsList = findViewById(R.id.medalsList);
        medalsList.setLayoutManager(new LinearLayoutManager(this));
        medalsList.setAdapter(new MedalsAdapter(this, medalSize, medals));

        // Provide user with a way to go back to the launch screen
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            LaunchActivity.start(this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
