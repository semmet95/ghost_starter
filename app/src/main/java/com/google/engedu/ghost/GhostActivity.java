/* Copyright 2016 Google Inc.
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

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    private TextView ghostText, gameStatus, meScore, youScore;
    private String word_fragment="";
    private int me_score=0, you_score=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        try {
            dictionary=new FastDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ghostText=(TextView)findViewById(R.id.ghostText);
        gameStatus=(TextView)findViewById(R.id.gameStatus);
        meScore=(TextView)findViewById(R.id.meScore);
        youScore=(TextView)findViewById(R.id.youScore);
        if(savedInstanceState!=null) {
            word_fragment=savedInstanceState.getString("word_fragment");
            ghostText.setText(word_fragment);
            gameStatus.setText(savedInstanceState.getString("game_status"));
            userTurn=savedInstanceState.getBoolean("userTurn");
            me_score=savedInstanceState.getInt("me_score");
            meScore.setText(""+me_score);
            you_score=savedInstanceState.getInt("you_score");
            youScore.setText(""+you_score);
            //Log.e("retrieving :", "the values with word_fragment = "+word_fragment);
        } else
            onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        word_fragment="";
        ghostText.setText(word_fragment);
        //SimpleDictionary.validWords.clear();
        if (userTurn) {
            gameStatus.setText(USER_TURN);
        } else {
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again
        if(word_fragment.length()>3&&dictionary.isWord(word_fragment)) {
            gameStatus.setText("you got rekt");
            ++me_score;
            meScore.setText(""+me_score);
            return;
        } else {
            String temp=dictionary.getGoodWordStartingWith(word_fragment);
            if(temp==null) {
                gameStatus.setText("you got rekt");
                ++me_score;
                meScore.setText(""+me_score);
                return;
            } else {
                word_fragment=temp.substring(0, word_fragment.length()+1);
                ghostText.setText(word_fragment);
            }
        }
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        userTurn = true;
        gameStatus.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        if(keyCode<KeyEvent.KEYCODE_A||keyCode>KeyEvent.KEYCODE_Z) {
            Log.e("key pressed :", "not a valid key");
            return super.onKeyUp(keyCode, event);
        }
        word_fragment=ghostText.getText().toString()+(char)event.getUnicodeChar();
        Log.e("key pressed :", "the word is "+word_fragment);
        ghostText.setText(word_fragment);
        computerTurn();
        /*if(dictionary.isWord(word_fragment)) {
            gameStatus.setText("it's a word");
            //computerTurn();
        }
        else
            gameStatus.setText("it's not a word");*/
        return true;
    }

    public void onChallenge(View v) {
        word_fragment=ghostText.getText().toString();
        if(word_fragment.length()>3&&dictionary.isWord(word_fragment)) {
            gameStatus.setText("How could I lose!");
            ++you_score;
            youScore.setText(""+you_score);
        }
        else {
            String temp=dictionary.getGoodWordStartingWith(word_fragment);
            if(temp==null) {
                gameStatus.setText("How could I lose!");
                ++you_score;
                youScore.setText(""+you_score);
            }
            else {
                gameStatus.setText("you got rekt");
                ghostText.setText(dictionary.getFullWordFrom(word_fragment));
                ++me_score;
                meScore.setText(""+me_score);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Log.e("saving :", "just entered");
        super.onSaveInstanceState(outState);
        //Log.e("saving :", "the values");
        outState.putString("word_fragment", word_fragment);
        outState.putString("game_status", gameStatus.getText().toString());
        outState.putBoolean("userTurn", userTurn);
        outState.putInt("me_score", me_score);
        outState.putInt("you_score", you_score);
    }
}
